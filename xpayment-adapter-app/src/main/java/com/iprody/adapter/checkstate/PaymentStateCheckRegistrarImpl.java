package com.iprody.adapter.checkstate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;


@Service
@Slf4j
public class PaymentStateCheckRegistrarImpl implements PaymentStateCheckRegistrar {

    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;
    private final String routingKey;

    @Value("${spring.app.rabbitmq.max-retries:60}")
    private int maxRetries;

    @Value("${spring.app.rabbitmq.interval-ms:60000}")
    private long intervalMs;

    @Autowired
    public PaymentStateCheckRegistrarImpl(
            RabbitTemplate rabbitTemplate,
            @Value("${spring.app.rabbitmq.delayed-exchange-name}") String exchangeName,
            @Value("${spring.app.rabbitmq.queue-name}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
    }

    @Override
    public void register(
            UUID chargeGuid,
            UUID paymentGuid,
            BigDecimal amount,
            String currency) {

        log.info("Registering message with RabbitMQ, id={}", paymentGuid);

        PaymentCheckStateMessage message = new PaymentCheckStateMessage(
                chargeGuid,
                paymentGuid,
                amount,
                currency
        );

        rabbitTemplate.convertAndSend(
                exchangeName,
                routingKey,
                message,
                m -> {
                    m.getMessageProperties().setHeader("x-delay", intervalMs);
                    m.getMessageProperties().setHeader("x-retry-count", 1);
                    return m;
                }
        );
    }

}
