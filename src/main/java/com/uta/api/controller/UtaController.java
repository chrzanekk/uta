package com.uta.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.uta.api.dto.ConsumerApiResponse;
import com.uta.api.dto.ConsumerCSVResponse;
import com.uta.api.dto.FuelTransactionFromCSVDto;
import com.uta.api.dto.VehicleFuelUsage;
import com.uta.api.service.UtaTransactionsApiService;
import com.uta.api.service.UtaTransactionsCalculationsService;
import com.uta.api.service.UtaTransactionsCsvService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/uta/")
public class UtaController {

    private final UtaTransactionsApiService utaTransactionsApiService;
    private final UtaTransactionsCsvService utaTransactionsCsvService;
    private final UtaTransactionsCalculationsService utaTransactionsCalculationsService;

    @GetMapping("/all")
    public ResponseEntity<ConsumerApiResponse> getAllUtaTransactions() throws JsonProcessingException {
        ConsumerApiResponse response = utaTransactionsApiService.getTransactions();
        if(response == null) {
            ConsumerApiResponse empty = new ConsumerApiResponse(Collections.emptyList(), 0);
            return new ResponseEntity<>(empty, HttpStatus.OK);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/csv")
    public ResponseEntity<ConsumerCSVResponse> getAllUtaTransactionsCsv() {
        List<FuelTransactionFromCSVDto> transactionDtos = utaTransactionsCsvService.importAllFromDirectory();
        ConsumerCSVResponse response = new ConsumerCSVResponse(transactionDtos, transactionDtos.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/fuel-usage")
    public ResponseEntity<List<VehicleFuelUsage>> getFuelUsage(
            @RequestParam(value = "startDate", defaultValue = "2025-01-01")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        List<VehicleFuelUsage> result = utaTransactionsCalculationsService.getActualVehicleFuelUsage(startDate);
        return ResponseEntity.ok(result);
    }
}
