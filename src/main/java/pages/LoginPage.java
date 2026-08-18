package pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.Duration;

public class LoginPage extends BasePage {

    private static final String PACKAGE =
            "com.saucelabs.mydemoapp.android";

    private static final By USERNAME_FIELD =
            By.id(PACKAGE + ":id/nameET");

    private static final By LOGIN_TITLE =
            By.id(PACKAGE + ":id/loginTV");

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/nameET")
    private WebElement usernameField;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/passwordET")
    private WebElement passwordField;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/loginBtn")
    private WebElement loginButton;

    public LoginPage(AppiumDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return isDisplayedQuietly(USERNAME_FIELD, Duration.ofSeconds(2))
                || isDisplayedQuietly(LOGIN_TITLE, Duration.ofSeconds(2));
    }

    public LoginPage enterUsername(String username) {
        type(usernameField, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(passwordField, password);
        return this;
    }

    public ProductCatalogPage login(
            String username,
            String password) {

        enterUsername(username);
        enterPassword(password);
        clickLogin();

        return new ProductCatalogPage(driver);
    }

    public LoginPage loginExpectingFailure(
            String username,
            String password) {

        enterUsername(username);
        enterPassword(password);
        clickLogin();

        return this;
    }

    public void clickLogin() {
        click(loginButton);
    }
}
