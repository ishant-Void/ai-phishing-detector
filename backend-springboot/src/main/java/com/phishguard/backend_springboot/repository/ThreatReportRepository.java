package com.phishguard.backend_springboot.repository;

import com.phishguard.backend_springboot.model.ThreatReport;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ThreatReportRepository extends MongoRepository<ThreatReport, String> {
}