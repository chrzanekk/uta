package com.uta.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record FuelTransactionFromCSVDto(
        @JsonProperty("Identyfikator")
        UUID id,

        @JsonProperty("NumerKlienta")
        String clientNumber,

        @JsonProperty("IDPartneraRozliczeniowego")
        String billingPartnerId,

        @JsonProperty("NazwaDostawcy")
        String supplierName, // Przedstawiciel

        @JsonProperty("DataDostawy")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime deliveryDate,

        @JsonProperty("Kraj")
        String countryCode,

        @JsonProperty("KrajMiejscaDostawy")
        String placeOfDeliveryCountry,

        @JsonProperty("PunktAkceptacji")
        Long acceptancePointId, // Numer ID stacji (long) lub ID środka akceptacji (string) - w CSV są oba

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

        @JsonProperty("KategoriaKarty")
        String cardCategory,

        @JsonProperty("PelnyNumerKarty")
        String fullCardNumber,

        @JsonProperty("NumerObcejKarty")
        String foreignCardNumber,

        @JsonProperty("Produkt")
        String productName,

        @JsonProperty("KodProduktu")
        String productCode,

        @JsonProperty("Ilosc")
        BigDecimal quantity,

        @JsonProperty("JednostkaMiary")
        String unitOfMeasure,

        // --- Waluta Kraju Dostawcy (Oryginalna) ---
        @JsonProperty("WalutaKrajuDostawcy")
        String currencySupplier,

        @JsonProperty("CenaJednNettoWalutaDostawcy")
        BigDecimal unitPriceNetSupplier,

        @JsonProperty("CenaJednBruttoWalutaDostawcy")
        BigDecimal unitPriceGrossSupplier,

        @JsonProperty("WartoscNettoWalutaDostawcy")
        BigDecimal totalNetValueSupplier,

        @JsonProperty("WartoscBruttoWalutaDostawcy")
        BigDecimal totalGrossValueSupplier,

        // --- Waluta Rozliczeniowa (np. PLN) ---
        @JsonProperty("WalutaRozliczeniowa")
        String currencySettlement,

        @JsonProperty("WartoscNettoWalutaRozliczeniowa")
        BigDecimal totalNetValueSettlement,

        @JsonProperty("WartoscVATWalutaRozliczeniowa")
        BigDecimal totalVatValueSettlement,

        @JsonProperty("WartoscBruttoWalutaRozliczeniowa")
        BigDecimal totalGrossValueSettlement,

        // --- Dokumenty ---
        @JsonProperty("UTAVoucherNumber")
        String utaReceiptNumber,

        @JsonProperty("VoucherNr")
        String receiptNumber, // Numer pokwitowania

        @JsonProperty("NrFakturyWgKraju")
        String countryInvoiceNumber,

        // --- Autostrady ---
        @JsonProperty("WjazdNaAutostrade")
        String entryHighway,

        @JsonProperty("WyjazdZAutostrady")
        String exitHighway,

        @JsonProperty("OBUID")
        String obuId
) {
}
