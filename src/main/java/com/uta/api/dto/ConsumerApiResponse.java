package com.uta.api.dto;

import java.util.List;

public record ConsumerApiResponse(List<FuelTransactionFromApiDto> transactions, int contentLength) {
}
