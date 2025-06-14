package com.tmt.automation.service;

import com.tmt.automation.model.TestCase;
import com.tmt.automation.model.TestStep;
import com.tmt.automation.model.BrowserAction;
import com.tmt.automation.repository.TestCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tmt.automation.base.Base;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Properties;
import java.util.List;

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

        try {
            for (TestStep step : testCase.getTestSteps()) {
                String locatorType = step.getLocatorType();
                String locatorValue = step.getLocatorValue();
                String testData = step.getTestdata();
                System.out.println(locatorType + " = " + locatorValue + ", Test Data: " + testData);

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
                            if (driver != null) {
                                driver.close();
                                System.out.println("Closing browser...");
                            }
                            break;
                        case ENTER_URL:
                            if (driver != null) {
                                System.out.println("Entering URL...");
                                driver.get(testData != null && !testData.isEmpty() ? testData : "https://www.google.com/");
                                System.out.println("Entering URL: " + (testData != null ? testData : "https://www.google.com/"));
                            }
                            break;
                        case CLICK:
                            if (driver != null && locatorType != null && locatorValue != null) {
                                WebElement element = findElement(driver, locatorType, locatorValue);
                                if (element != null) {
                                    element.click();
                                    System.out.println("Clicked element: " + locatorValue);
                                } else {
                                    System.out.println("Element not found for clicking: " + locatorType + " = " + locatorValue);
                                    return false;
                                }
                            }
                            break;
                        case ENTER:
                            if (driver != null && locatorType != null && locatorValue != null && testData != null) {
                                WebElement element = findElement(driver, locatorType, locatorValue);
                                if (element != null) {
                                    element.clear();
                                    element.sendKeys(testData);
                                    System.out.println("Entered data: " + testData + " in element: " + locatorValue);
                                } else {
                                    System.out.println("Element not found for entering data: " + locatorType + " = " + locatorValue);
                                    return false;
                                }
                            }
                            break;
                        case WAIT:
                            System.out.println("Waiting...");
                            try {
                                Thread.sleep(1000); // Wait 1 second for demonstration, adjust as needed
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            break;
                        default:
                            System.out.println("Unknown action: " + action);
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Error during test case execution: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Finds a WebElement based on locator type and value.
     */
    private WebElement findElement(WebDriver driver, String locatorType, String locatorValue) {
        try {
            switch (locatorType.toLowerCase()) {
                case "id":
                    return driver.findElement(By.id(locatorValue));
                case "name":
                    return driver.findElement(By.name(locatorValue));
                case "xpath":
                    return driver.findElement(By.xpath(locatorValue));
                case "css":
                case "cssselector":
                    return driver.findElement(By.cssSelector(locatorValue));
                case "class":
                case "classname":
                    return driver.findElement(By.className(locatorValue));
                case "tag":
                case "tagname":
                    return driver.findElement(By.tagName(locatorValue));
                case "linktext":
                    return driver.findElement(By.linkText(locatorValue));
                case "partiallinktext":
                    return driver.findElement(By.partialLinkText(locatorValue));
                default:
                    System.out.println("Unsupported locator type: " + locatorType);
                    return null;
            }
        } catch (Exception e) {
            System.out.println("Exception finding element: " + e.getMessage());
            return null;
        }
    }
}