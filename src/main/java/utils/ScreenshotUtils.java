package utils;

import constants.AppConstants;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captures screenshots as TestName_DateTime.png under /screenshots
 * (spec section 26). Called from the TestNG listener on failure, and
 * can also be called ad hoc from a test/page object.
 */
public final class ScreenshotUtils {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotUtils.class);
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private ScreenshotUtils() {
    }

    /**
     * @return the absolute path of the saved screenshot, or null if capture failed
     *         (a screenshot failure must never fail the test itself).
     */
    public static String capture(TakesScreenshot driver, String testName) {
        try {
            File srcFile = driver.getScreenshotAs(OutputType.FILE);
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String fileName = sanitize(testName) + "_" + timestamp + ".png";

            File targetDir = new File(AppConstants.SCREENSHOT_DIR);
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
            File destFile = new File(targetDir, fileName);
            FileUtils.copyFile(srcFile, destFile);

            log.info("Screenshot captured: {}", destFile.getAbsolutePath());
            return destFile.getAbsolutePath();
        } catch (IOException | RuntimeException e) {
            log.warn("Failed to capture screenshot for test '{}'", testName, e);
            return null;
        }
    }

    private static String sanitize(String name) {
        return name.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}
