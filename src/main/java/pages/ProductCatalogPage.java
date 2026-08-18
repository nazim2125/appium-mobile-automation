package pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import utils.GestureUtils;

import java.util.List;

/**
 * Product catalog / home screen — the first screen after a successful login.
 */
public class ProductCatalogPage extends BasePage {

    @AndroidFindBy(accessibility = "Cart button")
    private WebElement cartButton;

    @AndroidFindBy(accessibility = "open menu")
    private WebElement menuButton;

    @AndroidFindBy(accessibility = "Sort button")
    private WebElement sortButton;

    @AndroidFindBy(className = "android.widget.TextView")
    private List<WebElement> productTitles;

    public ProductCatalogPage(AppiumDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return isDisplayed(cartButton);
    }

    public ProductDetailPage openProduct(String productName) {
        By productLocator = By.xpath("//*[@text='" + productName + "']");
        WebElement product = GestureUtils.scrollToElement(driver, productLocator);
        click(product);
        return new ProductDetailPage(driver);
    }

    public CartPage openCart() {
        click(cartButton);
        return new CartPage(driver);
    }

    public MenuPage openMenu() {
        click(menuButton);
        return new MenuPage(driver);
    }

    public int getVisibleProductCount() {
        return productTitles.size();
    }
}
