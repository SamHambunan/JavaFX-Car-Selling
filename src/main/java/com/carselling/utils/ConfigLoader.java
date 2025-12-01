package com.carselling.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.io.File;

public class ConfigLoader {
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "db_config.properties";

    static {
        loadProperties();
    }

    private static void loadProperties() {
        // loading from the external file
        File externalConfig = new File(CONFIG_FILE);
        if (externalConfig.exists()) {
            try (InputStream input = new FileInputStream(externalConfig)) {
                properties.load(input);
                System.out.println("Loaded configuration from external file: " + externalConfig.getAbsolutePath());
                return;
            } catch (IOException e) {
                System.err.println("Failed to load external config, falling back to classpath.");
            }
        }

        // just go to the resources file
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                System.err.println("Sorry, unable to find " + CONFIG_FILE);
                return;
            }
            properties.load(input);
            System.out.println("Loaded configuration from internal resources.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}