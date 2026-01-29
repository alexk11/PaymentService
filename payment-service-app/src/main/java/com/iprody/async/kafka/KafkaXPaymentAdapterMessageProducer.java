package com.iprody.async.kafka;

import com.iprody.api.AsyncSender;
import com.iprody.api.dto.XPaymentAdapterRequestMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaXPaymentAdapterMessageProducer implements AsyncSender<XPaymentAdapterRequestMessage> {

    private final KafkaProperties kafkaProperties;
    private final KafkaTemplate<String, XPaymentAdapterRequestMessage> template;

    @Override
    public void send(XPaymentAdapterRequestMessage msg) {
        final String key = msg.getPaymentGuid().toString();
        log.info("Sending XPayment Adapter request: guid={}, amount={}, currency = {} -> topic = {} ",
                msg.getPaymentGuid(),
                msg.getAmount(),
                msg.getCurrency(),
                kafkaProperties.getRequestTopic());
        template.send(kafkaProperties.getRequestTopic(), key, msg);
    }

}
