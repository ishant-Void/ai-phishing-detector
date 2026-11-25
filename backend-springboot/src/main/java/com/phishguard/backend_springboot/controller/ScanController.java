package com.phishguard.backend_springboot.controller;

import com.phishguard.backend_springboot.dto.ScanRequest;
import com.phishguard.backend_springboot.dto.ScanResponse;
import com.phishguard.backend_springboot.model.ScanLog;
import com.phishguard.backend_springboot.repository.ScanLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ScanController {

    private final ScanLogRepository scanLogRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String ML_SERVICE_URL = "http://localhost:8000/predict";

    @GetMapping("/health")
    public ResponseEntity<Object> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "PhishGuard Backend"));
    }

    @PostMapping("/scan")
    public ResponseEntity<ScanResponse> scanUrl(@RequestBody ScanRequest request) {
        String targetUrl = request.getUrl();

        // 1. Check MongoDB Atlas Cache first
        Optional<ScanLog> existingLog = scanLogRepository.findFirstByUrlOrderByScannedAtDesc(targetUrl);
        if (existingLog.isPresent()) {
            ScanLog log = existingLog.get();
            return ResponseEntity.ok(ScanResponse.builder()
                    .url(log.getUrl())
                    .isPhishing(log.isPhishing())
                    .riskScore(log.getRiskScore())
                    .reasons(log.getReasons())
                    .cached(true)
                    .build());
        }

        // 2. Call Python FastAPI ML Engine on port 8000
        ScanResponse mlResponse = restTemplate.postForObject(
                ML_SERVICE_URL,
                Map.of("url", targetUrl),
                ScanResponse.class
        );

        if (mlResponse == null) {
            return ResponseEntity.internalServerError().build();
        }

        // 3. Persist result to MongoDB Atlas
        ScanLog newLog = ScanLog.builder()
                .url(targetUrl)
                .isPhishing(mlResponse.isPhishing())
                .riskScore(mlResponse.getRiskScore())
                .reasons(mlResponse.getReasons())
                .scannedAt(Instant.now())
                .build();
        scanLogRepository.save(newLog);

        mlResponse.setCached(false);
        return ResponseEntity.ok(mlResponse);
    }

    @GetMapping("/history")
    public ResponseEntity<List<ScanLog>> getHistory() {
        return ResponseEntity.ok(scanLogRepository.findAll());
    }
}