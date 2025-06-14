package com.tmt.automation.repository;

import com.tmt.automation.model.RunTestCaseResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RunTestCaseResultRepository extends MongoRepository<RunTestCaseResult, String> {
    // Optional: Add custom query methods if needed
}