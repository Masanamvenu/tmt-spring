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
@CrossOrigin(origins = "http://localhost:8082", allowCredentials = "true")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    @GetMapping
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<Project> getProjectById(@PathVariable String projectId) {
        return projectRepository.findByProjectID(projectId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createProject(@Valid @RequestBody Project project) {
        if (projectRepository.findByProjectName(project.getProjectName()).isPresent()) {
            return ResponseEntity.badRequest().body("Project name already exists");
        }
        long seq = sequenceGeneratorService.generateSequence("project_sequence");
        project.setProjectID(String.format("P-%05d", seq));
        project.setCreatedAt(Instant.now());
        project.setUpdatedAt(Instant.now());
        return ResponseEntity.ok(projectRepository.save(project));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<?> updateProject(@PathVariable String projectId, @Valid @RequestBody Project updatedProject) {
        Optional<Project> optionalProject = projectRepository.findByProjectID(projectId);
        if (optionalProject.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Optional<Project> projectWithName = projectRepository.findByProjectName(updatedProject.getProjectName());
        if (projectWithName.isPresent() && !projectWithName.get().getProjectID().equals(projectId)) {
            return ResponseEntity.badRequest().body("Project name already exists");
        }
        Project project = optionalProject.get();
        project.setProjectName(updatedProject.getProjectName());
        project.setUpdatedAt(Instant.now());
        return ResponseEntity.ok(projectRepository.save(project));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<String> deleteProject(@PathVariable String projectId) {
        Optional<Project> optionalProject = projectRepository.findByProjectID(projectId);
        if (optionalProject.isEmpty()) {
            return ResponseEntity.status(404).body("Project Not Found");
        }
        projectRepository.delete(optionalProject.get());
        return ResponseEntity.status(202).body("Project deleted successfully");
    }
}