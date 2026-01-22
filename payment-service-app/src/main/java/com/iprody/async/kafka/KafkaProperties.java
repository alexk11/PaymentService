package com.iprody.async.kafka;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@ToString
@Component
@ConfigurationProperties(prefix = "app.kafka.topics.x-payment-adapter")
public class KafkaProperties {

    private String requestTopic;
    private String responseTopic;
}
