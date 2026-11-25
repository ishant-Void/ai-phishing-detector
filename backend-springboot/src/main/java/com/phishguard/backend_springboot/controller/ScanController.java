package com.phishguard.backend_springboot.controller;

import com.phishguard.backend_springboot.dto.ScanRequest;
import com.phishguard.backend_springboot.dto.ScanResponse;
import com.phishguard.backend_springboot.model.ScanLog;
import com.phishguard.backend_springboot.repository.ScanLogRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ScanController {

    private final ScanLogRepository scanLogRepository;

    @GetMapping("/health")
    public Object health() {
        return Map.of("status", "UP", "service", "ai-phishing-backend");
    }

    @PostMapping("/scan")
    public ScanResponse scanUrl(@Valid @RequestBody ScanRequest request) {
        boolean isSuspicious = request.isHasHiddenIframe() || request.isSuspiciousFormAction();
        int score = isSuspicious ? 82 : 12;
        String verdict = isSuspicious ? "PHISHING" : "SAFE";
        var reasons = isSuspicious
                ? List.of("Suspicious DOM or form action detected")
                : List.of("No immediate threat indicators found");

        ScanLog log = ScanLog.builder()
                .url(request.getUrl())
                .phishing(isSuspicious)
                .riskScore(score)
                .verdict(verdict)
                .reasons(reasons)
                .scannedAt(Instant.now())
                .build();
        scanLogRepository.save(log);

        return ScanResponse.builder()
                .url(request.getUrl())
                .phishing(isSuspicious)
                .riskScore(score)
                .verdict(verdict)
                .reasons(reasons)
                .cached(false)
                .build();
    }
}