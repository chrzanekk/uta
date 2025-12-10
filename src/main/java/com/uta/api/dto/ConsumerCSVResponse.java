package com.uta.api.dto;

import java.util.List;

public record ConsumerCSVResponse(List<FuelTransactionFromCSVDto> transactions, int contentLength) {
}
