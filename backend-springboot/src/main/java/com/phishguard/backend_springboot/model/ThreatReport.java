package com.phishguard.backend_springboot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "threat_reports")
public class ThreatReport {
    @Id
    private String id;
    private String url;
    private String reportedBy;
    private String notes;
    private Instant reportedAt;
}