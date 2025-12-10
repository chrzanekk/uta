package com.uta.api.service;

import com.uta.api.dto.FuelTransactionDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class FuelTransactionImportService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final UtaCsvParserService utaCsvParserService;

    @Value("${csv.directory:csv}")
    private String csvDirectory;

    public List<FuelTransactionDto> importAllFromDirectory() {
        List<FuelTransactionDto> allTransactions = new ArrayList<>();

        Path folderPath = Paths.get(csvDirectory);

        log.info("Rozpoczynam import z katalogu: {} (Pełna ścieżka: {})", csvDirectory, folderPath.toAbsolutePath());

        if (!Files.exists(folderPath) || !Files.isDirectory(folderPath)) {
            log.error("Skonfigurowany katalog nie istnieje: {}", folderPath.toAbsolutePath());
            return allTransactions;
        }

        try (Stream<Path> pathStream = Files.list(folderPath)) {
            List<Path> csvFiles = pathStream
                    .filter(path -> !Files.isDirectory(path))
                    .filter(path -> path.toString().toLowerCase().endsWith(".csv"))
                    .toList();
            if (csvFiles.isEmpty()) {
                log.warn("Nie znaleziono plików CSV w katalogu: {}", folderPath.toAbsolutePath());
                return allTransactions;
            }

            for (Path csvFile : csvFiles) {
                allTransactions.addAll(processSingeFile(csvFile));
            }
        } catch (IOException e) {
            log.error("Błąd podczas dostępu do katalogu: {}", folderPath, e);
            throw new RuntimeException("Błąd wejścia/wyjścia podczas odczytu katalogu", e);
        }

        log.info("Zakończono import. Przetworzono łącznie {} transakcji.", allTransactions.size());
        return allTransactions;
    }


    private List<FuelTransactionDto> processSingeFile(Path filePath) {
        log.info("Przetwarzanie pliku: {}", filePath.getFileName());
        try (Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            List<FuelTransactionDto> transactions = utaCsvParserService.parseCsv(reader);
            log.info("Plik {} przetworzony pomyślnie. Znaleziono {} rekordów.", filePath.getFileName(), transactions.size());
            return transactions;
        } catch (IOException e) {
            log.error("Błąd podczas przetwarzania pliku: {}", filePath.getFileName(), e);
            return new ArrayList<>();
        }
    }
}
