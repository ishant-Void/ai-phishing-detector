package com.phishguard.backend_springboot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "scan_logs")
public class ScanLog {
    @Id
    private String id;
    private String url;
    private boolean phishing;
    private int riskScore;
    private String verdict;
    private List<String> reasons;
    private Instant scannedAt;
}