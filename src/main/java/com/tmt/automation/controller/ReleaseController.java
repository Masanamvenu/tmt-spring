package com.tmt.automation.controller;

import com.tmt.automation.model.Release;
import com.tmt.automation.repository.ReleaseRepository;
import com.tmt.automation.repository.ProjectRepository;
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
@RequestMapping("/api/releases")
@Validated
public class ReleaseController {

    @Autowired
    private ReleaseRepository releaseRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    @GetMapping
    public List<Release> getAllReleases() {
        return releaseRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Release> getReleaseById(@PathVariable String id) {
        return releaseRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createRelease(@Valid @RequestBody Release release) {
        // Check if ProjectID exists
        if (!projectRepository.findAll().stream().anyMatch(p -> p.getProjectID().equals(release.getProjectID()))) {
            return ResponseEntity.badRequest().body("Cannot add Release: ProjectID does not exist.");
        }
        // Check for duplicate ReleaseName within the same ProjectID
        if (releaseRepository.findByProjectIDAndReleaseName(release.getProjectID(), release.getReleaseName()).isPresent()) {
            return ResponseEntity.badRequest().body("Release name already exists for this project");
        }
        long seq = sequenceGeneratorService.generateSequence("release_sequence");
        release.setReleaseID(String.format("R-%05d", seq)); // auto-generate like R-00001
        release.setCreatedAt(Instant.now());
        release.setUpdatedAt(Instant.now());
        return ResponseEntity.ok(releaseRepository.save(release));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRelease(@PathVariable String id, @Valid @RequestBody Release updatedRelease) {
        Optional<Release> optionalRelease = releaseRepository.findById(id);
        if (optionalRelease.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        // Check if ProjectID exists
        if (!projectRepository.findAll().stream().anyMatch(p -> p.getProjectID().equals(updatedRelease.getProjectID()))) {
            return ResponseEntity.badRequest().body("Cannot update Release: ProjectID does not exist.");
        }
        // Check if another release with the same name exists in the same project
        Optional<Release> releaseWithName = releaseRepository.findByProjectIDAndReleaseName(updatedRelease.getProjectID(), updatedRelease.getReleaseName());
        if (releaseWithName.isPresent() && !releaseWithName.get().getId().equals(id)) {
            return ResponseEntity.badRequest().body("Release name already exists for this project");
        }
        Release release = optionalRelease.get();
        release.setReleaseName(updatedRelease.getReleaseName());
        release.setProjectID(updatedRelease.getProjectID());
        release.setUpdatedAt(Instant.now());
        return ResponseEntity.ok(releaseRepository.save(release));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRelease(@PathVariable String id) {
        if (!releaseRepository.existsById(id)) {
            //return ResponseEntity.notFound().build();
             return ResponseEntity.status(404).body("Release Not Found");
        }
        releaseRepository.deleteById(id);
        //return ResponseEntity.noContent().build();
        return ResponseEntity.status(202).body("Release deleted successfully");
    }
}