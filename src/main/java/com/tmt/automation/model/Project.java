package com.tmt.automation.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

@Data
@Document(collection = "projects")
public class Project {
    @Id
    private String id;

    @Indexed(unique = true)
    private String projectID;

    @NotBlank(message = "Project name is required")
    @Indexed(unique = true)
    private String projectName;

    private Instant createdAt;
    private Instant updatedAt;
}