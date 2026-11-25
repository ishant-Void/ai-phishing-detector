package com.phishguard.backend_springboot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("isPhishing")
    private boolean isPhishing;

    private double riskScore;
    private List<String> reasons;
    private Boolean cached;
}