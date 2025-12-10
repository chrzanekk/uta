package com.uta.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record FuelTransactionDto(
        @JsonProperty("Identyfikator")
        UUID id,

        @JsonProperty("NazwaKlienta")
        String clientName,

        @JsonProperty("IdDostawcy")
        Long supplierId,

        @JsonProperty("NazwaDostawcy")
        String supplierName,

        @JsonProperty("DataDostawy")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime deliveryDate,

        @JsonProperty("CzasDostawy")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime deliveryTime,

        @JsonProperty("Kraj")
        String countryCode,

        @JsonProperty("PunktAkceptacji")
        Long acceptancePointId,

        @JsonProperty("MarkaKoncern")
        String brandGroup,

        @JsonProperty("Miejscowosc")
        String city,

        @JsonProperty("KodPocztowyStacji")
        String stationZipCode,

        @JsonProperty("StawkaVAT")
        BigDecimal vatRate,

        @JsonProperty("NrRejestr")
        String registrationNumber,

        @JsonProperty("StanLicznika")
        Long odometerReading,

        @JsonProperty("MiejsceKosztu")
        String costCenter,

        @JsonProperty("MiejsceKosztu2")
        String costCenter2,

        @JsonProperty("KategoriaKarty")
        String cardCategory,

        @JsonProperty("NrKarty")
        BigDecimal cardShortNumber, // W JSON jest jako liczba (63.0000), choć logicznie to string/int

        @JsonProperty("PelnyNumerKarty")
        String fullCardNumber,

        @JsonProperty("Produkt")
        String product,

        @JsonProperty("KodProduktu")
        String productCode,

        @JsonProperty("Ilosc")
        BigDecimal quantity,

        @JsonProperty("Waluta")
        String currency,

        @JsonProperty("CenaJednostkowaNetto")
        BigDecimal unitPriceNet,

        @JsonProperty("CenaJedn")
        BigDecimal unitPriceGross, // Zakładam brutto na podstawie wartości (6.59 vs 5.35)

        @JsonProperty("WartoscNetto")
        BigDecimal netValue,

        @JsonProperty("Wartosc")
        BigDecimal grossValue,

        @JsonProperty("UTAVoucherNumber")
        String utaVoucherNumber,

        @JsonProperty("VoucherNr")
        String voucherNumber
) {
}
