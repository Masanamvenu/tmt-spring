package com.tmt.automation.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

@Data
@Document(collection = "runs")
@CompoundIndex(
    name = "unique_projectid_releaseid_runname",
    def = "{'projectID': 1, 'releaseID': 1, 'runName': 1}",
    unique = true
)
public class Run {
    @Id
    private String id;

    @Indexed(unique = true)
    private String runID;

    @NotBlank(message = "Run name is required")
    private String runName;

    @NotBlank(message = "ReleaseID is required")
    @Indexed
    private String releaseID;

    @NotBlank(message = "ProjectID is required")
    @Indexed
    private String projectID;

    private Instant createdAt;
    private Instant updatedAt;
}