package com.tmt.automation.repository;

import com.tmt.automation.model.Release;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ReleaseRepository extends MongoRepository<Release, String> {
    Optional<Release> findByProjectIDAndReleaseName(String projectID, String releaseName);
}