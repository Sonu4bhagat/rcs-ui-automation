package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties;

    static {
        try {
            FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
            properties = new Properties();
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load config.properties file.");
        }
    }

    /**
     * Resolution order:
     * 1. JVM system property (e.g. -Demail.recipients=... passed from Jenkins)
     * 2. Environment variable, key upper-cased with dots as underscores
     *    (e.g. EMAIL_PASSWORD injected from Jenkins credentials)
     * 3. config.properties
     */
    public static String get(String key) {
        String value = System.getProperty(key);
        if (value != null && !value.trim().isEmpty()) {
            return value.trim();
        }
        value = System.getenv(key.toUpperCase().replace('.', '_'));
        if (value != null && !value.trim().isEmpty()) {
            return value.trim();
        }
        value = properties.getProperty(key);
        return value != null ? value.trim() : null;
    }
}
