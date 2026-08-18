package tests;

import base.BaseTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.ProductCatalogPage;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Positive + negative login coverage (spec sections 7, 23).
 * Negative cases are data-driven from testdata/login_data.json so new
 * cases can be added without touching test code.
 */
public class LoginTest extends BaseTest {

    @Test(groups = {"smoke", "regression"}, description = "Valid credentials log the user into the product catalog")
    public void validLoginSucceeds() {
        // Arrange
        String username = ConfigReader.getRequired("testUsername");
        String password = ConfigReader.getRequired("testPassword");
        LoginPage loginPage = loginPage();

        // Act
        ProductCatalogPage catalog = loginPage.login(username, password);

        // Assert
        Assert.assertTrue(catalog.isDisplayed(), "Expected to land on the Product Catalog screen after valid login");
    }

    @Test(groups = {"regression", "negative"},
            dataProvider = "invalidLoginData",
            description = "Invalid/empty/malformed credential combinations must not log the user in")
    public void invalidLoginIsRejected(String caseName, String username, String password) {
        // Arrange
        LoginPage loginPage = loginPage();

        // Act
        LoginPage result = loginPage.loginExpectingFailure(username, password);

        // Assert
        Assert.assertTrue(result.isDisplayed(),
                "[" + caseName + "] Expected to remain on the Login screen for invalid credentials, "
                        + "but the app appears to have navigated away (unexpected login success?)");
    }

    @DataProvider(name = "invalidLoginData")
    public Object[][] invalidLoginData() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("testdata/login_data.json")) {
            List<Map<String, Object>> cases = mapper.readValue(is, List.class);
            Object[][] data = new Object[cases.size()][3];
            for (int i = 0; i < cases.size(); i++) {
                Map<String, Object> c = cases.get(i);
                data[i][0] = c.get("case");
                data[i][1] = c.get("username");
                data[i][2] = c.get("password");
            }
            return data;
        }
    }
}
