package base;

import driver.DriverFactory;
import io.appium.java_client.AppiumDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import pages.LoginPage;
import pages.ProductCatalogPage;

/**
 * Base test class for all Appium tests.
 *
 * Each @Test method receives a fresh Appium driver session.
 * The driver is stored in ThreadLocal inside DriverFactory,
 * allowing safe parallel execution across devices.
 */
public abstract class BaseTest {

    protected final Logger log =
            LoggerFactory.getLogger(getClass());

    @BeforeMethod(alwaysRun = true)
    public void setUp() {

        DriverFactory.initDriver();

        log.info(
                "Driver initialised for thread {}",
                Thread.currentThread().getId()
        );
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {

        DriverFactory.quitDriver();

        log.info(
                "Driver session closed for test '{}'",
                result.getMethod().getMethodName()
        );
    }

    /**
     * Returns the current Appium driver.
     */
    protected AppiumDriver driver() {
        return DriverFactory.getDriver();
    }

    /**
     * Returns the Login page.
     */
    protected LoginPage loginPage() {
        return new LoginPage(driver());
    }

    /**
     * Returns the Product Catalog page.
     */
    protected ProductCatalogPage productCatalogPage() {
        return new ProductCatalogPage(driver());
    }
}