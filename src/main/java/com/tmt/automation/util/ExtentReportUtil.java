package com.tmt.automation.util;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ExtentReportUtil {
    private ExtentReports extentReports;
    private Map<String, ExtentTest> testMap = new ConcurrentHashMap<>();
    private String reportPath;

    public void initReport(String projectId, String releaseId, String runId, String currentDate) {
        String baseDir = System.getProperty("user.dir") + "/src/reports";
        String runDir = baseDir + "/" + runId;
        File dir = new File(runDir);
        if (!dir.exists()) dir.mkdirs();

        // Clean up old run folders, keep only 5 latest
        cleanupOldRuns(baseDir, 5);

        String fileName = String.format("%s_%s_%s_%s.html", projectId, releaseId, runId, currentDate);
        reportPath = runDir + "/" + fileName;

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setTheme(com.aventstack.extentreports.reporter.configuration.Theme.DARK);
        sparkReporter.config().setDocumentTitle("Functional Test");
        sparkReporter.config().setReportName("Functional Test Result");
        sparkReporter.config().setProtocol(com.aventstack.extentreports.reporter.configuration.Protocol.HTTPS);

        extentReports = new ExtentReports();
        extentReports.attachReporter(sparkReporter);
        extentReports.setSystemInfo("Environment", "QA");
        extentReports.setSystemInfo("User", "sairamprince33@gmail.com");
        extentReports.setSystemInfo("OS", System.getProperty("os.name"));
        extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
        testMap.clear();
    }

    public ExtentTest createTest(String testCaseName) {
        ExtentTest test = extentReports.createTest(testCaseName);
        testMap.put(testCaseName, test);
        return test;
    }

    public ExtentTest getTest(String testCaseName) {
        return testMap.get(testCaseName);
    }

    public void logStep(String testCaseName, Status status, String details) {
        ExtentTest test = getTest(testCaseName);
        if (test != null) {
            test.log(status, details);
        }
    }

    public void flushReport() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }

    private void cleanupOldRuns(String baseDir, int keepLatest) {
        File dir = new File(baseDir);
        File[] runDirs = dir.listFiles(File::isDirectory);
        if (runDirs == null || runDirs.length <= keepLatest) return;

        Arrays.sort(runDirs, Comparator.comparingLong(File::lastModified).reversed());
        for (int i = keepLatest; i < runDirs.length; i++) {
            deleteDirectory(runDirs[i]);
        }
    }

    private void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                deleteDirectory(file);
            }
        }
        dir.delete();
    }
}