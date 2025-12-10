package com.uta.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.uta.api.dto.ConsumerResponse;

import java.time.LocalDateTime;

public interface UtaTransactionsService {

    ConsumerResponse getTransactions() throws JsonProcessingException;

    ConsumerResponse getTransactions(LocalDateTime startDate, LocalDateTime endDate);
}
