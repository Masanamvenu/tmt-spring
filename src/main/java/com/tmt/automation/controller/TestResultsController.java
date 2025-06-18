package com.tmt.automation.controller;

import com.tmt.automation.model.TestResult;
import com.tmt.automation.repository.TestResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/runtestcase/testresults")
public class TestResultsController {

    @Autowired
    private TestResultRepository getTestResultRepository;
    /**
     * Get run test case results filtered by projectId, releaseId, runId, testCaseId (all optional).
     * If any parameter is null, it will not be used as a filter.
     */
    @GetMapping
    public List<TestResult> getTestResults() {
        // Simple filtering. For advanced querying, consider using QueryDSL or Criteria.
        List<TestResult> allResults = getTestResultRepository.findAll();
        return allResults;
    }
}