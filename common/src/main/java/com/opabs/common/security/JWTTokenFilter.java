package com.opabs.common.security;

import com.google.gson.Gson;
import com.opabs.common.exceptions.AccessTokenInvalidException;
import com.opabs.common.exceptions.GroupInvalidException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class JWTTokenFilter extends AbstractAuthenticationProcessingFilter {

    private static final Pattern BEARER_TOKEN_PATTERN
            = Pattern.compile("^Bearer *([^ ]+) *$", Pattern.CASE_INSENSITIVE);

    protected JWTTokenFilter(AuthenticationManager authenticationManager) {
        super("/**", authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isEmpty(header)) {
            return null;
        }
        Matcher matcher = BEARER_TOKEN_PATTERN.matcher(header);
        if (matcher.matches()) {
            String accessToken = matcher.group(1);
            AccessToken token = parse(accessToken);

            GroupPermissions groupName = GroupPermissions.getByGroupName(token.getGroups());
            if (groupName == null) {
                throw new GroupInvalidException(token.getGroups());
            }

            JWTAuthToken authToken = new JWTAuthToken(
                    token,
                    groupName,
                    groupName.getAuthorities()
            );

            authToken.setAuthenticated(true);

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            log.debug("Token: {}", token);
            SecurityContextHolder.getContext().setAuthentication(authToken);
            return authToken;
        } else {
            throw new AccessTokenInvalidException();
        }
    }

    private AccessToken parse(String accessToken) {
        String[] split = accessToken.split("\\.");
        String claims = split[1];
        Gson gson = new Gson();
        return gson.fromJson(new String(Base64.getDecoder().decode(claims), StandardCharsets.UTF_8), AccessToken.class);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
        chain.doFilter(request, response);
    }
}
