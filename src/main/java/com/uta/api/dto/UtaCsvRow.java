package com.uta.api.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class UtaCsvRow {

    @CsvBindByName(column = " Numer Klienta")
    private String clientNumber;

    @CsvBindByName(column = " Pełny numer karty")
    private String fullCardNumber;

    @CsvBindByName(column = " Data transakcji")
    private String transactionDate; // Format in CSV: d-MM-yyyy

    @CsvBindByName(column = "Czas transakcji") // Note: No leading space here in your CSV
    private String transactionTime; // Format in CSV: HH:mm:ss

    @CsvBindByName(column = " Numer ID stacji")
    private Long stationId;

    @CsvBindByName(column = " Lokalizacja stacji")
    private String stationLocation; // e.g. "ESSO,Auetal Nord"

    @CsvBindByName(column = " Kraj dostawy")
    private String deliveryCountry;

    @CsvBindByName(column = "Kod pocztowy miejsca dostawy")
    private String stationZipCode;

    @CsvBindByName(column = "Kod produktu")
    private String productCodeRaw; // e.g. "01110 Olej napędowy"

    @CsvBindByName(column = " Ilość")
    private String quantity;

    @CsvBindByName(column = " Waluta kraju dostawcy")
    private String currency;

    @CsvBindByName(column = " Cena jednostkowa netto w walucie kraju dostawcy")
    private String unitPriceNet;

    @CsvBindByName(column = " Cena jednostkowa brutto w walucie kraju dostawcy")
    private String unitPriceGross;

    @CsvBindByName(column = " Łączna wartość netto w walucie kraju dostawcy")
    private String totalNetValue;

    @CsvBindByName(column = " Łączna wartość brutto w walucie kraju dostawcy")
    private String totalGrossValue;

    @CsvBindByName(column = "Nr pokwitowania UTA")
    private String utaReceiptNumber;

    @CsvBindByName(column = " Numer pokwitowania")
    private String receiptNumber;

    @CsvBindByName(column = " Stan licznika")
    private Long odometerReading;

    @CsvBindByName(column = " Miejsce powstawania kosztów")
    private String costCenter;

    @CsvBindByName(column = " Rodzaj karty")
    private String cardCategory;

    @CsvBindByName(column = "Nr rejestracyjny pojazdu")
    private String registrationNumber;

    @CsvBindByName(column = " Stawka VAT")
    private String vatRate;

    @CsvBindByName(column = "Przedstawiciel")
    private String supplierName;
}
