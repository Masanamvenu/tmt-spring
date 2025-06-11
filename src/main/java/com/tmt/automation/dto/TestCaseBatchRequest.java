package com.tmt.automation.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class TestCaseBatchRequest {
    @NotBlank
    private String testCaseName;
    @NotBlank
    private String releaseID;
    @NotBlank
    private String runID;
    @NotBlank
    private String projectID;
    private List<TestStepRequest> testSteps;

    public static class TestStepRequest {
        private String testSteps;
        private String expectedResult;
        private String actualResult;
        private String locatorType;
        private String locatorValue;
        private List<String> browserActions; // CHANGED from String to List<String>
        private String testdata;

        public String getTestSteps() { return testSteps; }
        public void setTestSteps(String testSteps) { this.testSteps = testSteps; }
        public String getExpectedResult() { return expectedResult; }
        public void setExpectedResult(String expectedResult) { this.expectedResult = expectedResult; }
        public String getActualResult() { return actualResult; }
        public void setActualResult(String actualResult) { this.actualResult = actualResult; }
        public String getLocatorType() { return locatorType; }
        public void setLocatorType(String locatorType) { this.locatorType = locatorType; }
        public String getLocatorValue() { return locatorValue; }
        public void setLocatorValue(String locatorValue) { this.locatorValue = locatorValue; }
        public List<String> getBrowserActions() { return browserActions; }
        public void setBrowserActions(List<String> browserActions) { this.browserActions = browserActions; }
        public String getTestdata() { return testdata; }
        public void setTestdata(String testdata) { this.testdata = testdata; }
    }

    public String getTestCaseName() { return testCaseName; }
    public void setTestCaseName(String testCaseName) { this.testCaseName = testCaseName; }
    public String getReleaseID() { return releaseID; }
    public void setReleaseID(String releaseID) { this.releaseID = releaseID; }
    public String getRunID() { return runID; }
    public void setRunID(String runID) { this.runID = runID; }
    public String getProjectID() { return projectID; }
    public void setProjectID(String projectID) { this.projectID = projectID; }
    public List<TestStepRequest> getTestSteps() { return testSteps; }
    public void setTestSteps(List<TestStepRequest> testSteps) { this.testSteps = testSteps; }
}