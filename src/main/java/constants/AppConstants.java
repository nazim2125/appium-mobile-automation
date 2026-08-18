package constants;

import config.ConfigReader;

import java.time.Duration;

/**
 * Centralised, named constants so magic numbers/strings don't get
 * duplicated across page objects and tests.
 */
public final class AppConstants {

    private AppConstants() {
    }

    public static final Duration IMPLICIT_WAIT =
            Duration.ofSeconds(ConfigReader.getInt("implicitWaitSeconds", 5));

    public static final Duration EXPLICIT_WAIT =
            Duration.ofSeconds(ConfigReader.getInt("explicitWaitSeconds", 15));

    public static final Duration POLLING_INTERVAL = Duration.ofMillis(300);

    public static final String SCREENSHOT_DIR = "screenshots";
    public static final String REPORT_DIR = "reports";

    // My Demo App package/activity — overridable via config.properties
    public static final String APP_PACKAGE = ConfigReader.get("appPackage", "com.saucelabs.mydemoapp.android");
    public static final String APP_ACTIVITY = ConfigReader.get("appActivity",
            "com.saucelabs.mydemoapp.android.view.activities.SplashActivity");
}
