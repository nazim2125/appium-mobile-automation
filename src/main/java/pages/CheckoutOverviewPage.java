package pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

public class CheckoutOverviewPage extends BasePage {

    @AndroidFindBy(accessibility = "Place Order button")
    private WebElement placeOrderButton;

    @AndroidFindBy(accessibility = "Total price label")
    private WebElement totalPriceLabel;

    public CheckoutOverviewPage(AppiumDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return isDisplayed(placeOrderButton);
    }

    public String getTotalPrice() {
        return textOf(totalPriceLabel);
    }

    public OrderConfirmationPage placeOrder() {
        click(placeOrderButton);
        return new OrderConfirmationPage(driver);
    }
}
