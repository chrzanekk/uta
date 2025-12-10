package com.uta.api.config;

import com.uta.api.service.UtaAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class BearerTokenInterceptor implements ClientHttpRequestInterceptor {
    private final Logger logger = LoggerFactory.getLogger(BearerTokenInterceptor.class);
    private final UtaAuthService authService;

    public BearerTokenInterceptor(UtaAuthService authService) {
        this.authService = authService;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String token = authService.getAccessToken();
        request.getHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        ClientHttpResponse response = execution.execute(request, body);

        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            logger.warn("Received 401 Unauthorized. Invalidating token and retrying...");

            authService.invalidateToken();

            String newToken = authService.getAccessToken();

            request.getHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer " + newToken);

            response.close();
            return execution.execute(request, body);
        }

        return response;
    }
}
