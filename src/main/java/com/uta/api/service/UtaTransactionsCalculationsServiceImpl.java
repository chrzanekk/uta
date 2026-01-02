package com.uta.api.service;

import com.uta.api.dto.*;
import com.uta.api.enumeration.EuNorm;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
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

    @Override
    public VehiclesFuelConsumptionSummaryAggregated getActualVehicleFuelUsageAggregated(LocalDate startDate) {
        log.info("Rozpoczynam obliczanie zagregowanego zużycia paliwa wg norm EURO od daty: {}", startDate);

        List<FuelTransactionFromCSVDto> allTransactions = csvService.importAllFromDirectory();

        List<FuelTransactionFromCSVDto> validTransactions = allTransactions.stream()
                .filter(t -> t.deliveryDate() != null)
                .filter(t -> !t.deliveryDate().toLocalDate().isBefore(startDate))
                .filter(t -> "LTR".equalsIgnoreCase(t.unitOfMeasure()))
                .filter(t -> t.quantity() != null)
                .toList();

        Map<String, EuNorm> registrationToNormMap = Arrays.stream(CarsDetails.values())
                .collect(Collectors.toMap(
                        car -> normalizeRegistrationNumber(car.getRegistrationNumber()),
                        CarsDetails::getNorm,
                        (existing, replacement) -> existing
                ));

        Map<EuNorm, BigDecimal> usageByNormMap = validTransactions.stream()
                .filter(t -> t.registrationNumber() != null && !t.registrationNumber().isBlank())
                .map(t -> {
                    String normReg = normalizeRegistrationNumber(t.registrationNumber());
                    EuNorm norm = registrationToNormMap.get(normReg);
                    return Map.entry(norm != null ? norm : "UNKNOWN", t.quantity());
                })
                .filter(entry -> entry.getKey() instanceof EuNorm)
                .collect(Collectors.groupingBy(
                        entry -> (EuNorm) entry.getKey(),
                        Collectors.reducing(BigDecimal.ZERO, Map.Entry::getValue, BigDecimal::add)
                ));

        List<ConsumptionByNormDTO> aggregatedByNorm = usageByNormMap.entrySet().stream()
                .map(entry -> new ConsumptionByNormDTO(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(ConsumptionByNormDTO::norm))
                .toList();

        BigDecimal totalFromNorm = aggregatedByNorm.stream()
                        .map(ConsumptionByNormDTO::fuelUsage)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Zgrupowano zużycie dla {} norm EURO", aggregatedByNorm.size());


        BigDecimal dieselUsage = validTransactions.stream()
                .filter(t -> t.productName() != null && (t.productName().contains("Olej napędowy") || t.productName().contains("Diesel")))
                .map(FuelTransactionFromCSVDto::quantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal petrolUsage = validTransactions.stream()
                .filter(t -> t.productName() != null && t.productName().contains("Etylina"))
                .map(FuelTransactionFromCSVDto::quantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalUsage = validTransactions.stream()
                .map(FuelTransactionFromCSVDto::quantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new VehiclesFuelConsumptionSummaryAggregated(
                dieselUsage,
                petrolUsage,
                totalUsage,
                totalFromNorm,
                aggregatedByNorm
        );
    }

    @Override
    public List<FuelTransactionFromCSVDto> getTransactionsByRegistrationNumber(String registrationNumber, LocalDate startDate) {
        log.info("Rozpoczynam obliczanie zużycia paliwa dla pojazdu o numerach: {} od daty: {}", registrationNumber, startDate);

        List<FuelTransactionFromCSVDto> allTransactions = csvService.importAllFromDirectory();

        List<FuelTransactionFromCSVDto> usageByVehicle = allTransactions.stream()
                .filter(t -> t.deliveryDate() != null)
                .filter(t -> !t.deliveryDate().toLocalDate().isBefore(startDate))
                .filter(t -> "LTR".equalsIgnoreCase(t.unitOfMeasure()))
                .filter(t -> t.registrationNumber() != null && !t.registrationNumber().isBlank() && normalizeRegistrationNumber(t.registrationNumber()).equalsIgnoreCase(normalizeRegistrationNumber(registrationNumber)))
                .filter(t -> t.quantity() != null)
                .toList();
        log.info("Obliczono zużycie dla numerów rejestracyjnych {}: ilość transakcji: {} ",registrationNumber, usageByVehicle.size());
        return usageByVehicle;
    }
}
