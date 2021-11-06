package com.opabs.gateway.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Data
@Configuration
@ConfigurationProperties(prefix = "okta")
public class OktaConfigProperties {

    private String issuerUrl;

    private String audience;

    private Duration connectionTimeout;
    
}
