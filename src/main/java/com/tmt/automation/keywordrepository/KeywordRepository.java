package com.tmt.automation.keywordrepository;

import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.tmt.automation.base.Base;

public class KeywordRepository {
    public Base base;
    public Properties prop;
    public WebDriver driver;
    WebElement element= null;
    Actions actions;
    Select selectDropDown;

    public void closeBrowser() {
            driver.quit();              
    }
    public void openBrowser(String browserType) {
        base = new Base();
        driver = base.init_driver(browserType); 
    }

    public void enterURL(String url) {
            driver.get(url);
    }  

    public void clickWebElement(String locatorType, String locatorValue) {
        GetWebElement.findElement(driver, locatorType, locatorValue).click();;
    }
    public void enterText(String locatorType, String locatorValue, String text) {
                element = GetWebElement.findElement(driver, locatorType, locatorValue);
                element.clear();
                element.sendKeys(text);  
    }
    public void waitForElement(int seconds) throws InterruptedException {
        Thread.sleep(seconds * 1000);
    }
    public void implicitlyWait(int seconds) {
        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(seconds));
    }
    public void explicitWait(String locatorType, String locatorValue, int seconds) {
        element = GetWebElement.findElement(driver, locatorType, locatorValue);
        WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(seconds));
        wait.until(ExpectedConditions.visibilityOf(element));
        
    }
    public void isElementDisplayed(String locatorType, String locatorValue) {
        element = GetWebElement.findElement(driver, locatorType, locatorValue);
        element.isDisplayed();
    }
    public void navigateTo(String url) {
        driver.navigate().to(url);
    }
    public void navigateBack() {
        driver.navigate().back();
    }
    public void navigateForward() {
        driver.navigate().forward();
    }
    public void navigateRefresh() {
        driver.navigate().refresh();
    }
    public void rightClick(String locatorType, String locatorValue) {
        actions = new Actions(driver);
        element = GetWebElement.findElement(driver, locatorType, locatorValue);
        actions.contextClick(element).build().perform();
    }
    public void doubleClick(String locatorType, String locatorValue) {
        actions = new Actions(driver);  
        element = GetWebElement.findElement(driver, locatorType, locatorValue);
        actions.doubleClick(element).build().perform();
    }
    public void selectByVisibleText(String locatorType, String locatorValue, String text) {
        element = GetWebElement.findElement(driver, locatorType, locatorValue);
        selectDropDown = new Select(element);
        selectDropDown.selectByVisibleText(text);
    }
    public void selectByValue(String locatorType, String locatorValue, String value) {
       element = GetWebElement.findElement(driver, locatorType, locatorValue);
        selectDropDown = new Select(element);
        selectDropDown.selectByValue(value);
    }
    public void selectByIndex(String locatorType, String locatorValue, int index) {
        element = GetWebElement.findElement(driver, locatorType, locatorValue);
        selectDropDown = new Select(element);
        selectDropDown.selectByIndex(index);
    }
    public void alertWithOk() {  
        driver.switchTo().alert().accept();
    }
    public void alertConfirmBoxWithOk() {
        driver.switchTo().alert().accept();
    }   
    public void alertConfirmBoxWithCancel() {
        driver.switchTo().alert().dismiss();
    }
    public void mouseHover(String locatorType, String locatorValue) {
        actions = new Actions(driver);
        element = GetWebElement.findElement(driver, locatorType, locatorValue);
        actions.moveToElement(element).build().perform();
    }
    public void mouseHoverAndClick(String locatorType, String locatorValue) {
        actions = new Actions(driver);
        element = GetWebElement.findElement(driver, locatorType, locatorValue);
        actions.moveToElement(element).click().build().perform();
    }
    public void mouseHoverAndClickByOffset(String locatorType, String locatorValue, int xOffset, int yOffset) {
        actions = new Actions(driver);  
        element = GetWebElement.findElement(driver, locatorType, locatorValue);
        actions.moveToElement(element, xOffset, yOffset).click().build().perform();
    }
}
