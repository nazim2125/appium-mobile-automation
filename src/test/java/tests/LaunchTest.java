package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;

/**
 * TC001 — Application Launch (spec section 6).
 */
public class LaunchTest extends BaseTest {

    @Test(groups = {"smoke"}, description = "App launches without crashing and lands on the Login screen")
    public void appLaunchesToLoginScreen() {
        // Arrange: driver + app launch happens in BaseTest.setUp()

        // Act
        LoginPage loginPage = loginPage();

        // Assert
        Assert.assertTrue(loginPage.isDisplayed(),
                "Expected the Login screen to be visible after a clean app launch");
    }
}
