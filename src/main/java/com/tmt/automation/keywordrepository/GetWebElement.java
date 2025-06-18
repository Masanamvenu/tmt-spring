package com.tmt.automation.keywordrepository;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class GetWebElement {
    
    /**
     * Finds a WebElement based on locator type and value.
     */
    public static WebElement findElement(WebDriver driver, String locatorType, String locatorValue) {
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
