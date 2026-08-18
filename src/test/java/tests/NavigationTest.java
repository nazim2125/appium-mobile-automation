package tests;

import base.BaseTest;
import config.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ProductCatalogPage;
import pages.ProductDetailPage;
import utils.GestureUtils;

/**
 * Navigation / Android back-button coverage (spec sections 10, 16).
 */
public class NavigationTest extends BaseTest {

    private static final String PRODUCT_NAME = "Sauce Labs Backpack";

    @Test(groups = {"regression"}, description = "Android back button from Product Detail returns to the Product Catalog")
    public void backButtonFromProductDetailReturnsToCatalog() {
        // Arrange
        String username = ConfigReader.getRequired("testUsername");
        String password = ConfigReader.getRequired("testPassword");
        ProductCatalogPage catalog = productCatalogPage()
                .openLogin()
                .login(username, password);

        // Act
        ProductDetailPage detail = catalog.openProduct(PRODUCT_NAME);
        Assert.assertTrue(detail.isDisplayed(), "Should be on the product detail screen before navigating back");
        GestureUtils.pressAndroidBack(driver());

        // Assert
        ProductCatalogPage catalogAfterBack = new ProductCatalogPage(driver());
        Assert.assertTrue(catalogAfterBack.isDisplayed(),
                "Android back button from Product Detail should return to the Product Catalog");
    }

    @Test(groups = {"regression"}, description = "App launches to product catalog before login")
    public void appLaunchesToProductCatalogBeforeLogin() {
        ProductCatalogPage catalog = new ProductCatalogPage(driver());

        Assert.assertTrue(catalog.isDisplayed(),
                "Sauce Labs My Demo App should launch to the Product Catalog screen");
    }
}
