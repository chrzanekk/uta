package com.uta.api.dto;

import java.time.LocalDateTime;

public record ConsumerRequest(
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
