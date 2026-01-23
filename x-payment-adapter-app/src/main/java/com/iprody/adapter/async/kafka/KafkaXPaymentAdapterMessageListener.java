package com.iprody.adapter.async.kafka;

import com.iprody.adapter.async.handler.MessageHandler;
import com.iprody.api.AsyncListener;
import com.iprody.api.dto.XPaymentAdapterRequestMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaXPaymentAdapterMessageListener implements AsyncListener<XPaymentAdapterRequestMessage> {

    private final MessageHandler<XPaymentAdapterRequestMessage> handler;

    @Override
    public void onMessage(XPaymentAdapterRequestMessage message) {
        handler.handle(message);
    }

    @KafkaListener(
            topics = "${app.kafka.topics.x-payment-adapter.request-topic}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(XPaymentAdapterRequestMessage message,
                        ConsumerRecord<String, XPaymentAdapterRequestMessage> record,
                        Acknowledgment acknowledgment) {
        try {
            log.info("Received XPayment Adapter request: paymentGuid={}, partition={}, offset={}",
                    message.getPaymentGuid(),
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
