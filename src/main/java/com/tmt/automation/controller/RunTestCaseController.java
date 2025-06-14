package com.tmt.automation.controller;

import com.tmt.automation.model.RunTestCase;
import com.tmt.automation.service.TestExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/runtestcase")
public class RunTestCaseController {

    @Autowired
    private TestExecutorService testExecutorService;

    @PostMapping
    public ResponseEntity<?> runTestCases(@RequestBody RunTestCase request) {
        // Validate if testCaseIds is not null or empty
        if (request.getTestCaseIds() == null || request.getTestCaseIds().isEmpty()) {
            return ResponseEntity.badRequest().body("No testCaseIds provided.");
        }
        // Optionally collect results for each case
        boolean allSuccess = true;
        StringBuilder results = new StringBuilder();
        for (String testCaseId : request.getTestCaseIds()) {
            boolean result = testExecutorService.executeTestCase(testCaseId);
            results.append("TestCaseID ").append(testCaseId)
                    .append(result ? ": SUCCESS\n" : ": FAILED\n");
            if (!result) allSuccess = false;
        }
        if (allSuccess) {
            return ResponseEntity.ok(results.toString());
        } else {
            return ResponseEntity.badRequest().body(results.toString());
        }
    }
}