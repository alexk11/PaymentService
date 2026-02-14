package com.iprody.adapter.checkstate;

import com.iprody.adapter.checkstate.handler.PaymentStatusCheckHandler;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class PaymentStateCheckListener {

    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;
    private final String routingKey;
    private final String dlxExchangeName;
    private final String dlxRoutingKey;
    private final PaymentStatusCheckHandler paymentStatusCheckHandler;

    @Value("${spring.app.rabbitmq.max-retries:60}")
    private int maxRetries;

    @Value("${spring.app.rabbitmq.interval-ms:60000}")
    private long intervalMs;

    @Autowired
    public PaymentStateCheckListener(
            RabbitTemplate rabbitTemplate,
            @Value("${spring.app.rabbitmq.delayed-exchange-name}") String exchangeName,
            @Value("${spring.app.rabbitmq.queue-name}") String routingKey,
            @Value("${spring.app.rabbitmq.dlx-exchange-name}") String dlxExchangeName,
            @Value("${spring.app.rabbitmq.dlx-routing-key}") String dlxRoutingKey,
            PaymentStatusCheckHandler paymentStatusCheckHandler) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
        this.dlxExchangeName = dlxExchangeName;
        this.dlxRoutingKey = dlxRoutingKey;
        this.paymentStatusCheckHandler = paymentStatusCheckHandler;
    }

    @RabbitListener(queues = "${spring.app.rabbitmq.queue-name}")
    public void handle(PaymentCheckStateMessage message, Message raw) {
        MessageProperties props = raw.getMessageProperties();
        int retryCount = (int)props.getHeaders().getOrDefault("x-retry-count", 0);
        boolean paid = paymentStatusCheckHandler.handle(message.getChargeGuid());
        if (paid) {
            return;
        }
        if (retryCount < maxRetries) {
        // Планируем следующую проверку
            PaymentCheckStateMessage newMessage = new PaymentCheckStateMessage(
                    message.getChargeGuid(),
                    message.getPaymentGuid(),
                    message.getAmount(),
                    message.getCurrency()
            );
            rabbitTemplate.convertAndSend(
                    exchangeName,
                    routingKey,
                    newMessage,
                    m -> {
                        m.getMessageProperties().setHeader("x-delay", intervalMs);
                        m.getMessageProperties().setHeader("x-retry-count", retryCount + 1);
                        return m;
                    }
            );
        } else {
    // Исчерпали попытки -- кладём сообщение в DLX
            rabbitTemplate.convertAndSend(
                    dlxExchangeName,
                    dlxRoutingKey,
                    message,
                    m -> {
                        m.getMessageProperties().setHeader("x-retry-count", retryCount);
                        m.getMessageProperties().setHeader("x-final-status", "TIMEOUT");
                        m.getMessageProperties().setHeader("x-original-queue", props.getConsumerQueue());
                        return m;
                    }
            );
        }
    }

}
