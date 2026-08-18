package driver;

import config.ConfigReader;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Builds and owns the Appium driver instance for the current thread.
 * <p>
 * ThreadLocal storage (spec section 30 — Parallel Execution) means each
 * TestNG thread gets its own isolated driver, so tests for
 * Android + Android + iOS can run in parallel without one test's driver
 * calls leaking into another's session.
 * <p>
 * Supports, via config.properties / -D overrides:
 * - Android emulator or real device (UiAutomator2)
 * - iOS simulator or real device (XCUITest)
 * - Local Appium server or a remote/cloud Appium server (e.g. a device farm)
 */
public final class DriverFactory {

    private static final Logger log = LoggerFactory.getLogger(DriverFactory.class);
    private static final ThreadLocal<AppiumDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static AppiumDriver getDriver() {
        AppiumDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("Driver has not been initialised for this thread. Call initDriver() first.");
        }
        return driver;
    }

    public static AndroidDriver getAndroidDriver() {
        return (AndroidDriver) getDriver();
    }

    public static void initDriver() {
        String platform = ConfigReader.get("platform", "android").toLowerCase();
        log.info("Initialising Appium driver for platform '{}'", platform);

        AppiumDriver driver = switch (platform) {
            case "android" -> createAndroidDriver();
            case "ios" -> createIosDriver();
            default -> throw new IllegalArgumentException(
                    "Unsupported platform '" + platform + "'. Use 'android' or 'ios'.");
        };

        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(ConfigReader.getInt("implicitWaitSeconds", 5)));

        DRIVER.set(driver);
    }

    private static AndroidDriver createAndroidDriver() {
        UiAutomator2Options options = new UiAutomator2Options();

        options.setDeviceName(ConfigReader.get("deviceName", "emulator-5554"));
        options.setAutomationName(ConfigReader.get("automationName", "UiAutomator2"));

        String udid = ConfigReader.get("udid");
        if (udid != null) {
            options.setUdid(udid); // real device
        }

        String platformVersion = ConfigReader.get("platformVersion");
        if (platformVersion != null) {
            options.setPlatformVersion(platformVersion);
        }

        // Either install from a local/CI-built APK path, or attach to an
        // already-installed app via package + activity.
        boolean useInstalledApp = ConfigReader.getBoolean("useInstalledApp", false);
        String appPath = ConfigReader.get("app");
        if (appPath != null && !useInstalledApp) {
            options.setApp(appPath);
        } else {
            options.setAppPackage(ConfigReader.getRequired("appPackage"));
            options.setAppActivity(ConfigReader.getRequired("appActivity"));
        }

        options.setNoReset(ConfigReader.getBoolean("noReset", false));
        options.setFullReset(ConfigReader.getBoolean("fullReset", false));
        options.setAutoGrantPermissions(ConfigReader.getBoolean("autoGrantPermissions", true));
        options.setNewCommandTimeout(Duration.ofSeconds(ConfigReader.getInt("newCommandTimeout", 120)));

        return new AndroidDriver(buildServerUrl(), options);
    }

    private static AppiumDriver createIosDriver() {
        XCUITestOptions options = new XCUITestOptions();

        options.setDeviceName(ConfigReader.get("deviceName", "iPhone 15"));
        options.setAutomationName(ConfigReader.get("automationName", "XCUITest"));

        String udid = ConfigReader.get("udid");
        if (udid != null) {
            options.setUdid(udid); // real device
        }

        String platformVersion = ConfigReader.get("platformVersion");
        if (platformVersion != null) {
            options.setPlatformVersion(platformVersion);
        }

        String appPath = ConfigReader.get("app");
        if (appPath != null) {
            options.setApp(appPath);
        } else {
            options.setBundleId(ConfigReader.getRequired("bundleId"));
        }

        options.setNoReset(ConfigReader.getBoolean("noReset", false));
        options.setNewCommandTimeout(Duration.ofSeconds(ConfigReader.getInt("newCommandTimeout", 120)));

        return new IOSDriver(buildServerUrl(), options);
    }

    private static URL buildServerUrl() {
        String url = ConfigReader.get("appiumServerUrl", "http://127.0.0.1:4723");
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid appiumServerUrl: " + url, e);
        }
    }

    public static void quitDriver() {
        AppiumDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
                log.info("Driver session closed");
            } catch (Exception e) {
                log.warn("Error while quitting driver", e);
            } finally {
                DRIVER.remove();
            }
        }
    }
}
