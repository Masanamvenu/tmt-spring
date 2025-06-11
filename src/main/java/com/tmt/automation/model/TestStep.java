package com.tmt.automation.model;
import lombok.Data;
import java.util.List;
@Data
public class TestStep {
    private String testSteps;
    private String expectedResult;
    private String actualResult;
    private String locatorType;
    private String locatorValue;
    private List<BrowserAction> browserActions; // List of enums!
    private String testdata;

    // Getters and setters (or use Lombok's @Data)
}