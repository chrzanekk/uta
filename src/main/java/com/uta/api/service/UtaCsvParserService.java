package com.uta.api.service;

import com.opencsv.bean.CsvToBeanBuilder;
import com.uta.api.csv.UtaCsvRow;
import com.uta.api.dto.FuelTransactionDto;
import org.springframework.stereotype.Service;

import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UtaCsvParserService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d-MM-yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public List<FuelTransactionDto> parseCsv(Reader reader) {
        List<UtaCsvRow> csvRows = new CsvToBeanBuilder<UtaCsvRow>(reader)
                .withType(UtaCsvRow.class)
                .withSeparator(';') // Separator to średnik!
                .withIgnoreLeadingWhiteSpace(true)
                .build()
                .parse();

        return csvRows.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private FuelTransactionDto mapToDto(UtaCsvRow row) {
        // 1. Parsowanie daty i czasu (teraz używamy angielskich getterów)
        LocalDate date = LocalDate.parse(row.getTransactionDate(), DATE_FORMATTER);
        LocalTime time = LocalTime.parse(row.getTransactionTime(), TIME_FORMATTER);
        LocalDateTime dateTime = LocalDateTime.of(date, time);

        // 2. Parsowanie Produktu
        String productCode = "";
        String productName = row.getProductCodeRaw();
        if (row.getProductCodeRaw() != null && row.getProductCodeRaw().contains(" ")) {
            String[] parts = row.getProductCodeRaw().split(" ", 2);
            productCode = parts[0];
            productName = parts[1];
        }

        // 3. Wyciąganie marki z lokalizacji
        String brand = "";
        String city = row.getStationLocation();
        if (row.getStationLocation() != null && row.getStationLocation().contains(",")) {
            String[] locParts = row.getStationLocation().split(",", 2);
            brand = locParts[0];
            city = locParts[1];
        }

        return new FuelTransactionDto(
                UUID.randomUUID(),
                row.getClientNumber(),
                null,
                row.getSupplierName(),
                dateTime,
                dateTime,
                row.getDeliveryCountry(),
                row.getStationId(),
                brand,
                city != null ? city.trim() : null,
                row.getStationZipCode() != null ? row.getStationZipCode().trim() : null,
                parseDecimal(row.getVatRate()),
                row.getRegistrationNumber() != null ? row.getRegistrationNumber().trim() : null,
                row.getOdometerReading(),
                row.getCostCenter() != null ? row.getCostCenter().trim() : null,
                null,
                row.getCardCategory(),
                null,
                row.getFullCardNumber() != null ? row.getFullCardNumber().trim() : null,
                productName,
                productCode,
                parseDecimal(row.getQuantity()),
                row.getCurrency(),
                parseDecimal(row.getUnitPriceNet()),
                parseDecimal(row.getUnitPriceGross()),
                parseDecimal(row.getTotalNetValue()),
                parseDecimal(row.getTotalGrossValue()),
                row.getUtaReceiptNumber(),
                row.getReceiptNumber() != null ? row.getReceiptNumber().trim() : null
        );

}

    private BigDecimal parseDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        // CSV używa kropki, więc standardowy konstruktor zadziała.
        // Jeśli pojawiłyby się przecinki, trzeba użyć value.replace(",", ".")
        return new BigDecimal(value.trim());
    }
}
