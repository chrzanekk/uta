package com.uta.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.uta.api.dto.ConsumerResponse;
import com.uta.api.dto.FuelTransactionDto;
import com.uta.api.service.FuelTransactionImportService;
import com.uta.api.service.UtaTransactionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/uta/")
public class UtaController {

    private final UtaTransactionsService utaTransactionsService;
    private final FuelTransactionImportService fuelTransactionImportService;

    @GetMapping("/all")
    public ResponseEntity<ConsumerResponse> getAllUtaTransactions() throws JsonProcessingException {
        ConsumerResponse response = utaTransactionsService.getTransactions();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/csv")
    public ResponseEntity<ConsumerResponse> getAllUtaTransactionsCsv() throws JsonProcessingException {
        List<FuelTransactionDto> transactionDtos = fuelTransactionImportService.importAllFromDirectory();
        ConsumerResponse response = new ConsumerResponse(transactionDtos, transactionDtos.size());
        return ResponseEntity.ok(response);
    }

}
