//package com.microservices.demo.common.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.util.IdGenerator;
//import org.springframework.util.JdkIdGenerator;
//
//
//// removing this class as using IdGeneratorConfig for MDC-Interceptor for aggregate logging anc correlationID
//
//@Configuration
//public class IdGeneratorConfig {
//
//    @Bean
//    public IdGenerator idGenerator(){
//        return new JdkIdGenerator();
//    }
//}
