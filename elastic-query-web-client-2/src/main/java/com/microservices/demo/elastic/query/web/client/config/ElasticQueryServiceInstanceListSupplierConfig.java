//package com.microservices.demo.elastic.query.web.client.config;
//
//import com.microservices.demo.config.ElasticQueryWebClientConfigData;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.cloud.client.DefaultServiceInstance;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import reactor.core.publisher.Flux;
//
//import java.util.List;
//import java.util.stream.Collectors;
//

//removing/commenting this class as using Springcloud eureka loalbalancing

//@Configuration
//@Primary
//public class ElasticQueryServiceInstanceListSupplierConfig implements ServiceInstanceListSupplier {
//
//    public static final Logger LOG = LoggerFactory.getLogger(ElasticQueryServiceInstanceListSupplierConfig.class);
//
//    private final ElasticQueryWebClientConfigData.WebClient webClientConfig;
//
//    public ElasticQueryServiceInstanceListSupplierConfig(ElasticQueryWebClientConfigData webClientConfig) {
//        this.webClientConfig = webClientConfig.getWebClient();
//    }
//
//    @Override
//    public String getServiceId() {
//        return webClientConfig.getServiceId();
//    }
//
//    @Override
//    public Flux<List<ServiceInstance>> get() {
//        return Flux.just(webClientConfig.getInstances().stream()
//                            .map(instance -> new DefaultServiceInstance(
//                                    instance.getId(),
//                                    getServiceId(),
//                                    instance.getHost(),
//                                    instance.getPort(),
//                                    false
//                            )).collect(Collectors.toList()));
//    }
//}
