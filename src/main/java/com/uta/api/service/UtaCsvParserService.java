package com.uta.api.service;

import com.opencsv.bean.CsvToBeanBuilder;
import com.uta.api.dto.UtaCsvRow;
import com.uta.api.dto.FuelTransactionFromCSVDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger log = LoggerFactory.getLogger(UtaCsvParserService.class);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d-MM-yyyy");
    // Czasami w plikach CSV czas może być różnie formatowany, 'HH:mm:ss' jest standardem
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public List<FuelTransactionFromCSVDto> parseCsv(Reader reader) {
        List<UtaCsvRow> csvRows = new CsvToBeanBuilder<UtaCsvRow>(reader)
                .withType(UtaCsvRow.class)
                .withSeparator(';')
                .withIgnoreLeadingWhiteSpace(true)
                .build()
                .parse();

        return csvRows.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private FuelTransactionFromCSVDto mapToDto(UtaCsvRow row) {
        // 1. Data i czas
        LocalDateTime dateTime = null;
        try {
            if (row.getTransactionDate() != null && !row.getTransactionDate().trim().isEmpty()) {
                LocalDate date = LocalDate.parse(row.getTransactionDate().trim(), DATE_FORMATTER);
                LocalTime time = LocalTime.MIN; // Domyślnie północ
                if (row.getTransactionTime() != null && !row.getTransactionTime().trim().isEmpty()) {
                    time = LocalTime.parse(row.getTransactionTime().trim(), TIME_FORMATTER);
                }
                dateTime = LocalDateTime.of(date, time);
            }
        } catch (Exception e) {
             log.warn("Problem parsing date for row: {}", row);
        }

        // 2. Produkt (Rozdzielenie kodu i nazwy)
        String productCode = row.getProductCodeRaw();
        String productName = "";

        if (row.getProductCodeRaw() != null && row.getProductCodeRaw().contains(" ")) {
            String[] parts = row.getProductCodeRaw().split(" ", 2);
            productCode = parts[0];
            productName = parts[1];
        }

        // 3. Lokalizacja (Marka i Miasto)
        String brand = "";
        String city = row.getStationLocation();
        if (row.getStationLocation() != null) {
            if (row.getStationLocation().contains(",")) {
                String[] locParts = row.getStationLocation().split(",", 2);
                brand = locParts[0].trim();
                city = locParts[1].trim();
            } else {
                city = row.getStationLocation().trim();
            }
        }

        return new FuelTransactionFromCSVDto(
                UUID.randomUUID(),
                row.getClientNumber(),
                row.getBillingPartnerId(),
                row.getSupplierName(),
                dateTime,
                row.getDeliveryCountry(),
                row.getPlaceOfDeliveryCountry(),
                row.getStationId(),
                brand,
                city,
                row.getStationZipCode() != null ? row.getStationZipCode().trim() : null,
                parseDecimal(row.getVatRate()),
                row.getRegistrationNumber() != null ? row.getRegistrationNumber().trim() : null,
                row.getOdometerReading(),
                row.getCostCenter() != null ? row.getCostCenter().trim() : null,
                row.getCardCategory(),
                row.getFullCardNumber() != null ? row.getFullCardNumber().trim() : null,
                row.getForeignCardNumber() != null ? row.getForeignCardNumber().trim() : null,
                productName,
                productCode,
                parseDecimal(row.getQuantity()),
                row.getUnitOfMeasure() != null ? row.getUnitOfMeasure().trim() : null,

                // Waluta dostawcy (EUR itp.)
                row.getCurrencySupplier(),
                parseDecimal(row.getUnitPriceNetSupplier()),
                parseDecimal(row.getUnitPriceGrossSupplier()),
                parseDecimal(row.getTotalNetValueSupplier()),
                parseDecimal(row.getTotalGrossValueSupplier()),

                // Waluta rozliczeniowa (PLN)
                row.getCurrencySettlement(),
                parseDecimal(row.getTotalNetValueSettlement()),
                parseDecimal(row.getTotalVatValueSettlement()),
                parseDecimal(row.getTotalGrossValueSettlement()),

                // Dokumenty
                row.getReceiptNumber() != null ? row.getReceiptNumber().trim() : null,
                row.getUtaReceiptNumber(),
                row.getCountryInvoiceNumber(),

                // Autostrady
                row.getEntryHighway() != null ? row.getEntryHighway().trim() : null,
                row.getExitHighway() != null ? row.getExitHighway().trim() : null,
                row.getObuId() != null ? row.getObuId().trim() : null
        );
    }

    private BigDecimal parseDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
