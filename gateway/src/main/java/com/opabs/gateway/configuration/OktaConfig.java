package com.opabs.gateway.configuration;

import com.okta.jwt.AccessTokenVerifier;
import com.okta.jwt.JwtVerifiers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OktaConfig {

    @Bean
    public AccessTokenVerifier accessTokenVerifier(OktaConfigProperties oktaConfigProperties) {
        return JwtVerifiers.accessTokenVerifierBuilder()
                .setIssuer(oktaConfigProperties.getIssuerUrl())
                .setAudience(oktaConfigProperties.getAudience())
                .setConnectionTimeout(oktaConfigProperties.getConnectionTimeout())
                .build();
    }

}
