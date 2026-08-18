package pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

public class CheckoutAddressPage extends BasePage {

    private static final String PACKAGE =
            "com.saucelabs.mydemoapp.android";

    @AndroidFindBy(id = PACKAGE + ":id/fullNameET")
    private WebElement fullNameField;

    @AndroidFindBy(id = PACKAGE + ":id/address1ET")
    private WebElement addressLine1Field;

    @AndroidFindBy(id = PACKAGE + ":id/cityET")
    private WebElement cityField;

    @AndroidFindBy(id = PACKAGE + ":id/stateET")
    private WebElement stateField;

    @AndroidFindBy(id = PACKAGE + ":id/zipET")
    private WebElement zipField;

    @AndroidFindBy(xpath = "//*[@text='To Payment']")
    private WebElement continueButton;

    public CheckoutAddressPage(AppiumDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return isDisplayed(fullNameField);
    }

    public CheckoutAddressPage fillAddress(String fullName, String addressLine1, String city, String state, String zip) {
        type(fullNameField, fullName);
        type(addressLine1Field, addressLine1);
        type(cityField, city);
        type(stateField, state);
        type(zipField, zip);
        return this;
    }

    public CheckoutPaymentPage continueToPayment() {
        click(continueButton);
        return new CheckoutPaymentPage(driver);
    }
}
