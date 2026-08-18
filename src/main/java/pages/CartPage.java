package pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CartPage extends BasePage {

    @AndroidFindBy(xpath = "//*[@text='Proceed To Checkout']")
    private WebElement checkoutButton;

    @AndroidFindBy(xpath = "//*[@text='Remove Item']")
    private List<WebElement> removeButtons;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/titleTV")
    private List<WebElement> cartItems;

    public CartPage(AppiumDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return isDisplayed(checkoutButton) || !cartItems.isEmpty();
    }

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }

    public int getItemCount() {
        return cartItems.size();
    }

    public CheckoutAddressPage proceedToCheckout() {
        click(checkoutButton);
        return new CheckoutAddressPage(driver);
    }

    public CartPage removeFirstItem() {
        if (!removeButtons.isEmpty()) {
            click(removeButtons.get(0));
        }
        return this;
    }
}
