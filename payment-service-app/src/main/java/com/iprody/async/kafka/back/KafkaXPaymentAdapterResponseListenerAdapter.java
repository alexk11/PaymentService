//package com.iprody.async.kafka.back;
//
//import com.iprody.api.AsyncListener;
//import handler.async.com.iprody.api.MessageHandler;
//import dto.com.iprody.api.XPaymentAdapterResponseMessage;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.support.Acknowledgment;
//import org.springframework.stereotype.Component;
//
//
//@Slf4j
//@Component
//public class KafkaXPaymentAdapterResponseListenerAdapter
//        implements AsyncListener<XPaymentAdapterResponseMessage> {
//
//    private final MessageHandler<XPaymentAdapterResponseMessage> handler;
//
//    public KafkaXPaymentAdapterResponseListenerAdapter(
//            MessageHandler<XPaymentAdapterResponseMessage> handler) {
//        this.handler = handler;
//    }
//
//    @Override
//    public void onMessage(XPaymentAdapterResponseMessage message) {
//        handler.handle(message);
//    }
//
//    @KafkaListener(topics = "${app.kafka.topics.x-payment-adapter.response-topic}",
//            groupId = "${spring.kafka.consumer.group-id}")
//    public void consume(
//            XPaymentAdapterResponseMessage message,
//            ConsumerRecord<String, XPaymentAdapterResponseMessage> record,
//            Acknowledgment ack) {
//        try {
//            log.info("Received XPayment Adapter response: paymentGuid={}, partition={}, offset={}",
//                    message.getPaymentGuid(),
//                    record.partition(),
//                    record.offset());
//            onMessage(message);
//            ack.acknowledge();
//        } catch (Exception e) {
//            log.error("Error handling XPayment Adapter response for paymentGuid={}", message.getPaymentGuid(), e);
//            throw e; // отдаём в error handler Spring Kafka
//        }
//    }
//
//}
