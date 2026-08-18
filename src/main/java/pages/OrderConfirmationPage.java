package pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

public class OrderConfirmationPage extends BasePage {

    @AndroidFindBy(accessibility = "Thank You header")
    private WebElement thankYouHeader;

    @AndroidFindBy(accessibility = "Continue Shopping button")
    private WebElement continueShoppingButton;

    public OrderConfirmationPage(AppiumDriver driver) {
        super(driver);
    }

    public boolean isOrderConfirmed() {
        return isDisplayed(thankYouHeader);
    }

    public ProductCatalogPage continueShopping() {
        click(continueShoppingButton);
        return new ProductCatalogPage(driver);
    }
}
