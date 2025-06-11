package com.tmt.automation.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;

@Data
@Document(collection = "testcases")
@CompoundIndex(
    name = "unique_runid_testcasename",
    def = "{'runID': 1, 'testCaseName': 1}",
    unique = true
)
public class TestCase {
    @Id
    private String id;

    @Indexed(unique = true)
    private String testCaseID;

    @NotBlank(message = "Test case name is required")
    private String testCaseName;

    @NotBlank(message = "RunID is required")
    @Indexed
    private String runID;

    @NotBlank(message = "ReleaseID is required")
    @Indexed
    private String releaseID;

    @NotBlank(message = "ProjectID is required")
    @Indexed
    private String projectID;

    private List<TestStep> testSteps;
    

    private Instant createdAt;
    private Instant updatedAt;
}