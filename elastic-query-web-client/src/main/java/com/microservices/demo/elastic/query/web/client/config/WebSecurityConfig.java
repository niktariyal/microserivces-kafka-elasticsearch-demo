package com.microservices.demo.elastic.query.web.client.config;

import com.microservices.demo.config.UserConfigData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
//@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final Logger LOG = LoggerFactory.getLogger(WebSecurityConfig.class);

//    private final UserConfigData userConfigData;
    private final ClientRegistrationRepository clientRegistrationRepository;

    private static final String GROUPS_CLAIM = "groups";
    private static final String ROLE_PREFIX = "ROLE_";
    @Value("${security.logout-success-url}")
    private String logoutSuccessUrl;

    public WebSecurityConfig(//UserConfigData userConfigData,
                             ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
//        this.userConfigData = userConfigData;
    }

    OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler(){
        OidcClientInitiatedLogoutSuccessHandler successHandler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        successHandler.setPostLogoutRedirectUri(logoutSuccessUrl);
        return successHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.httpBasic().and()
//                .authorizeRequests()
//                .antMatchers("/").permitAll()
//                .antMatchers("/**").hasRole("USER")
//                .anyRequest().fullyAuthenticated();
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .anyRequest()
                .fullyAuthenticated()
                .and()
                .logout().logoutSuccessHandler(oidcLogoutSuccessHandler())
                .and()
                .oauth2Client()
                .and()
                .oauth2Login()
                .userInfoEndpoint()
                .userAuthoritiesMapper(userAuthoritiesMapper());
    }

    private GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            authorities.forEach(
                    authority -> {
                        if(authority instanceof OidcUserAuthority){
                            OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;
                            OidcIdToken oidcIdToken = oidcUserAuthority.getIdToken();
                            LOG.info("Username from id token: {}",oidcIdToken.getPreferredUsername());
                            OidcUserInfo userInfo = oidcUserAuthority.getUserInfo();

                            List<SimpleGrantedAuthority> groupAuthorities =
                                    userInfo.getClaimAsStringList(GROUPS_CLAIM).stream()
                                            .map(group -> new SimpleGrantedAuthority(ROLE_PREFIX + group.toUpperCase()))
                                            .collect(Collectors.toList());
                            mappedAuthorities.addAll(groupAuthorities);
                        }
                    });
            return mappedAuthorities;
        };
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser(userConfigData.getUsername())
//                .password(passwordEncoder().encode(userConfigData.getPassword()))
//                .roles(userConfigData.getRoles());
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
}
