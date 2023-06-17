package com.microservices.demo.analytics.service.config;

import com.microservices.demo.analytics.service.security.AnalyticsUserDetailsService;
import com.microservices.demo.analytics.service.security.AnalyticsUserJwtConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final AnalyticsUserDetailsService analyticsUserDetailsService;
    private final OAuth2ResourceServerProperties oAuth2ResourceServerProperties;
    public WebSecurityConfig(
                             AnalyticsUserDetailsService analyticsUserDetailsService,
                             OAuth2ResourceServerProperties oAuth2ResourceServerProperties) {
        this.analyticsUserDetailsService = analyticsUserDetailsService;
        this.oAuth2ResourceServerProperties = oAuth2ResourceServerProperties;
    }

    @Value("${security.paths-to-ignore}")
    private String[] pathsToIgnore;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(pathsToIgnore);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().csrf().disable()
                .authorizeRequests().anyRequest()
                .fullyAuthenticated()
                .and()
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(analyticsUserJwtConverter());

    }

    @Bean
    Converter<Jwt,? extends AbstractAuthenticationToken> analyticsUserJwtConverter() {
        return new AnalyticsUserJwtConverter(analyticsUserDetailsService);
    }

    @Bean
    JwtDecoder jwtDecoder(@Qualifier("analytics-service-audience-validator")
                                  OAuth2TokenValidator<Jwt> audienceValidator){
        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromOidcIssuerLocation(
                oAuth2ResourceServerProperties.getJwt().getIssuerUri());
        OAuth2TokenValidator<Jwt> withIssuer =
                JwtValidators.createDefaultWithIssuer(oAuth2ResourceServerProperties.getJwt()
                        .getIssuerUri());
        OAuth2TokenValidator<Jwt> withAudience =
                new DelegatingOAuth2TokenValidator<>(withIssuer,audienceValidator);
        jwtDecoder.setJwtValidator(withAudience);
        return jwtDecoder;
    }

}
