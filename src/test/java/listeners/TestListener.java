package listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import config.ConfigReader;
import driver.DriverFactory;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.ScreenshotUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * TestNG listener wiring together logging, screenshot capture on
 * failure, and ExtentReports (spec sections 27–29, 38).
 * <p>
 * Registered via testng.xml &lt;listeners&gt; so it applies to every suite
 * without each test class needing to reference it.
 */
public class TestListener implements ITestListener {

    private static final Logger log = LoggerFactory.getLogger(TestListener.class);
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> currentTest = new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) {
        if (extent == null) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            ExtentSparkReporter spark = new ExtentSparkReporter("reports/ExtentReport_" + timestamp + ".html");
            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Platform", ConfigReader.get("platform", "android"));
            extent.setSystemInfo("Environment", ConfigReader.get("environment", "qa"));
            extent.setSystemInfo("Device", ConfigReader.get("deviceName", "n/a"));
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        log.info("=== START: {} ===", result.getMethod().getMethodName());
        ExtentTest test = extent.createTest(result.getMethod().getMethodName(), result.getMethod().getDescription());
        test.assignCategory(result.getMethod().getGroups());
        currentTest.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("=== PASS: {} ({} ms) ===", result.getMethod().getMethodName(), result.getEndMillis() - result.getStartMillis());
        currentTest.get().log(Status.PASS, "Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("=== FAIL: {} ===", result.getMethod().getMethodName(), result.getThrowable());

        String screenshotPath = null;
        try {
            Object driver = DriverFactory.getDriver();
            if (driver instanceof TakesScreenshot ts) {
                screenshotPath = ScreenshotUtils.capture(ts, result.getMethod().getMethodName());
            }
        } catch (Exception e) {
            log.warn("Could not capture failure screenshot", e);
        }

        ExtentTest test = currentTest.get();
        test.log(Status.FAIL, "Test failed: " + result.getThrowable());
        if (screenshotPath != null) {
            try {
                test.fail("Screenshot on failure", MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            } catch (Exception e) {
                log.warn("Could not attach screenshot to report", e);
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("=== SKIPPED: {} ===", result.getMethod().getMethodName());
        currentTest.get().log(Status.SKIP, "Test skipped: " + result.getThrowable());
    }

    @Override
    public void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
        }
    }
}
