package pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

public class CheckoutPaymentPage extends BasePage {

    private static final String PACKAGE =
            "com.saucelabs.mydemoapp.android";

    @AndroidFindBy(id = PACKAGE + ":id/fullNameET")
    private WebElement fullNameField;

    @AndroidFindBy(id = PACKAGE + ":id/cardNumberET")
    private WebElement cardNumberField;

    @AndroidFindBy(id = PACKAGE + ":id/expirationDateET")
    private WebElement expirationDateField;

    @AndroidFindBy(id = PACKAGE + ":id/securityCodeET")
    private WebElement securityCodeField;

    @AndroidFindBy(xpath = "//*[@text='Review Order']")
    private WebElement continueButton;

    public CheckoutPaymentPage(AppiumDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return isDisplayed(cardNumberField);
    }

    public CheckoutPaymentPage selectPaymentMethod(String method) {
        type(fullNameField, "Mohd Nazim");
        type(cardNumberField, "4111111111111111");
        type(expirationDateField, "12/30");
        type(securityCodeField, "123");
        return this;
    }

    public CheckoutOverviewPage continueToOverview() {
        click(continueButton);
        return new CheckoutOverviewPage(driver);
    }
}
