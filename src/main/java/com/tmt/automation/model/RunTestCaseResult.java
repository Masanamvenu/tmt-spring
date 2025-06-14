package com.tmt.automation.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "run_test_case_results")
public class RunTestCaseResult {
    @Id
    private String id;
    private String projectId;
    private String releaseId;
    private String runId;
    private String testCaseId;
    private String testStatus;
    private String executedAt; // <-- CHANGE TO String
}