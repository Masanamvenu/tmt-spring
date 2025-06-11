package com.tmt.automation.model;

import lombok.Data;

@Data
public class RunTestCase {
    private String testCaseId;
    private String runId; // optional, for run tracking if you need
}