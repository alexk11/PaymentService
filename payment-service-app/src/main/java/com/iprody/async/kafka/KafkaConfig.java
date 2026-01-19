package com.iprody.async.kafka;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.iprody.async.XPaymentAdapterRequestMessage;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    public ProducerFactory<String, XPaymentAdapterRequestMessage> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, XPaymentAdapterRequestMessage> kafkaTemplate() {
        return new KafkaTemplate<String, XPaymentAdapterRequestMessage>(producerFactory());
    }

//    public ProducerFactory<String, ServingDetailsEntity> producerFactoryServingDetail() {
//        Map<String, Object> config = new HashMap<>();
//        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
//        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        return new DefaultKafkaProducerFactory(config);
//    }
//
//    @Bean
//    public KafkaTemplate<String, ServingDetailsEntity> kafkaTemplateServingDetailsListener() {
//        return new KafkaTemplate<String, ServingDetailsEntity>(producerFactoryServingDetail());
//    }
//
//    @Bean
//    public ConsumerFactory<String, ServingDetailsEntity> consumerFactory() {
//        Map<String, Object> config = new HashMap<>();
//        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
//        config.put(ConsumerConfig.GROUP_ID_CONFIG, "event-group");
//        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),
//                new JsonDeserializer<>(ServingDetailsEntity.class));
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, ServingDetailsEntity> kafkaTemplateTaskDetailsListener() {
//        ConcurrentKafkaListenerContainerFactory<String, ServingDetailsEntity> factory = new ConcurrentKafkaListenerContainerFactory<String, ServingDetailsEntity>();
//        factory.setConsumerFactory(consumerFactory());
//        return factory;
//    }

}
