package com.opabs.trustchain.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class FeignConfig {

    @Autowired
    private HttpServletRequest currentRequest;

    @Bean
    public RequestInterceptor accessTokenInterceptor() {
        return requestTemplate -> {
            String authorizationHeader = currentRequest.getHeader(HttpHeaders.AUTHORIZATION);
            requestTemplate.header(HttpHeaders.AUTHORIZATION, authorizationHeader);
        };
    }

}
