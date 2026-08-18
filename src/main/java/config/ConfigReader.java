package config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Central configuration reader.
 * <p>
 * Resolution order for any key (highest priority first):
 * 1. JVM system property   (-Dplatform=android)
 * 2. Environment variable  (PLATFORM=android)
 * 3. src/test/resources/config.properties
 * <p>
 * This lets the same framework run locally (via the properties file),
 * on a CI runner (via env vars / secrets), or be overridden ad-hoc from
 * the Maven command line (mvn test -Dplatform=android -Denv=qa) without
 * touching any code — see spec section 24 (Configuration Management).
 */
public final class ConfigReader {

    private static final Logger log = LoggerFactory.getLogger(ConfigReader.class);
    private static final Properties PROPERTIES = new Properties();
    private static final String CONFIG_FILE = "config.properties";

    static {
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new RuntimeException("Unable to find " + CONFIG_FILE + " on the classpath");
            }
            PROPERTIES.load(input);
            log.info("Loaded configuration from {}", CONFIG_FILE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + CONFIG_FILE, e);
        }
    }

    private ConfigReader() {
        // static utility class
    }

    public static String get(String key) {
        // 1. System property
        String value = System.getProperty(key);
        if (isPresent(value)) {
            return value.trim();
        }

        // 2. Environment variable — support both exact key and UPPER_SNAKE_CASE
        value = System.getenv(key);
        if (!isPresent(value)) {
            value = System.getenv(toEnvStyle(key));
        }
        if (isPresent(value)) {
            return value.trim();
        }

        // 3. Properties file
        value = PROPERTIES.getProperty(key);
        if (isPresent(value)) {
            return value.trim();
        }

        return null;
    }

    public static String get(String key, String defaultValue) {
        String value = get(key);
        return value != null ? value : defaultValue;
    }

    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("Config key '{}' = '{}' is not a valid int, using default {}", key, value, defaultValue);
            return defaultValue;
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public static String getRequired(String key) {
        String value = get(key);
        if (!isPresent(value)) {
            throw new IllegalStateException(
                    "Required configuration key '" + key + "' is not set (checked -D, env var, and " + CONFIG_FILE + ")");
        }
        return value;
    }

    private static boolean isPresent(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static String toEnvStyle(String key) {
        // platformVersion -> PLATFORM_VERSION, appiumServerUrl -> APPIUM_SERVER_URL
        StringBuilder sb = new StringBuilder();
        for (char c : key.toCharArray()) {
            if (Character.isUpperCase(c)) {
                sb.append('_');
            }
            sb.append(Character.toUpperCase(c));
        }
        return sb.toString();
    }
}
