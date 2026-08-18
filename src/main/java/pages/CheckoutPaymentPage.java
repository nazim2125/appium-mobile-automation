package pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

public class CheckoutPaymentPage extends BasePage {

    @AndroidFindBy(accessibility = "Payment method")
    private WebElement paymentMethodField;

    @AndroidFindBy(accessibility = "Continue button")
    private WebElement continueButton;

    public CheckoutPaymentPage(AppiumDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return isDisplayed(paymentMethodField);
    }

    public CheckoutPaymentPage selectPaymentMethod(String method) {
        type(paymentMethodField, method);
        return this;
    }

    public CheckoutOverviewPage continueToOverview() {
        click(continueButton);
        return new CheckoutOverviewPage(driver);
    }
}
