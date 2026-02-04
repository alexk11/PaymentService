package com.iprody.adapter.async.kafka;

import com.iprody.api.AsyncSender;
import com.iprody.api.dto.XPaymentAdapterResponseMessage;
import com.iprody.adapter.config.KafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaXPaymentAdapterMessageProducer implements AsyncSender<XPaymentAdapterResponseMessage> {

    private final KafkaProperties kafkaProperties;
    private final KafkaTemplate<String, XPaymentAdapterResponseMessage> template;

    @Override
    public void send(XPaymentAdapterResponseMessage msg) {
        String key = msg.getPaymentGuid().toString();
        log.info("Sending XPayment Adapter response: guid={}, amount={}, status={}, currency={} -> topic={}",
                msg.getPaymentGuid(),
                msg.getAmount(),
                msg.getStatus(),
                msg.getCurrency(),
                kafkaProperties.getResponseTopic());

        template.send(kafkaProperties.getResponseTopic(), key, msg);
    }

}
