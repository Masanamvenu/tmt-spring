package com.tmt.automation.controller;

import com.tmt.automation.model.Project;
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
@RequestMapping("/api/projects")
@Validated
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    @GetMapping
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable String id) {
        return projectRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createProject(@Valid @RequestBody Project project) {
        // Check for duplicate projectName
        if (projectRepository.findByProjectName(project.getProjectName()).isPresent()) {
            return ResponseEntity.badRequest().body("Project name already exists");
        }
        long seq = sequenceGeneratorService.generateSequence("project_sequence");
        project.setProjectID(String.format("P-%05d", seq));
        project.setCreatedAt(Instant.now());
        project.setUpdatedAt(Instant.now());
        return ResponseEntity.ok(projectRepository.save(project));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@PathVariable String id, @Valid @RequestBody Project updatedProject) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        if (optionalProject.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        // Check if another project with the same name exists
        Optional<Project> projectWithName = projectRepository.findByProjectName(updatedProject.getProjectName());
        if (projectWithName.isPresent() && !projectWithName.get().getId().equals(id)) {
            return ResponseEntity.badRequest().body("Project name already exists");
        }
        Project project = optionalProject.get();
        project.setProjectName(updatedProject.getProjectName());
        project.setUpdatedAt(Instant.now());
        return ResponseEntity.ok(projectRepository.save(project));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable String id) {
        if (!projectRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        projectRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}