package com.tmt.automation.repository;

// import com.tmt.automation.model.TestCase;
// import org.springframework.data.mongodb.repository.MongoRepository;

// import java.util.Optional;

// public interface TestCaseRepository extends MongoRepository<TestCase, String> {
//    // Optional<TestCase> findByRunIDAndTestCaseName(String runID, String testCaseName);
//     Optional<TestCase> findByTestCaseID(String testCaseID);
// }
// package com.tmt.automation.repository;

import com.tmt.automation.model.TestCase;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface TestCaseRepository extends MongoRepository<TestCase, String> {
    Optional<TestCase> findByProjectIDAndReleaseIDAndRunIDAndTestCaseName(String projectID, String releaseID, String runID, String testCaseName);
    Optional<TestCase> findByTestCaseID(String testCaseID);
    //Optional<TestCase> findByRunIDAndTestCaseName(String runID, String testCaseName);
    
}