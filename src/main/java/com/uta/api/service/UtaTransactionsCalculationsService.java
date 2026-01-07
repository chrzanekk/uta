package com.uta.api.service;

import com.uta.api.dto.ConsumerCSVResponse;
import com.uta.api.dto.FuelTransactionFromCSVDto;
import com.uta.api.dto.VehiclesFuelConsumptionSummary;
import com.uta.api.dto.VehiclesFuelConsumptionSummaryAggregated;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface UtaTransactionsCalculationsService {

    VehiclesFuelConsumptionSummary getActualVehicleFuelUsage(LocalDate startDate);

    VehiclesFuelConsumptionSummaryAggregated getActualVehicleFuelUsageAggregated(LocalDate startDate);

    List<FuelTransactionFromCSVDto> getTransactionsByRegistrationNumber(String registrationNumber, LocalDate startDate);

   ConsumerCSVResponse findMisfuelTransactions(LocalDate startDate);
}
