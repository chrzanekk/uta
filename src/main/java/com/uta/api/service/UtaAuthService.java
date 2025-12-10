package com.uta.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class UtaAuthService {

    private final Logger logger = LoggerFactory.getLogger(UtaAuthService.class);
    private final RestClient authClient; // Osobny klient do autoryzacji (bez interceptora!)

    // Przechowujemy token i czas wygaśnięcia w wątkowo-bezpiecznym kontenerze
    private final AtomicReference<String> currentToken = new AtomicReference<>();
    private final AtomicReference<Instant> tokenExpiration = new AtomicReference<>(Instant.MIN);

    @Value("${uta.api.authUrl}")
    private String authUrl;

    @Value("${uta.api.userName}")
    private String username;

    @Value("${uta.api.password}")
    private String password;

    public UtaAuthService() {
        this.authClient = RestClient.builder().build();
    }


    public String getAccessToken() {
        if (isTokenExpired()) {
            logger.info("Token expired or missing. Refreshing...");
            refreshToken();
        }
        return currentToken.get();
    }

    public void invalidateToken() {
        this.tokenExpiration.set(Instant.MIN);
    }

    private boolean isTokenExpired() {
        return Instant.now().plusSeconds(10).isAfter(tokenExpiration.get());
    }

    private synchronized void refreshToken() {
        if (!isTokenExpired()) {
            return;
        }

        try {
            Map<String, String> credentials = Map.of(
                    "username", username,
                    "password", password
            );

            // Odbieramy surowy String (token)
            String rawToken = authClient.post()
                    .uri(authUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(credentials)
                    .retrieve()
                    .body(String.class); // <--- ZMIANA TUTAJ

            if (rawToken != null) {
                String cleanToken = rawToken.replace("\"", "").trim();

                currentToken.set(cleanToken);

                tokenExpiration.set(Instant.now().plusSeconds(3600 - 300));

                logger.info("Token refreshed successfully. Valid for ~1 hour.");
            } else {
                throw new RuntimeException("Failed to retrieve access token: Empty response");
            }
        } catch (Exception e) {
            logger.error("Error while refreshing token", e);
            throw new RuntimeException("Authentication failed", e);
        }
    }
}
