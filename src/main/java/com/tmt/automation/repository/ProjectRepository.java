package com.tmt.automation.repository;

import com.tmt.automation.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProjectRepository extends MongoRepository<Project, String> {
    Optional<Project> findByProjectName(String projectName);
}