package com.tmt.automation.model;

import java.util.List;
import lombok.Data;

@Data
public class RunTestCase {
    private String projectId;
    private String releaseId;
    private String runId; // optional, for run tracking if you need
    private List<String> testCaseIds;
}