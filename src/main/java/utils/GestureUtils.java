package utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * Reusable W3C Actions-based mobile gestures (spec sections 14–16).
 * Coordinates are derived from screen/element bounds at runtime rather
 * than hardcoded, so these work across different device resolutions.
 */
public final class GestureUtils {

    private static final int MAX_SWIPE_ATTEMPTS = 8;

    private GestureUtils() {
    }

    private static PointerInput finger() {
        return new PointerInput(PointerInput.Kind.TOUCH, "finger");
    }

    public static void tap(AppiumDriver driver, WebElement element) {
        element.click();
    }

    public static void longPress(AppiumDriver driver, WebElement element, Duration holdDuration) {
        Point center = elementCenter(element);
        PointerInput finger = finger();
        Sequence longPress = new Sequence(finger, 1);
        longPress.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), center.x, center.y));
        longPress.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        longPress.addAction(new Pause(finger, holdDuration));
        longPress.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(longPress));
    }

    public static void doubleTap(AppiumDriver driver, WebElement element) {
        Point center = elementCenter(element);
        PointerInput finger = finger();
        Sequence tap1 = buildTapSequence(finger, center, 1);
        Sequence tap2 = buildTapSequence(finger, center, 1);
        driver.perform(List.of(tap1));
        driver.perform(List.of(tap2));
    }

    private static Sequence buildTapSequence(PointerInput finger, Point point, int seq) {
        Sequence tap = new Sequence(finger, seq);
        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), point.x, point.y));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(new Pause(finger, Duration.ofMillis(80)));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        return tap;
    }

    /** Swipe from one point to another over the given duration. */
    public static void swipe(AppiumDriver driver, Point start, Point end, Duration duration) {
        PointerInput finger = finger();
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), start.x, start.y));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(duration, PointerInput.Origin.viewport(), end.x, end.y));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(swipe));
    }

    public static void swipeUp(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        Point start = new Point(size.width / 2, (int) (size.height * 0.75));
        Point end = new Point(size.width / 2, (int) (size.height * 0.25));
        swipe(driver, start, end, Duration.ofMillis(400));
    }

    public static void swipeDown(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        Point start = new Point(size.width / 2, (int) (size.height * 0.25));
        Point end = new Point(size.width / 2, (int) (size.height * 0.75));
        swipe(driver, start, end, Duration.ofMillis(400));
    }

    /** Pull-to-refresh: a deliberately slower downward swipe starting near the top. */
    public static void pullToRefresh(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        Point start = new Point(size.width / 2, (int) (size.height * 0.20));
        Point end = new Point(size.width / 2, (int) (size.height * 0.70));
        swipe(driver, start, end, Duration.ofMillis(900));
    }

    /**
     * Scrolls until the element identified by {@code locator} is visible, or
     * gives up after a bounded number of attempts (never scrolls forever).
     */
    public static WebElement scrollToElement(AppiumDriver driver, By locator) {
        for (int attempt = 0; attempt < MAX_SWIPE_ATTEMPTS; attempt++) {
            List<WebElement> found = driver.findElements(locator);
            if (!found.isEmpty() && found.get(0).isDisplayed()) {
                return found.get(0);
            }
            swipeUp(driver);
        }
        throw new org.openqa.selenium.NoSuchElementException(
                "Element " + locator + " not found after " + MAX_SWIPE_ATTEMPTS + " scroll attempts");
    }

    /** Presses the Android hardware/software back button. */
    public static void pressAndroidBack(AppiumDriver driver) {
        if (driver instanceof AndroidDriver androidDriver) {
            androidDriver.pressKey(new KeyEvent(AndroidKey.BACK));
        } else {
            driver.navigate().back();
        }
    }

    private static Point elementCenter(WebElement element) {
        Point location = element.getLocation();
        Dimension size = element.getSize();
        return new Point(location.x + size.width / 2, location.y + size.height / 2);
    }
}
