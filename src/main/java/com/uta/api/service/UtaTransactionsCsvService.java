package com.uta.api.service;

import com.uta.api.dto.FuelTransactionFromCSVDto;

import java.util.List;

public interface UtaTransactionsCsvService {

    List<FuelTransactionFromCSVDto> importAllFromDirectory();
}
