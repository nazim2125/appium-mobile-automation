package base;

import driver.DriverFactory;
import io.appium.java_client.AppiumDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import pages.LoginPage;

/**
 * Every test class extends this. Each @Test method gets a fresh driver
 * session (BeforeMethod/AfterMethod, not BeforeClass) so one test's
 * failure/leftover app state can never bleed into the next test — and
 * because the driver lives in a ThreadLocal, this is safe for parallel
 * execution across multiple devices (spec section 30).
 */
public abstract class BaseTest {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverFactory.initDriver();
        log.info("Driver initialised for thread {}", Thread.currentThread().getId());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        DriverFactory.quitDriver();
        log.info("Driver session closed for test '{}'", result.getMethod().getMethodName());
    }

    protected AppiumDriver driver() {
        return DriverFactory.getDriver();
    }

    protected LoginPage loginPage() {
        return new LoginPage(driver());
    }
}
