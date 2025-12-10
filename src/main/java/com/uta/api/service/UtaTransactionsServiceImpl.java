package com.uta.api.service;

import com.uta.api.config.BearerTokenInterceptor;
import com.uta.api.dto.FuelTransactionDto;
import com.uta.api.exception.ClientNotFoundException;
import com.uta.api.dto.ConsumerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UtaTransactionsServiceImpl implements UtaTransactionsService {

    private static final String SYNC_CLIENT = "synchronizationClientId";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RestClient restClient;

    @Value("${uta.api.synchronizationClientId}")
    private String syncClientID;

    public UtaTransactionsServiceImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public ConsumerResponse getTransactions() {
        logger.debug("Request to get list of all fuel transactions");
        List<FuelTransactionDto> transactions = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam(SYNC_CLIENT, syncClientID)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    throw new ClientNotFoundException("Resource not found: " + response.getStatusCode());
                })).body(new ParameterizedTypeReference<>() {
                });
        List<FuelTransactionDto> safeList = transactions != null ? transactions : List.of();
        return new ConsumerResponse(safeList, safeList.size());
    }

    @Override
    public ConsumerResponse getTransactions(LocalDateTime startDate, LocalDateTime endDate) {
        return new ConsumerResponse(List.of(), 0);
    }
}
