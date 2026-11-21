package tests;

import org.testng.annotations.Test;
import base.BaseTest;
import Utils.NavigationHelper;

public class LoginTest extends BaseTest {

    @Test(priority = 1)
    public void verifyLogin() {
        System.out.println("********** LoginTest STARTED **********");
        NavigationHelper.ensureLoggedInAndOnMenu("CONTEST");
        System.out.println("********** LoginTest PASSED **********");
    }
}