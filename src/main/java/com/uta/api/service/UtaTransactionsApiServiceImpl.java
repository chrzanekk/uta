package com.uta.api.service;

import com.uta.api.dto.FuelTransactionFromApiDto;
import com.uta.api.exception.ClientNotFoundException;
import com.uta.api.dto.ConsumerApiResponse;
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
public class UtaTransactionsApiServiceImpl implements UtaTransactionsApiService {

    private static final String SYNC_CLIENT = "synchronizationClientId";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RestClient restClient;

    @Value("${uta.api.synchronizationClientId}")
    private String syncClientID;

    public UtaTransactionsApiServiceImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public ConsumerApiResponse getTransactions() {
        logger.debug("Request to get list of all fuel transactions");
        List<FuelTransactionFromApiDto> transactions = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam(SYNC_CLIENT, syncClientID)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    throw new ClientNotFoundException("Resource not found: " + response.getStatusCode());
                })).body(new ParameterizedTypeReference<>() {
                });
        List<FuelTransactionFromApiDto> safeList = transactions != null ? transactions : List.of();
        return new ConsumerApiResponse(safeList, safeList.size());
    }

    @Override
    public ConsumerApiResponse getTransactions(LocalDateTime startDate, LocalDateTime endDate) {
        return new ConsumerApiResponse(List.of(), 0);
    }
}
