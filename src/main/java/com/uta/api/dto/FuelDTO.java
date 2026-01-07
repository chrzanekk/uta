package com.uta.api.dto;

import com.uta.api.enumeration.FuelType;

import java.math.BigDecimal;

public record FuelDTO(BigDecimal quantity, FuelType fuelType) {
}
