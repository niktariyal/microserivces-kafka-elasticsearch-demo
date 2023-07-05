package com.microservices.demo.api.gateway.service.config;

import com.microservices.demo.config.ApiGatewayServiceConfigData;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;

@Configuration
public class ApiGatewayConfig {

    private final ApiGatewayServiceConfigData apiGatewayServiceConfigData;

    private static final String HEADER_FOR_KEY_RESOLVER = "Authorization";

    public ApiGatewayConfig(ApiGatewayServiceConfigData apiGatewayServiceConfigData) {
        this.apiGatewayServiceConfigData = apiGatewayServiceConfigData;
    }

    @Bean(name = "authHeaderResolver")
    KeyResolver userKeyResolver(){
        return exchange -> Mono.just(Objects.requireNonNull(exchange
                .getRequest().getHeaders().getFirst(HEADER_FOR_KEY_RESOLVER)));
    }

    //circuit breaker config
    @Bean
    Customizer<ReactiveResilience4JCircuitBreakerFactory> circuitBreakerFactoryCustomizer(){
        return reactiveResilience4JCircuitBreakerFactory ->
                reactiveResilience4JCircuitBreakerFactory.configureDefault(id ->
                        new Resilience4JConfigBuilder(id)
                                .timeLimiterConfig(TimeLimiterConfig.custom()
                                        .timeoutDuration(Duration.ofMillis(apiGatewayServiceConfigData.getTimeoutMs()))
                                        .build())
                                .circuitBreakerConfig(
                                        CircuitBreakerConfig.custom()
                                            .failureRateThreshold(apiGatewayServiceConfigData.getFailureRateThreshold())
                                            .slowCallRateThreshold(apiGatewayServiceConfigData.getSlowCallRateThreshold())
                                            .slowCallDurationThreshold(Duration.ofMillis(
                                                    apiGatewayServiceConfigData.getSlowCallDurationThreshold()))
                                            .permittedNumberOfCallsInHalfOpenState(apiGatewayServiceConfigData
                                                    .getPermittedNumOfCallsInHalfOpenState())
                                            .slidingWindowSize(apiGatewayServiceConfigData.getSlidingWindowSize())
                                            .minimumNumberOfCalls((apiGatewayServiceConfigData.getMinNumberOfCalls()))
                                            .waitDurationInOpenState(Duration.ofMillis(
                                                    apiGatewayServiceConfigData.getWaitDurationInOpenState()))
                                            .build())
                                .build());
    }
}
