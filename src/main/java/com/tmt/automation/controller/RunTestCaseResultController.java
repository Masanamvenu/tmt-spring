package com.tmt.automation.controller;

import com.tmt.automation.model.RunTestCaseResult;
import com.tmt.automation.repository.RunTestCaseResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/runtestcase/results")
public class RunTestCaseResultController {

    @Autowired
    private RunTestCaseResultRepository runTestCaseResultRepository;

    /**
     * Get run test case results filtered by projectId, releaseId, runId, testCaseId (all optional).
     * If any parameter is null, it will not be used as a filter.
     */
    @GetMapping
    public List<RunTestCaseResult> getRunTestCaseResults(
            @RequestParam(required = false) String projectId,
            @RequestParam(required = false) String releaseId,
            @RequestParam(required = false) String runId,
            @RequestParam(required = false) String testCaseId
    ) {
        // Simple filtering. For advanced querying, consider using QueryDSL or Criteria.
        List<RunTestCaseResult> allResults = runTestCaseResultRepository.findAll();
        return allResults.stream()
                .filter(r -> projectId == null || projectId.equals(r.getProjectId()))
                .filter(r -> releaseId == null || releaseId.equals(r.getReleaseId()))
                .filter(r -> runId == null || runId.equals(r.getRunId()))
                .filter(r -> testCaseId == null || testCaseId.equals(r.getTestCaseId()))
                .toList();
    }
}