package com.microservices.demo.kafka.streams.service.config;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.config.KafkaStreamsConfigData;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.parameters.P;

import java.util.Properties;

@Configuration
public class KafkaStreamsCofig {

    private final KafkaConfigData kafkaConfigData;
    private final KafkaStreamsConfigData kafkaStreamsConfigData;

    public KafkaStreamsCofig(KafkaConfigData kafkaConfigData,
                             KafkaStreamsConfigData kafkaStreamsConfigData) {
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaStreamsConfigData = kafkaStreamsConfigData;
    }

    @Bean
    @Qualifier("streamConfiguration")
    public Properties streamsConfiguration(){
        Properties streamsConfiguratino = new Properties();
        streamsConfiguratino.put(StreamsConfig.APPLICATION_ID_CONFIG,kafkaStreamsConfigData.getApplicationID());
        streamsConfiguratino.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaConfigData.getBootstrapServers());
        streamsConfiguratino.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                kafkaConfigData.getSchemaRegistryUrl());
        streamsConfiguratino.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguratino.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguratino.put(StreamsConfig.STATE_DIR_CONFIG, kafkaStreamsConfigData.getStateFileLocation());
        System.out.println("State File Location : "+kafkaStreamsConfigData.getStateFileLocation());
        return streamsConfiguratino;
    }
}
