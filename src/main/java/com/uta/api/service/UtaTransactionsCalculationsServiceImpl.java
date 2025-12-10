package com.uta.api.service;

import com.uta.api.dto.FuelTransactionFromCSVDto;
import com.uta.api.dto.VehicleFuelUsage;
import com.uta.api.dto.VehiclesFuelConsumptionSummary;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UtaTransactionsCalculationsServiceImpl implements UtaTransactionsCalculationsService {

    private final Logger log = LoggerFactory.getLogger(UtaTransactionsCalculationsServiceImpl.class);

    private final UtaTransactionsCsvService csvService;

    @Override
    public VehiclesFuelConsumptionSummary getActualVehicleFuelUsage(LocalDate startDate) {
        log.info("Rozpoczynam obliczanie zużycia paliwa od daty: {}", startDate);
        List<FuelTransactionFromCSVDto> allTransactions = csvService.importAllFromDirectory();

        Map<String, BigDecimal> usageByVehicle = allTransactions.stream()
                .filter(t -> t.deliveryDate() != null)
                .filter(t -> !t.deliveryDate().toLocalDate().isBefore(startDate))
                .filter(t -> "LTR".equalsIgnoreCase(t.unitOfMeasure()))
                .filter(t -> t.registrationNumber() != null && !t.registrationNumber().isBlank())
                .filter(t -> t.quantity() != null)
                .collect(Collectors.groupingBy(
                        t -> normalizeRegistrationNumber(t.registrationNumber()),
                        Collectors.reducing(BigDecimal.ZERO, FuelTransactionFromCSVDto::quantity,
                                BigDecimal::add)
                ));
        List<VehicleFuelUsage> vehicleFuelUsages = usageByVehicle.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(entry -> new VehicleFuelUsage(entry.getKey(), entry.getValue()))
                .toList();
        log.info("Obliczono zużycie dla {} pojazdów", vehicleFuelUsages.size());

        BigDecimal dieselUsage = allTransactions.stream()
                .filter(t -> t.deliveryDate() != null)
                .filter(t -> !t.deliveryDate().toLocalDate().isBefore(startDate))
                .filter(t -> "LTR".equalsIgnoreCase(t.unitOfMeasure()))
                .filter(t -> t.quantity() != null)
                .filter(t -> t.productName().contains("Olej napędowy") || t.productName().contains("Diesel"))
                .map(FuelTransactionFromCSVDto::quantity).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal petrol = allTransactions.stream()
                .filter(t -> t.deliveryDate() != null)
                .filter(t -> !t.deliveryDate().toLocalDate().isBefore(startDate))
                .filter(t -> "LTR".equalsIgnoreCase(t.unitOfMeasure()))
                .filter(t -> t.quantity() != null)
                .filter(t -> t.productName().contains("Etylina"))
                .map(FuelTransactionFromCSVDto::quantity).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal total = allTransactions.stream()
                .filter(t -> t.deliveryDate() != null)
                .filter(t -> !t.deliveryDate().toLocalDate().isBefore(startDate))
                .filter(t -> "LTR".equalsIgnoreCase(t.unitOfMeasure()))
                .map(FuelTransactionFromCSVDto::quantity)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new VehiclesFuelConsumptionSummary(dieselUsage, petrol, total, vehicleFuelUsages);
    }

    /**
     * Metoda pomocnicza do normalizacji numerów rejestracyjnych.
     * Np. "LPU 11111 " -> "LPU11111"
     * Zapobiega sytuacji, gdzie "WA 12345" i "WA12345" są traktowane jako dwa różne pojazdy.
     */

    private String normalizeRegistrationNumber(String regNumber) {
        if (regNumber == null) return "UNKNOWN";
        return regNumber.replaceAll("\\s+", "").toUpperCase();
    }
}
