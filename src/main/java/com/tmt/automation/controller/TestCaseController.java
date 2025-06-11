package com.tmt.automation.controller;

import com.tmt.automation.model.TestCase;
import com.tmt.automation.model.TestStep;
import com.tmt.automation.model.BrowserAction;
import com.tmt.automation.repository.TestCaseRepository;
import com.tmt.automation.repository.ProjectRepository;
import com.tmt.automation.repository.ReleaseRepository;
import com.tmt.automation.repository.RunRepository;
import com.tmt.automation.service.SequenceGeneratorService;
import com.tmt.automation.dto.TestCaseBatchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/testcases")
@Validated
public class TestCaseController {

    @Autowired
    private TestCaseRepository testCaseRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ReleaseRepository releaseRepository;

    @Autowired
    private RunRepository runRepository;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    @PostMapping("/batch")
    public ResponseEntity<?> createTestCase(@Valid @RequestBody TestCaseBatchRequest batchRequest) {
        // Validation
        boolean projectExists = projectRepository.findAll().stream()
            .anyMatch(p -> p.getProjectID().equals(batchRequest.getProjectID()));
        if (!projectExists) {
            return ResponseEntity.badRequest().body("Cannot add TestCase: ProjectID does not exist.");
        }
        boolean releaseExists = releaseRepository.findAll().stream()
            .anyMatch(r -> r.getReleaseID().equals(batchRequest.getReleaseID()));
        if (!releaseExists) {
            return ResponseEntity.badRequest().body("Cannot add TestCase: ReleaseID does not exist.");
        }
        boolean runExists = runRepository.findAll().stream()
            .anyMatch(r -> r.getRunID().equals(batchRequest.getRunID()));
        if (!runExists) {
            return ResponseEntity.badRequest().body("Cannot add TestCase: RunID does not exist.");
        }
        /*if (testCaseRepository.findByProjectIDAndReleaseIDAndRunIDAndTestCaseName(batchRequest.getProjectID(), batchRequest.getReleaseID(), batchRequest.getRunID(), batchRequest.getTestCaseName()).isPresent()) {
            return ResponseEntity.badRequest().body("Test case name already exists for this run");
        }*/
        if (testCaseRepository.findByProjectIDAndReleaseIDAndRunIDAndTestCaseName(
        batchRequest.getProjectID(),
        batchRequest.getReleaseID(),
        batchRequest.getRunID(),
        batchRequest.getTestCaseName()
    ).isPresent()) {
            return ResponseEntity.badRequest().body("Test case name already exists for this run");
        }
        // Map test steps
        List<TestStep> steps = new ArrayList<>();
        for (TestCaseBatchRequest.TestStepRequest stepReq : batchRequest.getTestSteps()) {
            TestStep step = new TestStep();
            step.setTestSteps(stepReq.getTestSteps());
            step.setExpectedResult(stepReq.getExpectedResult());
            step.setActualResult(stepReq.getActualResult());
            step.setLocatorType(stepReq.getLocatorType());
            step.setLocatorValue(stepReq.getLocatorValue());
            // Fix: Convert List<String> to List<BrowserAction>
            if (stepReq.getBrowserActions() != null) {
                List<BrowserAction> browserActions = stepReq.getBrowserActions().stream()
                    .map(BrowserAction::valueOf)
                    .collect(Collectors.toList());
                step.setBrowserActions(browserActions);
            } else {
                step.setBrowserActions(null);
            }
            step.setTestdata(stepReq.getTestdata());
            steps.add(step);
        }
        // Create TestCase
        TestCase tc = new TestCase();
        long seq = sequenceGeneratorService.generateSequence("testcase_sequence");
        tc.setTestCaseID(String.format("T-%05d", seq));
        tc.setTestCaseName(batchRequest.getTestCaseName());
        tc.setProjectID(batchRequest.getProjectID());
        tc.setReleaseID(batchRequest.getReleaseID());
        tc.setRunID(batchRequest.getRunID());
        tc.setTestSteps(steps);
        Instant now = Instant.now();
        tc.setCreatedAt(now);
        tc.setUpdatedAt(now);
        TestCase saved = testCaseRepository.save(tc);

        return ResponseEntity.ok(List.of(saved));
    }
    // GET: Get all test cases
    @GetMapping
    public ResponseEntity<List<TestCase>> getAllTestCases() {
        List<TestCase> testCases = testCaseRepository.findAll();
        return ResponseEntity.ok(testCases);
    }

    // GET: Get test case by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getTestCaseById(@PathVariable("id") String id) {
        Optional<TestCase> testCase = testCaseRepository.findById(id);
        return testCase.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // PUT: Update test case by ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTestCase(
            @PathVariable("id") String id,
            @Valid @RequestBody TestCaseBatchRequest batchRequest
    ) {
        Optional<TestCase> existingOpt = testCaseRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Validate existence of related entities as in POST
        boolean projectExists = projectRepository.findAll().stream()
            .anyMatch(p -> p.getProjectID().equals(batchRequest.getProjectID()));
        if (!projectExists) {
            return ResponseEntity.badRequest().body("Cannot update TestCase: ProjectID does not exist.");
        }
        boolean releaseExists = releaseRepository.findAll().stream()
            .anyMatch(r -> r.getReleaseID().equals(batchRequest.getReleaseID()));
        if (!releaseExists) {
            return ResponseEntity.badRequest().body("Cannot update TestCase: ReleaseID does not exist.");
        }
        boolean runExists = runRepository.findAll().stream()
            .anyMatch(r -> r.getRunID().equals(batchRequest.getRunID()));
        if (!runExists) {
            return ResponseEntity.badRequest().body("Cannot update TestCase: RunID does not exist.");
        }
        // Check for test case name uniqueness (exclude current)
        //Optional<TestCase> duplicate = testCaseRepository.findByProjectIDAndReleaseIDAndRunIDAndTestCaseName(batchRequest.getRunID(), batchRequest.getTestCaseName());
        Optional<TestCase> duplicate = testCaseRepository.findByProjectIDAndReleaseIDAndRunIDAndTestCaseName(
            batchRequest.getProjectID(),
            batchRequest.getReleaseID(),
            batchRequest.getRunID(),
            batchRequest.getTestCaseName()
        );
        if (duplicate.isPresent() && !duplicate.get().getTestCaseID().equals(id)) {
            return ResponseEntity.badRequest().body("Test case name already exists for this run");
        }

        TestCase existing = existingOpt.get();
        existing.setTestCaseName(batchRequest.getTestCaseName());
        existing.setProjectID(batchRequest.getProjectID());
        existing.setReleaseID(batchRequest.getReleaseID());
        existing.setRunID(batchRequest.getRunID());

        // Map test steps
        List<TestStep> steps = new ArrayList<>();
        for (TestCaseBatchRequest.TestStepRequest stepReq : batchRequest.getTestSteps()) {
            TestStep step = new TestStep();
            step.setTestSteps(stepReq.getTestSteps());
            step.setExpectedResult(stepReq.getExpectedResult());
            step.setActualResult(stepReq.getActualResult());
            step.setLocatorType(stepReq.getLocatorType());
            step.setLocatorValue(stepReq.getLocatorValue());
            if (stepReq.getBrowserActions() != null) {
                List<BrowserAction> browserActions = stepReq.getBrowserActions().stream()
                    .map(BrowserAction::valueOf)
                    .collect(Collectors.toList());
                step.setBrowserActions(browserActions);
            } else {
                step.setBrowserActions(null);
            }
            step.setTestdata(stepReq.getTestdata());
            steps.add(step);
        }
        existing.setTestSteps(steps);
        existing.setUpdatedAt(Instant.now());

        TestCase updated = testCaseRepository.save(existing);
        return ResponseEntity.ok(updated);
    }

    // DELETE: Delete test case by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTestCase(@PathVariable("id") String id) {
        Optional<TestCase> testCase = testCaseRepository.findById(id);
        if (testCase.isEmpty()) {
            //return ResponseEntity.notFound().build();
            return ResponseEntity.status(404).body("TestCase not found");
        }
        testCaseRepository.deleteById(id);
        //return ResponseEntity.noContent().build();
        return ResponseEntity.status(202).body("TestCase deleted successfully");
    }
}