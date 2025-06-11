package com.tmt.automation.service;

import com.tmt.automation.model.TestCase;
import com.tmt.automation.model.TestStep;
import com.tmt.automation.model.BrowserAction;
import com.tmt.automation.repository.TestCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tmt.automation.base.Base;
import org.openqa.selenium.WebDriver;
import java.util.Properties;
import java.util.List;  
import java.util.Optional;
import java.util.ArrayList;
import org.springframework.stereotype.Service;

@Service
public class TestExecutorService {
public Base base;
public Properties prop;
public WebDriver driver;
    @Autowired
    private TestCaseRepository testCaseRepository;

    public boolean executeTestCase(String testCaseId) {
        TestCase testCase = testCaseRepository.findByTestCaseID(testCaseId).orElse(null);
        if (testCase == null) return false;
        if (testCase.getTestSteps() == null) return false;
        for (TestStep step : testCase.getTestSteps()) {
            if (step.getBrowserActions() == null) continue;
            for (BrowserAction action : step.getBrowserActions()) {
                switch (action) {
                    case OPEN_BROWSER:
                        System.out.println("Opening browser...");
                        base = new Base();
						driver = base.init_driver("chrome");
                        if (driver == null) {
                            System.out.println("Failed to open browser.");
                            return false;
                        }
                        break;
                    case CLOSE_BROWSER:
                        driver.close();
                        System.out.println("Closing browser...");
                        break;
                    case ENTER_URL:
                        driver.get("https://www.google.com/");
                        System.out.println("Entering URL: " + step.getTestdata());
                        break;
                    case CLICK:
                        System.out.println("Clicking element: " + step.getLocatorValue());
                        break;
                    case ENTER:
                        System.out.println("Entering data: " + step.getTestdata());
                        break;
                    case WAIT:
                        System.out.println("Waiting...");
                        break;
                    default:
                        System.out.println("Unknown action: " + action);
                }
            }
        }
        return true;
    }
}