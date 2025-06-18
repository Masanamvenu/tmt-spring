package com.tmt.automation.service;

import com.tmt.automation.model.TestCase;
import com.tmt.automation.model.TestStep;
import com.tmt.automation.model.BrowserAction;
import com.tmt.automation.repository.TestCaseRepository;
import com.tmt.automation.keywordrepository.KeywordRepository;
import com.tmt.automation.model.TestResult;
import com.tmt.automation.model.TestStepResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TestExecutorService {

    public KeywordRepository keywordRepository;

    @Autowired
    private TestCaseRepository testCaseRepository;

    public TestResult executeTestCaseWithSteps(String testCaseId, String projectId, String releaseId, String runId) {
        keywordRepository = new KeywordRepository();
        TestCase testCase = testCaseRepository.findByTestCaseID(testCaseId).orElse(null);
        if (testCase == null) return null;

        List<TestStepResult> stepResults = new ArrayList<>();
        boolean overallPass = true;

        try {
            for (TestStep step : testCase.getTestSteps()) {
                TestStepResult stepResult = new TestStepResult();
                stepResult.setTestSteps(step.getTestSteps());
                stepResult.setExpectedResult(step.getExpectedResult());
                stepResult.setLocatorType(step.getLocatorType());
                stepResult.setLocatorValue(step.getLocatorValue());
                stepResult.setBrowserActions(step.getBrowserActions() != null ?
                        step.getBrowserActions().stream().map(Enum::name).toList() : null);
                stepResult.setTestdata(step.getTestdata());

                String actualResult = "";
                String stepStatus = "PASS";

                try {
                    if (step.getBrowserActions() != null) {
                        for (BrowserAction action : step.getBrowserActions()) {
                            switch (action) {
                                case OPEN_BROWSER:
                                    keywordRepository.openBrowser(step.getTestdata());
                                    actualResult = "Browser launched";
                                    break;
                                case CLOSE_BROWSER:
                                    keywordRepository.closeBrowser();
                                    actualResult = "Browser closed";
                                    break;
                                case ENTER_URL:
                                    keywordRepository.enterURL(step.getTestdata());
                                    actualResult = "URL entered";
                                    break;
                                case CLICK_WEB_ELEMENT:
                                    keywordRepository.clickWebElement(step.getLocatorType(), step.getLocatorValue());
                                    actualResult = "Clicked web element";
                                    break;
                                case ENTER_TEXT:
                                    keywordRepository.enterText(step.getLocatorType(), step.getLocatorValue(), step.getTestdata());
                                    actualResult = "Entered text";
                                    break;
                                case WAIT:
                                    keywordRepository.waitForElement(step.getTestdata() != null && !step.getTestdata().isEmpty() ? Integer.parseInt(step.getTestdata()) : 5);
                                    actualResult = "Waited";
                                    break;
                                case IMPLICITLYWAIT:
                                    keywordRepository.implicitlyWait(step.getTestdata() != null && !step.getTestdata().isEmpty() ? Integer.parseInt(step.getTestdata()) : 5);
                                    actualResult = "Implicit wait";
                                    break;
                                case EXPLICITWAIT:
                                    keywordRepository.explicitWait(step.getLocatorType(), step.getLocatorValue(), step.getTestdata() != null && !step.getTestdata().isEmpty() ? Integer.parseInt(step.getTestdata()) : 5);
                                    actualResult = "Explicit wait";
                                    break;
                                case ISDISPLAYED:
                                    keywordRepository.isElementDisplayed(step.getLocatorType(), step.getLocatorValue());
                                    actualResult = "Element displayed";
                                    break;
                                case NAVIGATION_TO:
                                    keywordRepository.navigateTo(step.getTestdata());
                                    actualResult = "Navigated to URL";
                                    break;
                                case NAVIGATE_BACK:
                                    keywordRepository.navigateBack();
                                    actualResult = "Navigated back";
                                    break;
                                case NAVIGATE_FORWARD:
                                    keywordRepository.navigateForward();
                                    actualResult = "Navigated forward";
                                    break;
                                case NAVIGATE_REFRESH:
                                    keywordRepository.navigateRefresh();
                                    actualResult = "Refreshed";
                                    break;
                                case RIGHT_CLICK:
                                    keywordRepository.rightClick(step.getLocatorType(), step.getLocatorValue());
                                    actualResult = "Right clicked";
                                    break;
                                case DOUBLE_CLICK:
                                    keywordRepository.doubleClick(step.getLocatorType(), step.getLocatorValue());
                                    actualResult = "Double clicked";
                                    break;
                                case SELECTBYVISIBILETEXT:
                                    keywordRepository.selectByVisibleText(step.getLocatorType(), step.getLocatorValue(), step.getTestdata());
                                    actualResult = "Selected by visible text";
                                    break;
                                case SELECTBYVALUE:
                                    keywordRepository.selectByValue(step.getLocatorType(), step.getLocatorValue(), step.getTestdata());
                                    actualResult = "Selected by value";
                                    break;
                                case SELECTBYINDEX:
                                    keywordRepository.selectByIndex(step.getLocatorType(), step.getLocatorValue(), step.getTestdata() != null && !step.getTestdata().isEmpty() ? Integer.parseInt(step.getTestdata()) : 0);
                                    actualResult = "Selected by index";
                                    break;
                                case ALERT_WITH_OK:
                                    keywordRepository.alertWithOk();
                                    actualResult = "Alert OK";
                                    break;
                                case ALERT_CONFIRMBOX_WITH_OK:
                                    keywordRepository.alertConfirmBoxWithOk();
                                    actualResult = "Alert confirm OK";
                                    break;
                                case ALERT_CONFIRMBOX_WITH_CANCEL:
                                    keywordRepository.alertConfirmBoxWithCancel();
                                    actualResult = "Alert confirm cancel";
                                    break;
                                case MOUSE_HOVER:
                                    keywordRepository.mouseHover(step.getLocatorType(), step.getLocatorValue());
                                    actualResult = "Mouse hover";
                                    break;
                                default:
                                    actualResult = "Unknown action: " + action;
                                    stepStatus = "FAIL";
                                    overallPass = false;
                            }
                        }
                    }
                } catch (Exception e) {
                    actualResult = e.getMessage();
                    stepStatus = "FAIL";
                    overallPass = false;
                }

                stepResult.setActualResult(actualResult);
                stepResult.setStepStatus(stepStatus);
                stepResults.add(stepResult);
            }
        } catch (Exception ex) {
            overallPass = false;
        }

        TestResult testResult = new TestResult();
        testResult.setReleaseID(releaseId);
        testResult.setProjectID(projectId);
        testResult.setRunID(runId);
        testResult.setTestCaseName(testCase.getTestCaseName());
        testResult.setTestStatus(overallPass ? "PASS" : "FAIL");
        testResult.setTestSteps(stepResults);

        return testResult;
    }
}