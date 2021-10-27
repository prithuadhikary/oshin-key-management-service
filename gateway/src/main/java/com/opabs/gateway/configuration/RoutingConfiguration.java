package com.opabs.gateway.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutingConfiguration {

    /**
     * In case of local environment, where Eureka is being used, this will be lb://
     * otherwise http:// in kubernetes environment.
     */
    @Value("${route-locator.uri-prefix}")
    private String uriPrefix;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r ->
                        r.path("/api/user-service/**")
                                .uri(withUriPrefix("user-service/"))
                )
                .route(r ->
                        r.path("/api/crypto-service/**")
                        .uri(withUriPrefix("crypto-service/")))
                .route(r ->
                        r.path("/api/crypto-management-service/**")
                        .uri(withUriPrefix("crypto-management-service/")))
                .build();
    }

    /**
     * Appends the uriPrefix.
     *
     * @param uri the uri to prefix with uriPrefix.
     * @return uri to configure with the routeLocator.
     */
    private String withUriPrefix(String uri) {
        return uriPrefix + uri;
    }
}
