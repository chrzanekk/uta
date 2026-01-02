package com.uta.api.dto;

import com.uta.api.enumeration.EuNorm;

import java.math.BigDecimal;

public record ConsumptionByNormDTO(
        EuNorm norm, BigDecimal fuelUsage
) {
}
