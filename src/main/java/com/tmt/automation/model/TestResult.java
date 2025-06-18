package com.tmt.automation.model;

import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.time.Instant;

@Document(collection = "TestResults")
@Data
public class TestResult {
    @JsonProperty("releaseID")
    private String releaseID;

    @JsonProperty("projectID")
    private String projectID;

    @JsonProperty("runID")
    private String runID;

    private String testCaseName;
    private String testStatus;
    private List<TestStepResult> testSteps;
    private Instant lastUpdated;
}