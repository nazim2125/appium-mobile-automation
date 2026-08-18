package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ProductCatalogPage;

/**
 * TC001 — Application Launch.
 *
 * Verifies that the My Demo App launches successfully
 * and displays the Product Catalog screen.
 */
public class LaunchTest extends BaseTest {

    @Test(
            groups = {"smoke"},
            description = "App launches without crashing and displays the Product Catalog screen"
    )
    public void appLaunchesToProductCatalogScreen() {

        ProductCatalogPage productCatalogPage =
                productCatalogPage();

        Assert.assertTrue(
                productCatalogPage.isDisplayed(),
                "Expected the Product Catalog screen to be visible after app launch"
        );
    }
}