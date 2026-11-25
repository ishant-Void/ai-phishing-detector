package com.phishguard.backend_springboot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ScanRequest {
    @NotBlank(message = "URL cannot be empty")
    private String url;
    private boolean hasHiddenIframe;
    private boolean suspiciousFormAction;
    private double externalLinkRatio;
}