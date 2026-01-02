package com.uta.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record VehiclesFuelConsumptionSummaryAggregated(
        BigDecimal dieselSummaryUsage,
        BigDecimal petrolSummaryUsage,
        BigDecimal total,
        BigDecimal totalFromNorm,
        List<ConsumptionByNormDTO> aggregatedByNorm
) {
}
