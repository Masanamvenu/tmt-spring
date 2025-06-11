package com.tmt.automation.util;

import com.tmt.automation.dto.TestCaseBatchRequest;
import com.tmt.automation.model.TestCase;
import com.tmt.automation.model.TestStep;
import com.tmt.automation.model.BrowserAction;

import java.util.List;
import java.util.stream.Collectors;

public class TestCaseMapper {
    public static TestCase toEntity(TestCaseBatchRequest dto) {
        TestCase entity = new TestCase();
        entity.setTestCaseName(dto.getTestCaseName());
        entity.setReleaseID(dto.getReleaseID());
        entity.setRunID(dto.getRunID());
        entity.setProjectID(dto.getProjectID());
        entity.setTestSteps(
            dto.getTestSteps().stream().map(TestCaseMapper::toStepEntity).collect(Collectors.toList())
        );
        return entity;
    }

    private static TestStep toStepEntity(TestCaseBatchRequest.TestStepRequest dto) {
        TestStep entity = new TestStep();
        entity.setTestSteps(dto.getTestSteps());
        entity.setExpectedResult(dto.getExpectedResult());
        entity.setActualResult(dto.getActualResult());
        entity.setLocatorType(dto.getLocatorType());
        entity.setLocatorValue(dto.getLocatorValue());
        entity.setTestdata(dto.getTestdata());
        if (dto.getBrowserActions() != null) {
            entity.setBrowserActions(
                dto.getBrowserActions().stream()
                    .map(BrowserAction::valueOf)
                    .collect(Collectors.toList())
            );
        }
        return entity;
    }
}