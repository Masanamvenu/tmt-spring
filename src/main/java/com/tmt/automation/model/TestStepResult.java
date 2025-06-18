package com.tmt.automation.model;

import java.util.List;

import lombok.Data;

@Data
public class TestStepResult {
    private String testSteps;
    private String expectedResult;
    private String actualResult;
    private String locatorType;
    private String locatorValue;
    private List<String> browserActions;
    private String testdata;
    private String stepStatus;

    // Getters and setters
    // (Generate using your IDE or Lombok)
}