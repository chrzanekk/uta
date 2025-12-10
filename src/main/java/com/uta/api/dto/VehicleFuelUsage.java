package com.uta.api.dto;

import java.math.BigDecimal;

public record VehicleFuelUsage(String registrationNumber, BigDecimal fuelQuantity) {
}
