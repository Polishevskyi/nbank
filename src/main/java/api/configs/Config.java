package api.configs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Config INSTANCE = new Config();
    private final Properties properties = new Properties();

    private Config() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties not found in resources");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Fail to load config.properties", e);
        }
    }

    public static String getProperty(String key) {
        // PRIORITY 1 is the baseApiUrl =... system property.
        String systemValue = System.getProperty(key);

        if (systemValue != null) {
            return systemValue;
        }

        // PRIORITY 2 is an environment variable baseApiUrl - BASEAPIURL
        // admin.username -> ADMIN_USERNAME
        String envKey = key.toUpperCase().replace('.', '_');

        String envValue = System.getenv(envKey);
        if (envValue != null) {
            return envValue;
        }

        // PRIORITY 3 is config.properties
        return INSTANCE.properties.getProperty(key);
    }
}
