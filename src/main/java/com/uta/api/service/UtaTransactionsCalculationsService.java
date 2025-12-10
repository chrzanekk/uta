package com.uta.api.service;

import com.uta.api.dto.VehicleFuelUsage;

import java.time.LocalDate;
import java.util.List;

public interface UtaTransactionsCalculationsService {

    List<VehicleFuelUsage> getActualVehicleFuelUsage(LocalDate startDate);
}
