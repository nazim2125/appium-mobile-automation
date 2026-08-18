package pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

public class ProductDetailPage extends BasePage {

    @AndroidFindBy(accessibility = "Add To Cart button")
    private WebElement addToCartButton;

    @AndroidFindBy(accessibility = "Product price")
    private WebElement priceLabel;

    @AndroidFindBy(accessibility = "Cart button")
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
