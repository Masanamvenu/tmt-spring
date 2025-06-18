package com.tmt.automation.repository;

import com.tmt.automation.model.TestResult;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TestResultRepository extends MongoRepository<TestResult, String> {
}