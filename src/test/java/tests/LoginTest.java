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
 * Positive + negative login coverage.
 *
 * Application flow:
 *
 * Products
 *    ↓
 * View menu
 *    ↓
 * Log In
 *    ↓
 * Login screen
 *    ↓
 * Username + Password
 */
public class LoginTest extends BaseTest {

    @Test(
            groups = {"smoke", "regression"},
            description = "Valid credentials log the user into the product catalog"
    )
    public void validLoginSucceeds() {

        // Arrange
        String username =
                ConfigReader.getRequired("testUsername");

        String password =
                ConfigReader.getRequired("testPassword");

        /*
         * App launches on Product Catalog.
         */
        ProductCatalogPage catalog =
                productCatalogPage();

        /*
         * Products → View menu → Log In
         */
        LoginPage loginPage =
                catalog.openLogin();

        Assert.assertTrue(
                loginPage.isDisplayed(),
                "Expected Login screen to be displayed"
        );

        // Act
        ProductCatalogPage loggedInCatalog =
                loginPage.login(username, password);

        // Assert
        Assert.assertTrue(
                loggedInCatalog.isDisplayed(),
                "Expected to land on the Product Catalog screen after valid login"
        );
    }

    @Test(
        groups = {"regression", "negative"},
        dataProvider = "invalidLoginData",
        description = "Invalid/empty/malformed credential combinations must not log the user in"
)
public void invalidLoginIsRejected(
        String caseName,
        String username,
        String password) {

    // Arrange
    ProductCatalogPage catalog = productCatalogPage();

    LoginPage loginPage = catalog.openLogin();

    Assert.assertTrue(
            loginPage.isDisplayed(),
            "[" + caseName + "] Expected Login screen before entering credentials"
    );

    // Act
    loginPage.loginExpectingFailure(username, password);

    // Assert
    Assert.assertTrue(
            loginPage.isDisplayed(),
            "[" + caseName + "] Expected to remain on the Login screen after invalid login"
    );
}
    @DataProvider(name = "invalidLoginData")
    public Object[][] invalidLoginData() throws Exception {

        ObjectMapper mapper =
                new ObjectMapper();

        InputStream is =
                getClass()
                        .getClassLoader()
                        .getResourceAsStream(
                                "testdata/login_data.json"
                        );

        if (is == null) {
            throw new IllegalStateException(
                    "Could not find testdata/login_data.json on the test classpath"
            );
        }

        try (InputStream inputStream = is) {

            List<Map<String, Object>> cases =
                    mapper.readValue(
                            inputStream,
                            List.class
                    );

            Object[][] data =
                    new Object[cases.size()][3];

            for (int i = 0; i < cases.size(); i++) {

                Map<String, Object> testCase =
                        cases.get(i);

                data[i][0] =
                        testCase.get("case");

                data[i][1] =
                        testCase.get("username");

                data[i][2] =
                        testCase.get("password");
            }

            return data;
        }
    }
}
