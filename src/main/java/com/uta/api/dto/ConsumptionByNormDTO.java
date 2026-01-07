package com.uta.api.dto;

import com.uta.api.enumeration.EuNorm;

import java.math.BigDecimal;
import java.util.List;

public record ConsumptionByNormDTO(
        EuNorm norm, List<FuelDTO> fuelUsage
) {
}
