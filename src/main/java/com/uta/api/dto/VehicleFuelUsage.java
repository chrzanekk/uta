package com.uta.api.dto;

import java.util.List;

public record VehicleFuelUsage(String registrationNumber, List<FuelDTO> fuelUsage) {
}
