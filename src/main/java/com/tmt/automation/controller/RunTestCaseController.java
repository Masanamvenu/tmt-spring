package com.tmt.automation.controller;

import com.tmt.automation.model.RunTestCase;
import com.tmt.automation.model.TestResult;
import com.tmt.automation.model.TestCase;
import com.tmt.automation.repository.TestResultRepository;
import com.tmt.automation.repository.TestCaseRepository;
import com.tmt.automation.service.TestExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/runtestcase")
public class RunTestCaseController {

    @Autowired
    private TestExecutorService testExecutorService;

    @Autowired
    private TestResultRepository testResultRepository;

    @Autowired
    private TestCaseRepository testCaseRepository;

    @PostMapping
    public ResponseEntity<?> runTestCases(@RequestBody RunTestCase request) {
        if (request.getTestCaseIds() == null || request.getTestCaseIds().isEmpty()) {
            return ResponseEntity.badRequest().body("No testCaseIds provided.");
        }

        List<TestResult> resultList = new ArrayList<>();

        for (String testCaseId : request.getTestCaseIds()) {
            // Fetch TestCase from DB
            TestCase testCase = testCaseRepository.findByTestCaseID(testCaseId).orElse(null);
            if (testCase == null) {
                continue; // or handle as needed
            }
            String projectId = testCase.getProjectID();
            String releaseId = testCase.getReleaseID();
            String runId = testCase.getRunID();

            TestResult testResult = testExecutorService.executeTestCaseWithSteps(testCaseId, projectId, releaseId, runId);
            if (testResult != null) {
                testResult.setLastUpdated(Instant.now());
                testResultRepository.save(testResult);
                resultList.add(testResult);
            }
        }
        return ResponseEntity.ok(resultList);
    }
}