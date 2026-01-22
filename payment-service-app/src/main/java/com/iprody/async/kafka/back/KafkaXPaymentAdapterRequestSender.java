//package com.iprody.async.kafka.back;
//
//import com.iprody.api.AsyncSender;
//import dto.com.iprody.api.XPaymentAdapterRequestMessage;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//
//@Slf4j
//@Service
//public class KafkaXPaymentAdapterRequestSender
//        implements AsyncSender<XPaymentAdapterRequestMessage> {
//
//    private final KafkaTemplate<String, XPaymentAdapterRequestMessage> template;
//    private final String topic;
//
//    public KafkaXPaymentAdapterRequestSender(
//            KafkaTemplate<String, XPaymentAdapterRequestMessage> template,
//            @Value("${app.kafka.topics.x-payment-adapter.request-topic:x-payment-adapter.requests}") String topic) {
//        this.template = template;
//        this.topic = topic;
//    }
//
//    @Override
//    public void send(XPaymentAdapterRequestMessage msg) {
//        String key = msg.getPaymentGuid().toString(); // фиксируем партиционирование по платежу
//        log.info("Sending XPayment Adapter request: guid={}, amount={}, currency={} -> topic={}",
//                msg.getPaymentGuid(), msg.getAmount(), msg.getCurrency(), topic);
//        template.send(topic, key, msg);
//    }
//
//}
