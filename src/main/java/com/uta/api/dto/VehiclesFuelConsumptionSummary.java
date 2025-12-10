package com.uta.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record VehiclesFuelConsumptionSummary(
        BigDecimal dieselSummaryUsage,
        BigDecimal petrolSummaryUsage,
        BigDecimal total,
        List<VehicleFuelUsage> fuelUsageList) {
}
