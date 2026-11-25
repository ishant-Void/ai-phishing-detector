package com.phishguard.backend_springboot.repository;

import com.phishguard.backend_springboot.model.ScanLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface ScanLogRepository extends MongoRepository<ScanLog, String> {
    Optional<ScanLog> findFirstByUrlOrderByScannedAtDesc(String url);
}