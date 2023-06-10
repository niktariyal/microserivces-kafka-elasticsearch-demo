package com.microservices.demo.elastic.query.service.common.api.error;

public class ElasticQueryServiceException extends RuntimeException{

    public ElasticQueryServiceException(){
        super();
    }

    public ElasticQueryServiceException(String message){
        super(message);
    }

    public ElasticQueryServiceException(String message, Throwable t){
        super(message, t);
    }
}
