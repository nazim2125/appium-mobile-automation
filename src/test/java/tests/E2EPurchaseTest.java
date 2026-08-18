package tests;

import base.BaseTest;
import config.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.*;

/**
 * End-to-end business flow (spec section 22):
 * Launch -> Login -> Browse -> Product Detail -> Add to Cart -> Cart ->
 * Checkout (address, payment, overview) -> Place Order -> Confirmation
 * -> Continue Shopping -> Logout.
 * <p>
 * Uses only page objects and their reusable methods — no raw locators
 * or driver calls live in the test itself.
 */
public class E2EPurchaseTest extends BaseTest {

    private static final String PRODUCT_NAME = "Sauce Labs Backpack";

    @Test(groups = {"e2e", "regression"}, description = "Full purchase journey from login to order confirmation")
    public void completesEndToEndPurchase() {
        // Arrange
        String username = ConfigReader.getRequired("testUsername");
        String password = ConfigReader.getRequired("testPassword");

        // Act + Assert, one business step at a time
        ProductCatalogPage catalog = loginPage().login(username, password);
        Assert.assertTrue(catalog.isDisplayed(), "Login should land on the product catalog");

        ProductDetailPage productDetail = catalog.openProduct(PRODUCT_NAME);
        Assert.assertTrue(productDetail.isDisplayed(), "Product detail screen should open for " + PRODUCT_NAME);

        productDetail.addToCart();
        CartPage cart = productDetail.openCart();
        Assert.assertFalse(cart.isEmpty(), "Cart should contain the item just added");
        Assert.assertEquals(cart.getItemCount(), 1, "Cart should contain exactly one item");

        CheckoutAddressPage addressPage = cart.proceedToCheckout();
        Assert.assertTrue(addressPage.isDisplayed(), "Checkout should start on the address screen");

        CheckoutPaymentPage paymentPage = addressPage
                .fillAddress("Mohd Nazim", "123 Sector 62", "Noida", "Uttar Pradesh", "201301")
                .continueToPayment();
        Assert.assertTrue(paymentPage.isDisplayed(), "Should reach the payment screen after address entry");

        CheckoutOverviewPage overviewPage = paymentPage
                .selectPaymentMethod("Credit Card")
                .continueToOverview();
        Assert.assertTrue(overviewPage.isDisplayed(), "Should reach the order overview screen");

        OrderConfirmationPage confirmation = overviewPage.placeOrder();
        Assert.assertTrue(confirmation.isOrderConfirmed(), "Order should be confirmed after placing it");

        // Cleanup the business flow: return to catalog and log out
        ProductCatalogPage backToCatalog = confirmation.continueShopping();
        Assert.assertTrue(backToCatalog.isDisplayed(), "Continue Shopping should return to the product catalog");

        LoginPage loggedOut = backToCatalog.openMenu().logOut();
        Assert.assertTrue(loggedOut.isDisplayed(), "Logout should return the user to the Login screen");
    }
}
