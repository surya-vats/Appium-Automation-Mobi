package Utils;

import Page.LoginPage;
import Page.FormsPage;
import Page.BasePage;
import driver.DriverManager;
import io.appium.java_client.android.AndroidDriver;

public class NavigationHelper {

    private static boolean loggedIn = false;
    private static boolean menuSelected = false;

    public static void ensureLoggedInAndOnMenu(String menuName) {
        AndroidDriver driver = DriverManager.getDriver();

        if (!loggedIn) {
            System.out.println("Not logged in yet — logging in fresh");
            LoginPage loginPage = new LoginPage(driver);
            loginPage.login("9155208902", "Surya@123");
            loggedIn = true;
        } else {
            System.out.println("Already logged in this session — skipping login");
            try {
                new BasePage(driver).navigateBackFromForm();
                System.out.println("Navigated back from an open form screen");
            } catch (Exception e) {
                System.out.println("No open form screen detected — already on forms grid");
            }
        }

        if (!menuSelected) {
            System.out.println("Selecting menu: " + menuName);
            FormsPage formsPage = new FormsPage(driver);
            formsPage.selectMenu(menuName);
            menuSelected = true;
        } else {
            System.out.println("Menu already selected this session — skipping menu selection");
        }
    }
}