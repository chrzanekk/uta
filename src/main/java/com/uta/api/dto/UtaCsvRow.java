package com.uta.api.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class UtaCsvRow {

    // --- Sekcja 1: Dane podstawowe klienta i karty ---
    @CsvBindByName(column = "Numer rachunku")
    private String accountNumber;

    @CsvBindByName(column = "Data za dostawy i usługi do dnia")
    private String billingPeriodEnd;

    @CsvBindByName(column = " ID Partnera rozliczeniowego")
    private String billingPartnerId;

    @CsvBindByName(column = " Numer Klienta")
    private String clientNumber;

    @CsvBindByName(column = " Pełny numer karty")
    private String fullCardNumber;

    @CsvBindByName(column = " Tekst karty")
    private String cardText;

    @CsvBindByName(column = " Typ karty")
    private String cardType;

    @CsvBindByName(column = " Rodzaj karty")
    private String cardCategory;

    @CsvBindByName(column = "Numer obcej karty")
    private String foreignCardNumber;


    // --- Sekcja 2: Transakcja (Czas i Miejsce) ---
    @CsvBindByName(column = " Data transakcji")
    private String transactionDate; // Format: d-MM-yyyy

    @CsvBindByName(column = "Czas transakcji")
    private String transactionTime; // Format: HH:mm:ss

    @CsvBindByName(column = " Numer ID stacji")
    private Long stationId;

    @CsvBindByName(column = " Lokalizacja stacji")
    private String stationLocation;

    @CsvBindByName(column = " Kraj dostawy")
    private String deliveryCountry;

    @CsvBindByName(column = "Kraj miejsca dostawy")
    private String placeOfDeliveryCountry;

    @CsvBindByName(column = "Kod pocztowy miejsca dostawy")
    private String stationZipCode;

    @CsvBindByName(column = "Kraj opodatkowania")
    private String taxationCountry;

    @CsvBindByName(column = "ID środka akceptacji")
    private String acceptancePointId;


    // --- Sekcja 3: Produkt i Ilość ---
    @CsvBindByName(column = "Kod produktu")
    private String productCodeRaw;

    @CsvBindByName(column = " Ilość")
    private String quantity;

    @CsvBindByName(column = "Jednostka miary")
    private String unitOfMeasure;

    @CsvBindByName(column = " Stan licznika")
    private Long odometerReading;

    @CsvBindByName(column = " Samoobsługa / Obsługa")
    private String selfServiceOrAttendant;


    // --- Sekcja 4: Finanse (Waluta Kraju Dostawcy) ---
    @CsvBindByName(column = " Waluta kraju dostawcy")
    private String currencySupplier;

    @CsvBindByName(column = " Cena jednostkowa netto w walucie kraju dostawcy")
    private String unitPriceNetSupplier;

    @CsvBindByName(column = " Cena jednostkowa brutto w walucie kraju dostawcy")
    private String unitPriceGrossSupplier;

    @CsvBindByName(column = " Łączna wartość netto w walucie kraju dostawcy")
    private String totalNetValueSupplier;

    @CsvBindByName(column = " Łączna wartość brutto w walucie kraju dostawcy")
    private String totalGrossValueSupplier;

    @CsvBindByName(column = " Rabat w walucie kraju dostawcy brutto")
    private String discountGrossSupplier;

    @CsvBindByName(column = "Cena jednostkowa brutto w walucie kraju dostawy")
    private String unitPriceGrossDelivery;

    @CsvBindByName(column = "Cena jednostkowa netto w walucie kraju dostawy")
    private String unitPriceNetDelivery;


    // --- Sekcja 5: Finanse (Waluta Rozliczeniowa - zwykle PLN) ---
    @CsvBindByName(column = " Waluta rozliczeniowa")
    private String currencySettlement;

    @CsvBindByName(column = " Opłata serwisowa w walucie rozliczeniowej brutto")
    private String serviceFeeGrossSettlement; // Może wystąpić konflikt nazw, sprawdź w CSV czy kolumny są unikalne

    // Uwaga: W Twoim CSV były dwie kolumny o podobnej nazwie, OpenCSV mapuje po nagłówku.
    // Jeśli nazwy są identyczne, OpenCSV weźmie ostatnią lub rzuci błąd.
    // W analizie Pythona widziałem "Opłata serwisowa...brutto.1", co sugeruje duplikat w Excelu/Pandas, ale w CSV tekstowym nagłówki są tekstami.

    @CsvBindByName(column = " Rabat w walucie rozliczeniowej brutto")
    private String discountGrossSettlement;

    @CsvBindByName(column = " Łączna wartość netto w walucie rozliczeniowej")
    private String totalNetValueSettlement;

    @CsvBindByName(column = " Łączna wartość VAT w walucie rozliczeniowej")
    private String totalVatValueSettlement;

    @CsvBindByName(column = " Łączna wartość brutto w walucie rozliczeniowej")
    private String totalGrossValueSettlement;

    @CsvBindByName(column = " Stawka VAT")
    private String vatRate;

    @CsvBindByName(column = "Stawka Vat") // Druga kolumna z 'Vat' małą literą na końcu pliku
    private String vatRateAlternative;


    // --- Sekcja 6: Dokumenty i Numery ---
    @CsvBindByName(column = " Numer pokwitowania")
    private String receiptNumber;

    @CsvBindByName(column = "Nr pokwitowania UTA")
    private String utaReceiptNumber;

    @CsvBindByName(column = "Dodatkowy nr pokwitowania UTA")
    private String additionalUtaReceiptNumber;

    @CsvBindByName(column = "Data faktury")
    private String invoiceDate;

    @CsvBindByName(column = "Nr faktury wg kraju")
    private String countryInvoiceNumber;

    @CsvBindByName(column = "TC numer faktury (autostrady niemieckie)")
    private String tcFkNumber;


    // --- Sekcja 7: Pojazd i Koszty ---
    @CsvBindByName(column = "Nr rejestracyjny pojazdu")
    private String registrationNumber;

    @CsvBindByName(column = "NrRejPojazduKompr")
    private String compressedRegistrationNumber;

    @CsvBindByName(column = " Miejsce powstawania kosztów")
    private String costCenter;

    @CsvBindByName(column = "ID urządzenia OBU")
    private String obuId;


    // --- Sekcja 8: Autostrady i Myto ---
    @CsvBindByName(column = "Wjazd na autostradę")
    private String entryHighway;

    @CsvBindByName(column = "Wyjazd z autostrady")
    private String exitHighway;

    @CsvBindByName(column = " Warunki specjalne Francja autostrady")
    private String specialConditionsFrance;

    @CsvBindByName(column = "Winieta ważna od")
    private String vignetteValidFrom;

    @CsvBindByName(column = "Winieta ważna do")
    private String vignetteValidTo;


    // --- Sekcja 9: Pozostałe / Techniczne ---
    @CsvBindByName(column = "Przedstawiciel")
    private String supplierName;

    @CsvBindByName(column = "Termin płatnosci (w dniach)")
    private String paymentTermDays;

    @CsvBindByName(column = "DataWymagalnosci")
    private String dueDate;

    @CsvBindByName(column = "Kategoria podatkowa")
    private String taxCategory;

    @CsvBindByName(column = "Indeks")
    private String index;

    @CsvBindByName(column = "Data przetworzenia pliku")
    private String fileProcessingDate;

    @CsvBindByName(column = "TypTransakcji")
    private String transactionType;

    @CsvBindByName(column = "PowodWpisu")
    private String entryReason;

    @CsvBindByName(column = "NotaInformacyjna")
    private String infoNote;

    @CsvBindByName(column = "ZrodloNoty")
    private String noteSource;

    @CsvBindByName(column = "Pole informacyjne")
    private String infoField;

    @CsvBindByName(column = "PodatkowaGrupaProduktowa")
    private String taxProductGroup;

    @CsvBindByName(column = "ZmiennoscMiejscaSwUslug")
    private String servicePlaceVariability;

    @CsvBindByName(column = "KatWarPlatnosci")
    private String paymentConditionCategory;

    @CsvBindByName(column = "NrNotyObciazeniowej")
    private String debitNoteNumber;

    @CsvBindByName(column = "IdWystawcyUzytkownika")
    private String userIssuerId;

    // Pola "wypełniacze" lub techniczne
    @CsvBindByName(column = "Bitmap")
    private String bitmap;

    @CsvBindByName(column = "Wypelnienie43")
    private String filler43;

    @CsvBindByName(column = "Wypelnienie46")
    private String filler46;

    // Pola VAT dodatkowe
    @CsvBindByName(column = "WartoscVatOdUpustuDostawy")
    private String vatValueFromDeliveryDiscount;

    @CsvBindByName(column = "WartoscVATDoplatyServisowejDostawy")
    private String vatValueFromServiceSurcharge;

    @CsvBindByName(column = "LcznyVatDoplServisowejDostawy")
    private String totalVatServiceSurcharge;
}
