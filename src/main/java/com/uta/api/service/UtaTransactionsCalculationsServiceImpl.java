package com.uta.api.service;

import com.uta.api.dto.*;
import com.uta.api.enumeration.CarsDetails;
import com.uta.api.enumeration.EuNorm;
import com.uta.api.enumeration.FuelType;
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

        List<FuelTransactionFromCSVDto> validTransactions = filterValidTransactions(allTransactions, startDate);


        Map<String, List<FuelTransactionFromCSVDto>> transactionsByVehicle = validTransactions.stream()
                .filter(t -> t.registrationNumber() != null && !t.registrationNumber().isBlank())
                .collect(Collectors.groupingBy(t -> normalizeRegistrationNumber(t.registrationNumber())));

        List<VehicleFuelUsage> vehicleFuelUsages = transactionsByVehicle.entrySet().stream()
                .map(entry -> {
                    String regNumber = entry.getKey();
                    List<FuelDTO> fuels = calculateFuelUsage(entry.getValue());
                    return new VehicleFuelUsage(regNumber, fuels);
                })
                .sorted(Comparator.comparing(v -> v.fuelUsage().stream()
                        .map(FuelDTO::quantity)
                        .reduce(BigDecimal.ZERO, BigDecimal::add), Comparator.reverseOrder())).toList();


        log.info("Obliczono zużycie dla {} pojazdów", vehicleFuelUsages.size());

        BigDecimal dieselTotal = calculateTotalByFuelType(validTransactions, FuelType.DIESEL);
        BigDecimal petrolTotal = calculateTotalByFuelType(validTransactions, FuelType.GASOLINE);
        BigDecimal total = validTransactions.stream().map(FuelTransactionFromCSVDto::quantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new VehiclesFuelConsumptionSummary(dieselTotal, petrolTotal, total, vehicleFuelUsages);
    }


    @Override
    public VehiclesFuelConsumptionSummaryAggregated getActualVehicleFuelUsageAggregated(LocalDate startDate) {
        log.info("Rozpoczynam obliczanie zagregowanego zużycia paliwa wg norm EURO od daty: {}", startDate);

        List<FuelTransactionFromCSVDto> allTransactions = csvService.importAllFromDirectory();

        List<FuelTransactionFromCSVDto> validTransactions = filterValidTransactions(allTransactions, startDate);

        Map<String, EuNorm> registrationToNormMap = Arrays.stream(CarsDetails.values())
                .collect(Collectors.toMap(
                        car -> normalizeRegistrationNumber(car.getRegistrationNumber()),
                        CarsDetails::getNorm,
                        (existing, replacement) -> existing
                ));

        Map<EuNorm, List<FuelTransactionFromCSVDto>> transactionsByNorm = validTransactions
                .stream()
                .filter(t -> t.registrationNumber() != null && !t.registrationNumber().isBlank())
                .filter(t -> registrationToNormMap.containsKey(normalizeRegistrationNumber(t.registrationNumber())))
                .collect(Collectors.groupingBy(
                        t -> registrationToNormMap.get(normalizeRegistrationNumber(t.registrationNumber()))
                ));

        List<ConsumptionByNormDTO> aggregatedByNorm = transactionsByNorm.entrySet().stream()
                .map(entry -> {
                    EuNorm norm = entry.getKey();
                    List<FuelDTO> fuels = calculateFuelUsage(entry.getValue());
                    return new ConsumptionByNormDTO(norm, fuels);
                })
                .sorted(Comparator.comparing(ConsumptionByNormDTO::norm))
                .toList();

        BigDecimal totalFromNorm = aggregatedByNorm.stream()
                .flatMap(dto -> dto.fuelUsage().stream())
                .map(FuelDTO::quantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal dieselUsage = calculateTotalByFuelType(validTransactions, FuelType.DIESEL);
        BigDecimal petrolUsage = calculateTotalByFuelType(validTransactions, FuelType.GASOLINE);
        BigDecimal totalUsage = validTransactions.stream()
                .map(FuelTransactionFromCSVDto::quantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        log.info("Zgrupowano zużycie dla {} norm EURO", aggregatedByNorm.size());

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
        log.info("Rozpoczynam pobieranie transakcji dla pojazdu o numerach: {} od daty: {}", registrationNumber, startDate);

        List<FuelTransactionFromCSVDto> allTransactions = csvService.importAllFromDirectory();

        List<FuelTransactionFromCSVDto> result = filterValidTransactions(allTransactions, startDate).stream()
                .filter(t -> t.registrationNumber() != null && !t.registrationNumber().isBlank())
                .filter(t -> normalizeRegistrationNumber(t.registrationNumber())
                        .equalsIgnoreCase(normalizeRegistrationNumber(registrationNumber)))
                .sorted(Comparator.comparing(FuelTransactionFromCSVDto::deliveryDate))
                .toList();

        log.info("Znaleziono {} transakcji dla pojazdu {}", result.size(), registrationNumber);
        return result;
    }

    /**
     * Znajduje transakcje z "małą" ilością paliwa dla pojazdów (nie WAG).
     * Przydatne do wykrywania dolewek, tankowania sprzętu ogrodniczego itp.
     *
     * @param startDate         data początkowa analizy
     * @param thresholdInLiters próg litrów, poniżej którego uznajemy tankowanie za małe (np. 15.0)
     */

    /**
     * Znajduje transakcje, w których zatankowano inny typ paliwa niż dominujący dla danego pojazdu.
     * Np. Auto ma 1000L Diesla i 5L Benzyny -> Transakcje Benzynowe są zwracane.
     */
    @Override
    public ConsumerCSVResponse findMisfuelTransactions(LocalDate startDate) {
        log.info("Poszukiwanie anomalii paliwowych (zły typ paliwa) od daty: {}", startDate);

        List<FuelTransactionFromCSVDto> allTransactions = csvService.importAllFromDirectory();

        // 1. Filtrowanie wstępne
        List<FuelTransactionFromCSVDto> validTransactions = filterValidTransactions(allTransactions, startDate);

        // 2. Grupowanie po pojeździe (pomijamy WAG)
        Map<String, List<FuelTransactionFromCSVDto>> transactionsByVehicle = validTransactions.stream()
                .filter(t -> t.registrationNumber() != null
                        && !normalizeRegistrationNumber(t.registrationNumber()).startsWith("WAG"))
                .collect(Collectors.groupingBy(t -> normalizeRegistrationNumber(t.registrationNumber())));

        List<FuelTransactionFromCSVDto> suspiciousTransactions = new ArrayList<>();

        // 3. Analiza każdego pojazdu
        for (Map.Entry<String, List<FuelTransactionFromCSVDto>> entry : transactionsByVehicle.entrySet()) {
            List<FuelTransactionFromCSVDto> vehicleTransactions = entry.getValue();

            // Obliczamy sumy dla każdego typu paliwa dla tego konkretnego auta
            Map<FuelType, BigDecimal> fuelSums = vehicleTransactions.stream()
                    .map(t -> {
                        FuelType type = resolveFuelType(t.productName());
                        return type != null ? Map.entry(type, t.quantity()) : null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(
                            Map.Entry::getKey,
                            Collectors.reducing(BigDecimal.ZERO, Map.Entry::getValue, BigDecimal::add)
                    ));

            // Jeśli auto tankowało tylko jeden rodzaj paliwa (lub żadnego znanego), pomijamy je
            if (fuelSums.size() < 2) {
                continue;
            }

            // Znajdujemy paliwo dominujące (to z największą ilością)
            FuelType dominantFuel = Collections.max(fuelSums.entrySet(), Map.Entry.comparingByValue()).getKey();

            // Wyłapujemy transakcje, które NIE są paliwem dominującym
            List<FuelTransactionFromCSVDto> anomalies = vehicleTransactions.stream()
                    .filter(t -> {
                        FuelType currentType = resolveFuelType(t.productName());
                        // Zwracamy transakcję, jeśli typ jest znany (Diesel/Benzyna) ALE inny niż dominujący
                        return currentType != null && currentType != dominantFuel;
                    })
                    .toList();

            suspiciousTransactions.addAll(anomalies);
        }

        log.info("Znaleziono {} podejrzanych transakcji z błędnym typem paliwa", suspiciousTransactions.size());

        // Sortowanie po dacie (opcjonalne)
        List<FuelTransactionFromCSVDto> transactions = suspiciousTransactions.stream()
                .sorted(Comparator.comparing(FuelTransactionFromCSVDto::deliveryDate))
                .toList();
        return new ConsumerCSVResponse(transactions, transactions.size());
    }


    // --- METODY POMOCNICZE (PRIVATE) ---

    /**
     * Główna logika filtrowania transakcji (Data, Jednostka, Null Check)
     */
    private List<FuelTransactionFromCSVDto> filterValidTransactions(List<FuelTransactionFromCSVDto> transactions, LocalDate startDate) {
        return transactions.stream()
                .filter(t -> t.deliveryDate() != null)
                .filter(t -> !t.deliveryDate().toLocalDate().isBefore(startDate))
                .filter(t -> "LTR".equalsIgnoreCase(t.unitOfMeasure()))
                .filter(t -> t.quantity() != null)
                .toList();
    }

    /**
     * Agreguje listę transakcji do listy FuelDTO (sumuje quantity per FuelType)
     */
    private List<FuelDTO> calculateFuelUsage(List<FuelTransactionFromCSVDto> transactions) {
        // Grupowanie po typie paliwa i sumowanie ilości
        Map<FuelType, BigDecimal> fuelMap = transactions.stream()
                .map(t -> {
                    FuelType type = resolveFuelType(t.productName());
                    return Map.entry(type != null ? type : FuelType.DIESEL, t.quantity()); // Domyślnie Diesel jeśli null, lub można filtrować
                    // Wersja bezpieczniejsza (filtrowanie nieznanych):
                    // FuelType type = resolveFuelType(t.productName());
                    // return type != null ? Map.entry(type, t.quantity()) : null;
                })
                // .filter(Objects::nonNull) // Odkomentuj jeśli chcesz pomijać nieznane paliwa (np. AdBlue)
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.reducing(BigDecimal.ZERO, Map.Entry::getValue, BigDecimal::add)
                ));

        return fuelMap.entrySet().stream()
                .map(e -> new FuelDTO(e.getValue(), e.getKey()))
                .toList();
    }

    /**
     * Rozpoznaje typ paliwa na podstawie nazwy produktu z CSV
     */
    private FuelType resolveFuelType(String productName) {
        if (productName == null) return null;
        String lowerName = productName.toLowerCase();

        if (lowerName.contains("olej napędowy") || lowerName.contains("diesel")) {
            return FuelType.DIESEL;
        } else if (lowerName.contains("etylina") || lowerName.contains("benzyna")) {
            return FuelType.GASOLINE;
        }
        // Można dodać obsługę AdBlue, LPG itp.
        return null;
    }

    /**
     * Oblicza sumę dla konkretnego typu paliwa z całej listy transakcji
     */
    private BigDecimal calculateTotalByFuelType(List<FuelTransactionFromCSVDto> transactions, FuelType targetType) {
        return transactions.stream()
                .filter(t -> {
                    FuelType type = resolveFuelType(t.productName());
                    return type == targetType;
                })
                .map(FuelTransactionFromCSVDto::quantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
