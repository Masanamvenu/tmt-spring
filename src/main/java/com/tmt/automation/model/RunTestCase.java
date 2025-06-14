package com.tmt.automation.model;

import java.util.List;

import lombok.Data;

@Data
public class RunTestCase {
    //private String testCaseId;
    private List<String> testCaseIds;
    private String runId; // optional, for run tracking if you need
}