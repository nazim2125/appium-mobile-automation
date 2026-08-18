package pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

public class CheckoutAddressPage extends BasePage {

    @AndroidFindBy(accessibility = "Fullname input field")
    private WebElement fullNameField;

    @AndroidFindBy(accessibility = "Address line 1 input field")
    private WebElement addressLine1Field;

    @AndroidFindBy(accessibility = "City input field")
    private WebElement cityField;

    @AndroidFindBy(accessibility = "State input field")
    private WebElement stateField;

    @AndroidFindBy(accessibility = "Zip code input field")
    private WebElement zipField;

    @AndroidFindBy(accessibility = "Continue button")
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
