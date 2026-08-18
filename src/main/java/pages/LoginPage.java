package pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

/**
 * Login screen of the Sauce Labs "My Demo App".
 * <p>
 * NOTE ON LOCATORS: the accessibility-id / content-desc values below are
 * the ones published in Sauce Labs' own My Demo App sample automation
 * code. They should still be re-verified against the exact APK version
 * you run using Appium Inspector before relying on this in CI — see
 * README "Inspecting the app" section for how. Never trust locators
 * you haven't confirmed against the running app.
 */
public class LoginPage extends BasePage {

    @AndroidFindBy(accessibility = "Username input field")
    private WebElement usernameField;

    @AndroidFindBy(accessibility = "Password input field")
    private WebElement passwordField;

    @AndroidFindBy(accessibility = "Login button")
    private WebElement loginButton;

    @AndroidFindBy(xpath = "//*[contains(@text,'do not match') or contains(@text,'required') or contains(@text,'locked')]")
    private WebElement errorMessage;

    public LoginPage(AppiumDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return isDisplayed(usernameField);
    }

    public LoginPage enterUsername(String username) {
        type(usernameField, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(passwordField, password);
        return this;
    }

    /** Convenience method combining the two field entries used by most tests/DataProviders. */
    public ProductCatalogPage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        return new ProductCatalogPage(driver);
    }

    /** Same as {@link #login} but for negative cases where login is expected to fail and stay on this screen. */
    public LoginPage loginExpectingFailure(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        return this;
    }

    public void clickLogin() {
        click(loginButton);
    }

    public boolean isErrorMessageDisplayed() {
        return isDisplayed(errorMessage);
    }

    public String getErrorMessageText() {
        return textOf(errorMessage);
    }
}
