package driver;

import java.net.URI;
import java.net.URL;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import Utils.ConfigReader;

public class DriverManager {

    // ThreadLocal ensures each thread gets its OWN driver instance - critical if you scale to parallel execution later
    private static ThreadLocal<AndroidDriver> driver = new ThreadLocal<>();

    public static AndroidDriver getDriver() {
        return driver.get();
    }

    public static void initDriver() {
        try {
            UiAutomator2Options options = new UiAutomator2Options();
            options.setDeviceName(ConfigReader.get("deviceName"));
            options.setPlatformVersion(ConfigReader.get("platformVersion"));
            options.setApp(ConfigReader.get("appPath"));
            options.setAppPackage(ConfigReader.get("appPackage"));
            options.setAppActivity(ConfigReader.get("appActivity"));
            options.autoGrantPermissions();
            options.noReset();

            URL url = URI.create(ConfigReader.get("appiumServerUrl")).toURL();
            AndroidDriver androidDriver = new AndroidDriver(url, options);

            driver.set(androidDriver);
            System.out.println("Driver session started. Session ID: " + androidDriver.getSessionId());
        } catch (Exception e) {
            System.out.println("Driver initialization failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void quitDriver() {
        if (getDriver() != null) {
            getDriver().quit();
            System.out.println("Driver session ended.");
            driver.remove();   // prevents memory leaks in thread pools
        }
    }
}