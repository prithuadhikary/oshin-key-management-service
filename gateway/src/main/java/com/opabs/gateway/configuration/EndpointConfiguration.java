package com.opabs.gateway.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "endpoint.configuration")
public class EndpointConfiguration {

    private String cryptoService;

    private String trustChainService;

    private String tenantManagementService;

    private String authService;

    public String getCryptoService() {
        return cryptoService;
    }

    public void setCryptoService(String cryptoService) {
        this.cryptoService = cryptoService;
    }

    public String getTrustChainService() {
        return trustChainService;
    }

    public void setTrustChainService(String trustChainService) {
        this.trustChainService = trustChainService;
    }

    public String getTenantManagementService() {
        return tenantManagementService;
    }

    public void setTenantManagementService(String tenantManagementService) {
        this.tenantManagementService = tenantManagementService;
    }

    public String getAuthService() {
        return authService;
    }

    public void setAuthService(String authService) {
        this.authService = authService;
    }
}
