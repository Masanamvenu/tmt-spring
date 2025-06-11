package com.tmt.automation.controller;

import com.tmt.automation.model.Run;
import com.tmt.automation.repository.RunRepository;
import com.tmt.automation.repository.ProjectRepository;
import com.tmt.automation.repository.ReleaseRepository;
import com.tmt.automation.service.SequenceGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/runs")
@Validated
public class RunController {

    @Autowired
    private RunRepository runRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ReleaseRepository releaseRepository;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    @GetMapping
    public List<Run> getAllRuns() {
        return runRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Run> getRunById(@PathVariable String id) {
        return runRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createRun(@Valid @RequestBody Run run) {
        // Check if ProjectID exists
        boolean projectExists = projectRepository.findAll().stream().anyMatch(p -> p.getProjectID().equals(run.getProjectID()));
        if (!projectExists) {
            return ResponseEntity.badRequest().body("Cannot add Run: ProjectID does not exist.");
        }
        // Check if ReleaseID exists
        boolean releaseExists = releaseRepository.findAll().stream().anyMatch(r -> r.getReleaseID().equals(run.getReleaseID()));
        if (!releaseExists) {
            return ResponseEntity.badRequest().body("Cannot add Run: ReleaseID does not exist.");
        }
        // Check for duplicate RunName within the same ProjectID and ReleaseID
        if (runRepository.findByProjectIDAndReleaseIDAndRunName(run.getProjectID(), run.getReleaseID(), run.getRunName()).isPresent()) {
            return ResponseEntity.badRequest().body("Run name already exists for this release in the project");
        }
        long seq = sequenceGeneratorService.generateSequence("run_sequence");
        run.setRunID(String.format("N-%05d", seq)); // auto-generate like N-00001
        run.setCreatedAt(Instant.now());
        run.setUpdatedAt(Instant.now());
        return ResponseEntity.ok(runRepository.save(run));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRun(@PathVariable String id, @Valid @RequestBody Run updatedRun) {
        Optional<Run> optionalRun = runRepository.findById(id);
        if (optionalRun.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        // Check if ProjectID exists
        boolean projectExists = projectRepository.findAll().stream().anyMatch(p -> p.getProjectID().equals(updatedRun.getProjectID()));
        if (!projectExists) {
            return ResponseEntity.badRequest().body("Cannot update Run: ProjectID does not exist.");
        }
        // Check if ReleaseID exists
        boolean releaseExists = releaseRepository.findAll().stream().anyMatch(r -> r.getReleaseID().equals(updatedRun.getReleaseID()));
        if (!releaseExists) {
            return ResponseEntity.badRequest().body("Cannot update Run: ReleaseID does not exist.");
        }
        // Check if another run with the same name exists in the same project and release
        Optional<Run> runWithName = runRepository.findByProjectIDAndReleaseIDAndRunName(updatedRun.getProjectID(), updatedRun.getReleaseID(), updatedRun.getRunName());
        if (runWithName.isPresent() && !runWithName.get().getId().equals(id)) {
            return ResponseEntity.badRequest().body("Run name already exists for this release in the project");
        }
        Run run = optionalRun.get();
        run.setRunName(updatedRun.getRunName());
        run.setProjectID(updatedRun.getProjectID());
        run.setReleaseID(updatedRun.getReleaseID());
        run.setUpdatedAt(Instant.now());
        return ResponseEntity.ok(runRepository.save(run));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRun(@PathVariable String id) {
        if (!runRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        runRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}