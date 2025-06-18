package com.tmt.automation.util;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class TestReport {
    
    static ExtentReports extentreport;
	static ExtentSparkReporter spark;
	static ExtentTest extentTest;

    public static void initializeReport(String reportPath,String testCaseName) {
        extentreport = new ExtentReports();
        spark = new ExtentSparkReporter(reportPath);
        extentreport.attachReporter(spark);
        extentreport.setSystemInfo("OS", System.getProperty("os.name"));
        extentreport.setSystemInfo("Java Version", System.getProperty("java.version"));
        extentreport.setSystemInfo("User", System.getProperty("user.name"));
        
    } 
    public static ExtentTest createTest(String testCaseName) {
        extentTest = extentreport.createTest(testCaseName);
        return extentTest;
    }  

}
