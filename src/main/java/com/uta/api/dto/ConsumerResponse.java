package com.uta.api.dto;

import java.util.List;

public record ConsumerResponse(List<FuelTransactionDto> transactions, int contentLength) {
}
