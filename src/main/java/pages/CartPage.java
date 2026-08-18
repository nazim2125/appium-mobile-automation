package pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CartPage extends BasePage {

    @AndroidFindBy(accessibility = "Proceed To Checkout button")
    private WebElement checkoutButton;

    @AndroidFindBy(accessibility = "Remove")
    private List<WebElement> removeButtons;

    @AndroidFindBy(xpath = "//*[contains(@resource-id,'cart_item') or contains(@class,'TextView')]")
    private List<WebElement> cartItems;

    public CartPage(AppiumDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return isDisplayed(checkoutButton) || !cartItems.isEmpty();
    }

    public boolean isEmpty() {
        return removeButtons.isEmpty();
    }

    public int getItemCount() {
        return removeButtons.size();
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
