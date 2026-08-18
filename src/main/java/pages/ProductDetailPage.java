package pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

public class ProductDetailPage extends BasePage {

    private static final String PACKAGE =
            "com.saucelabs.mydemoapp.android";

    @AndroidFindBy(id = PACKAGE + ":id/cartBt")
    private WebElement addToCartButton;

    @AndroidFindBy(id = PACKAGE + ":id/priceTV")
    private WebElement priceLabel;

    @AndroidFindBy(accessibility = "View cart")
    private WebElement cartButton;

    public ProductDetailPage(AppiumDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return isDisplayed(addToCartButton);
    }

    public String getPrice() {
        return textOf(priceLabel);
    }

    public ProductDetailPage addToCart() {
        click(addToCartButton);
        return this;
    }

    public CartPage openCart() {
        click(cartButton);
        return new CartPage(driver);
    }

    public void goBack() {
        driver.navigate().back();
    }
}
