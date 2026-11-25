package com.phishguard.backend_springboot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScanResponse {
    private String url;
    private boolean phishing;
    private int riskScore;
    private String verdict;
    private List<String> reasons;
    private boolean cached;
}