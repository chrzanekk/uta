package com.uta.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.uta.api.dto.ConsumerApiResponse;

import java.time.LocalDateTime;

public interface UtaTransactionsApiService {

    ConsumerApiResponse getTransactions() throws JsonProcessingException;

    ConsumerApiResponse getTransactions(LocalDateTime startDate, LocalDateTime endDate);
}
