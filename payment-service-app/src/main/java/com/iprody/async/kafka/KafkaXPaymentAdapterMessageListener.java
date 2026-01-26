package com.iprody.async.kafka;

import com.iprody.api.AsyncListener;
import com.iprody.api.dto.XPaymentAdapterResponseMessage;
import com.iprody.async.handler.MessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaXPaymentAdapterMessageListener implements AsyncListener<XPaymentAdapterResponseMessage> {

    private final MessageHandler<XPaymentAdapterResponseMessage> handler;

    @Override
    public void onMessage(XPaymentAdapterResponseMessage message) {
        handler.handle(message);
    }

    @KafkaListener(
            topics = "${spring.app.kafka.topics.xpayment-adapter.response-topic}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(XPaymentAdapterResponseMessage message,
                        ConsumerRecord<String, XPaymentAdapterResponseMessage> record,
                        Acknowledgment acknowledgment) {
        try {
            log.info("Received XPayment Adapter response: paymentGuid = {}, status = {}, partition = {}, offset = {}",
                    message.getPaymentGuid(),
                    message.getStatus(),
                    record.partition(),
                    record.offset());
            onMessage(message);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error handling XPayment Adapter response for paymentGuid = {}", message.getPaymentGuid(), e);
            throw e;
        }
    }

}
