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
        // 1. Pobierz token (z cache lub nowy) i dodaj do nagłówka
        String token = authService.getAccessToken();
        request.getHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        // 2. Wykonaj request
        ClientHttpResponse response = execution.execute(request, body);

        // 3. Sprawdź czy dostaliśmy 401 Unauthorized
        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            logger.warn("Received 401 Unauthorized. Invalidating token and retrying...");

            // 4. Inwaliduj token (wymuś pobranie nowego przy następnym wołaniu)
            authService.invalidateToken();

            // 5. Pobierz świeży token
            String newToken = authService.getAccessToken();

            // UWAGA: Nie możemy zmodyfikować 'request' (jest immutable w niektórych implementacjach),
            // ale nagłówki są zazwyczaj mutowalne przed wysłaniem.
            // Jeśli używasz standardowego request factory, to zadziała.
            request.getHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer " + newToken);

            // 6. Ponów request
            // Musimy zamknąć poprzedni response stream, żeby nie było wycieku zasobów
            response.close();
            return execution.execute(request, body);
        }

        return response;
    }
}
