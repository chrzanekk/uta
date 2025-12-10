package com.uta.api.config;

import com.uta.api.service.UtaAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
public class UtaClientConfig {

    @Value("${uta.api.baseUrl}")
    private String baseUrl;

    @Value("${uta.api.userName}")
    private String userName;


    @Bean("utaRestClient") // Nazwany bean, żeby nie kolidował z innymi
    public RestClient utaRestClient(UtaAuthService authService) {
        String fullUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .pathSegment("Customer", userName, "em-trans-data").build().toUriString();

        return RestClient.builder()
                .baseUrl(fullUrl)
                .requestInterceptor(new BearerTokenInterceptor(authService))
                .build();
    }
}
