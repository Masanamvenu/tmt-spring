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
    public ResponseEntity<String> runTestCase(@RequestBody RunTestCase request) {
        boolean result = testExecutorService.executeTestCase(request.getTestCaseId());
        if (result) {
            return ResponseEntity.ok("Test case executed successfully.");
        } else {
            return ResponseEntity.badRequest().body("Test case execution failed.");
        }
    }
}