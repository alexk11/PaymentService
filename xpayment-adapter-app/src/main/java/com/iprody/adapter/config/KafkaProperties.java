package com.iprody.adapter.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


@Component
@Data
@ToString
@PropertySource("classpath:application-kafka.yaml")
@ConfigurationProperties(prefix = "app.kafka.topics.xpayment-adapter")
public class KafkaProperties {

    private String requestTopic;
    private String responseTopic;

    public KafkaProperties(
            @Value("${request-topic}") String requestTopic,
            @Value("${response-topic}") String responseTopic) {
        this.requestTopic = requestTopic;
        this.responseTopic = responseTopic;
    }
}