package com.opabs.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class ExampleWebFilter implements WebFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleWebFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange,
                             WebFilterChain webFilterChain) {
        LOGGER.info("Hitting {}", serverWebExchange.getRequest().getURI());
        serverWebExchange.getResponse()
          .getHeaders().add("web-filter", "web-filter-test");
        return webFilterChain.filter(serverWebExchange);
    }
}