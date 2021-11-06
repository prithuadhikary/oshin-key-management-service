package com.opabs.gateway.filter;

import com.okta.jwt.AccessTokenVerifier;
import com.okta.jwt.JwtVerificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter implements WebFilter {

    private final AccessTokenVerifier accessTokenVerifier;

    private static final Pattern BEARER_TOKEN_PATTERN
            = Pattern.compile("^Bearer *([^ ]+) *$", Pattern.CASE_INSENSITIVE);

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final WebFilterChain chain) {
        return Mono.fromCallable(() -> {
            boolean authValid = false;
            HttpHeaders headers = exchange.getRequest().getHeaders();
            List<String> authorizationHeaders = headers.get(HttpHeaders.AUTHORIZATION);
            if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
                String authorization = authorizationHeaders.get(0);
                Matcher matcher = BEARER_TOKEN_PATTERN.matcher(authorization);
                if (matcher.matches()) {
                    String accessToken = matcher.group(1);
                    try {
                        accessTokenVerifier.decode(accessToken);
                        log.debug("Auth token validation successful.");
                        authValid = true;
                    } catch (JwtVerificationException e) {
                        log.error("Error occurred while decoding the access token.", e);
                    }
                }
            }
            return authValid;
        }).flatMap(authValid -> {
            if (authValid) {
                return chain.filter(exchange);
            } else {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return Mono.empty();
            }
        });
    }
}
