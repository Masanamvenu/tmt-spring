package com.tmt.automation.repository;

import com.tmt.automation.model.Run;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RunRepository extends MongoRepository<Run, String> {
    Optional<Run> findByProjectIDAndReleaseIDAndRunName(String projectID, String releaseID, String runName);
}