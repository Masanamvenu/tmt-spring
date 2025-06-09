package com.tmt.automation.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

@Data
@Document(collection = "releases")
@CompoundIndex(name = "unique_projectid_releasename", def = "{'projectID': 1, 'releaseName': 1}", unique = true)
public class Release {
    @Id
    private String id;

    @Indexed(unique = true)
    private String releaseID; // auto-generated like R-00001

    @NotBlank(message = "Release name is required")
    private String releaseName;

    @NotBlank(message = "ProjectID is required")
    @Indexed
    private String projectID;

    private Instant createdAt;
    private Instant updatedAt;
}