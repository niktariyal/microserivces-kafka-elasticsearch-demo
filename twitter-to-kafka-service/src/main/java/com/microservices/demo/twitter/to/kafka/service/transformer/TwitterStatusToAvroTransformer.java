package com.microservices.demo.twitter.to.kafka.service.transformer;

import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import twitter4j.Status;

import java.util.Date;

@Component
public class TwitterStatusToAvroTransformer {

    public static final Logger LOG = LoggerFactory.getLogger(TwitterStatusToAvroTransformer.class);

    public TwitterAvroModel getTwitterAvroModelFromStatus(Status status){
        LOG.info("TwitterStatusToAvroTransformer | getTwitterAvroModelFromStatus() method init, " +
                "status: {}",status);
        return TwitterAvroModel.newBuilder()
                .setId(status.getId())
                .setUserId(status.getUser().getId())
                .setText(status.getText())
                .setCreatedAt(new Date().getTime())
                .build();
    }
}
