package com.opabs.gateway.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutingConfiguration {

    @Autowired
    private EndpointConfiguration endpointConfiguration;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r ->
                        r.path("/api/crypto-service/**")
                                .uri(endpointConfiguration.getCryptoService()))
                .route(r ->
                        r.path("/api/trust-chain-service/**")
                                .uri(endpointConfiguration.getTrustChainService()))
                .route(r ->
                        r.path("/api/tenant-management-service/**")
                                .uri(endpointConfiguration.getTenantManagementService()))
                .route(r ->
                        r.path("/api/auth-service/**")
                                .uri(endpointConfiguration.getAuthService()))
                .build();
    }

}
