package pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import utils.GestureUtils;

import java.util.List;

/**
 * Product catalog / home screen.
 *
 * Locators verified against the running Sauce Labs My Demo App APK.
 */
public class ProductCatalogPage extends BasePage {

    private static final String PACKAGE =
            "com.saucelabs.mydemoapp.android";

    /*
     * Verified from UI hierarchy:
     * content-desc="View cart"
     */
    @AndroidFindBy(accessibility = "View cart")
    private WebElement cartButton;

    /*
     * Verified from UI hierarchy:
     * content-desc="View menu"
     */
    @AndroidFindBy(accessibility = "View menu")
    private WebElement menuButton;

    /*
     * Verified from UI hierarchy:
     * content-desc="Shows current sorting order and displays available sorting options"
     */
    @AndroidFindBy(
        accessibility =
            "Shows current sorting order and displays available sorting options"
    )
    private WebElement sortButton;

    /*
     * Verified from UI hierarchy:
     * resource-id="...:id/titleTV"
     *
     * This represents individual product titles.
     */
    @AndroidFindBy(
        id = PACKAGE + ":id/titleTV"
    )
    private List<WebElement> productTitles;

    /*
     * Verified product catalog title/container.
     */
    @AndroidFindBy(
        id = PACKAGE + ":id/productTV"
    )
    private WebElement productCatalogTitle;

    public ProductCatalogPage(AppiumDriver driver) {
        super(driver);
    }

    /**
     * Verifies that the Product Catalog is displayed.
     */
    public boolean isDisplayed() {
        return isDisplayed(productCatalogTitle);
    }

    /**
     * Opens a product by its visible product name.
     */
    public ProductDetailPage openProduct(String productName) {

        By productLocator = By.xpath(
            "//*[@text=" + xpathLiteral(productName) + "]"
        );

        WebElement product =
                GestureUtils.scrollToElement(driver, productLocator);

        String productImageXpath = "//*[@text=" + xpathLiteral(productName)
                + "]/preceding-sibling::android.widget.ImageView[@resource-id='"
                + PACKAGE + ":id/productIV']";

        WebElement productImage =
                driver.findElement(By.xpath(productImageXpath));

        click(productImage);

        return new ProductDetailPage(driver);
    }

    /**
     * Opens the shopping cart.
     */
    public CartPage openCart() {

        click(cartButton);

        return new CartPage(driver);
    }

    /**
     * Opens the side menu.
     */
    public MenuPage openMenu() {

        click(menuButton);

        return new MenuPage(driver);
    }

    /**
     * Opens the Login screen through:
     *
     * Products → View menu → Login Menu Item
     */
    public LoginPage openLogin() {

        click(menuButton);

        // Verified accessibility ID from actual UI hierarchy.
        WebElement loginMenuItem = driver.findElement(
            io.appium.java_client.AppiumBy.accessibilityId(
                "Login Menu Item"
            )
        );

        click(loginMenuItem);

        return new LoginPage(driver);
    }

    /**
     * Returns the number of product title elements currently
     * available in the UI hierarchy.
     */
    public int getVisibleProductCount() {

        return productTitles.size();
    }

    /**
     * Safely creates an XPath string literal.
     *
     * This handles product names containing apostrophes.
     */
    private String xpathLiteral(String value) {

        if (!value.contains("'")) {
            return "'" + value + "'";
        }

        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }

        String[] parts = value.split("'");

        StringBuilder xpath = new StringBuilder("concat(");

        for (int i = 0; i < parts.length; i++) {

            if (i > 0) {
                xpath.append(", \"'\", ");
            }

            xpath.append("'")
                 .append(parts[i])
                 .append("'");
        }

        xpath.append(")");

        return xpath.toString();
    }
}
