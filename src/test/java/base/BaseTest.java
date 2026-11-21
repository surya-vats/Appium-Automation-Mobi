package base;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import driver.DriverManager;

public class BaseTest {

    @BeforeSuite
    public void setUp() {
        System.out.println("===== TEST SETUP STARTED =====");
        DriverManager.initDriver();
    }

    @AfterSuite
    public void tearDown() {
        System.out.println("===== TEST TEARDOWN STARTED =====");
        DriverManager.quitDriver();
    }
}