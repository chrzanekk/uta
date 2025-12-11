package com.uta.api.service;

import com.uta.api.dto.FuelTransactionFromCSVDto;
import com.uta.api.dto.VehiclesFuelConsumptionSummary;

import java.time.LocalDate;
import java.util.List;

public interface UtaTransactionsCalculationsService {

    VehiclesFuelConsumptionSummary getActualVehicleFuelUsage(LocalDate startDate);

    List<FuelTransactionFromCSVDto> getTransactionsByRegistrationNumber(String registrationNumber, LocalDate startDate);
}
