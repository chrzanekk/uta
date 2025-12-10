package com.uta.api.service;

import com.uta.api.dto.VehicleFuelUsage;
import com.uta.api.dto.VehiclesFuelConsumptionSummary;

import java.time.LocalDate;
import java.util.List;

public interface UtaTransactionsCalculationsService {

    VehiclesFuelConsumptionSummary getActualVehicleFuelUsage(LocalDate startDate);
}
