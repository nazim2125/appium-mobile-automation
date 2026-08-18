package pages;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

/**
 * The hamburger/side-drawer menu — used to reach Log Out, About,
 * Webview, and the QR code scanner screens (spec sections 10, 32).
 */
public class MenuPage extends BasePage {

    @AndroidFindBy(accessibility = "Log Out")
    private WebElement logOutOption;

    @AndroidFindBy(accessibility = "Webview")
    private WebElement webviewOption;

    @AndroidFindBy(accessibility = "About")
    private WebElement aboutOption;

    @AndroidFindBy(accessibility = "Qr Code Scanner")
    private WebElement qrScannerOption;

    public MenuPage(AppiumDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return isDisplayed(logOutOption);
    }

    public LoginPage logOut() {
        click(logOutOption);
        return new LoginPage(driver);
    }

    public void openWebview() {
        click(webviewOption);
    }

    public void openAbout() {
        click(aboutOption);
    }

    public void openQrScanner() {
        click(qrScannerOption);
    }
}
