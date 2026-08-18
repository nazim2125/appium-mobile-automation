package pages;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.WaitUtils;

import java.time.Duration;
import java.util.List;

/**
 * Common behaviour shared by every page object. Concrete pages extend
 * this, declare their own @AndroidFindBy-annotated fields, and expose
 * screen-specific actions/assertion-helpers — tests should never call
 * WaitUtils or raw By locators directly.
 */
public abstract class BasePage {

    protected final AppiumDriver driver;
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected BasePage(AppiumDriver driver) {
        this.driver = driver;
        PageFactory.initElements(new io.appium.java_client.pagefactory.AppiumFieldDecorator(driver), this);
    }

    protected void click(WebElement element) {
        WaitUtils.fluentWaitUntil(driver, d -> element.isDisplayed() ? element : null);
        element.click();
    }

    protected void type(WebElement element, String text) {
        element.clear();
        element.sendKeys(text);
    }

    protected String textOf(WebElement element) {
        return element.getText();
    }

    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean isDisplayedQuietly(By locator, Duration timeout) {
        return WaitUtils.isDisplayedQuietly(driver, locator, timeout);
    }

    protected WebElement waitVisible(By locator) {
        return WaitUtils.waitForVisible(driver, locator);
    }

    protected List<WebElement> waitAllVisible(By locator) {
        return WaitUtils.waitForAllVisible(driver, locator);
    }
}
