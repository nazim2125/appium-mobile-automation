package utils;

import constants.AppConstants;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

/**
 * Reusable explicit/fluent wait helpers. No test or page object should
 * ever call Thread.sleep() directly — everything waits on an actual
 * condition instead (spec section 25).
 */
public final class WaitUtils {

    private WaitUtils() {
    }

    private static WebDriverWait wait(AppiumDriver driver) {
        return new WebDriverWait(driver, AppConstants.EXPLICIT_WAIT);
    }

    public static WebElement waitForVisible(AppiumDriver driver, By locator) {
        return wait(driver).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForClickable(AppiumDriver driver, By locator) {
        return wait(driver).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static WebElement waitForPresence(AppiumDriver driver, By locator) {
        return wait(driver).until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public static List<WebElement> waitForAllVisible(AppiumDriver driver, By locator) {
        return wait(driver).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public static boolean waitForInvisible(AppiumDriver driver, By locator) {
        return wait(driver).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Polls repeatedly (ignoring transient StaleElementReferenceExceptions) until
     * the supplied condition is true, or a screen-load/animation-dependent
     * condition settles. Use for cases plain ExpectedConditions doesn't cover,
     * e.g. "wait until this screen's item count stabilises".
     */
    public static <T> T fluentWaitUntil(AppiumDriver driver, Function<AppiumDriver, T> condition) {
        Wait<AppiumDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(AppConstants.EXPLICIT_WAIT)
                .pollingEvery(AppConstants.POLLING_INTERVAL)
                .ignoring(StaleElementReferenceException.class);
        return fluentWait.until(condition);
    }

    public static boolean isDisplayedQuietly(AppiumDriver driver, By locator, Duration timeout) {
        try {
            new WebDriverWait(driver, timeout).until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
