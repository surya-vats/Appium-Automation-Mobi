package tests;
import Page.FormsPage;
import Page.OnGridButtonClickFormPage;
import io.appium.java_client.android.AndroidDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import base.BaseTest;
import driver.DriverManager;
import Utils.NavigationHelper;

public class OnGridButtonClickFormTest extends BaseTest {
    AndroidDriver driver;
    OnGridButtonClickFormPage onGridButtonClickFormPage;
    FormsPage formsPage;

    @BeforeClass
    public void navigateToForm() {
        NavigationHelper.ensureLoggedInAndOnMenu("CONTEST");
        driver = DriverManager.getDriver();
        formsPage = new FormsPage(driver);
        System.out.println("Selecting ONGRIDBUTTONCLICK from forms grid");
        formsPage.selectForm("ONGRIDBUTTONCLICK");
        onGridButtonClickFormPage = new OnGridButtonClickFormPage(driver);
        System.out.println("Now on OnGridButtonClick form, ready to run test cases");
    }

    @Test(priority = 1)
    public void verifyInActiveIconDisablesTextbox() {
        System.out.println("********** verifyInActiveIconDisablesTextbox STARTED **********");

        Assert.assertTrue(onGridButtonClickFormPage.isTestingTextboxEnabled(),
            "TestingTextbox should be enabled before clicking InActive");

        onGridButtonClickFormPage.clickInActiveIcon();

        Assert.assertFalse(onGridButtonClickFormPage.isTestingTextboxEnabled(),
            "TestingTextbox should be disabled after clicking InActive");

        System.out.println("********** verifyInActiveIconDisablesTextbox PASSED **********");
    }

    @Test(priority = 2)
    public void verifyActiveIconReEnablesTextbox() {
        System.out.println("********** verifyActiveIconReEnablesTextbox STARTED **********");

        // Test 1 left the textbox disabled, so this precondition makes the re-enable check meaningful
        Assert.assertFalse(onGridButtonClickFormPage.isTestingTextboxEnabled(),
            "TestingTextbox should still be disabled before clicking Active");

        onGridButtonClickFormPage.clickActiveIcon();

        Assert.assertTrue(onGridButtonClickFormPage.isTestingTextboxEnabled(),
            "TestingTextbox should be enabled after clicking Active");

        String value = onGridButtonClickFormPage.typeIntoTestingTextbox("EditableAgain");
        Assert.assertTrue(value.contains("EditableAgain"),
            "TestingTextbox should accept typed text once re-enabled");

        System.out.println("********** verifyActiveIconReEnablesTextbox PASSED **********");
    }

    @Test(priority = 3)
    public void verifyOpenPageIconOpensNewPageAndCloses() {
        System.out.println("********** verifyOpenPageIconOpensNewPageAndCloses STARTED **********");

        // Test 2 left the screen scrolled down, so go back to the top before checking the form marker
        onGridButtonClickFormPage.scrollToTop();
        Assert.assertTrue(onGridButtonClickFormPage.isFormLoaded(10),
            "Precondition failed: form is not displayed before clicking OpenPage");

        onGridButtonClickFormPage.clickOpenPageIcon();

        Assert.assertTrue(onGridButtonClickFormPage.waitForFormToDisappear(10),
            "A new page should open after clicking OpenPage");

        System.out.println("Scrolling down on the new page and clicking its close icon");
        onGridButtonClickFormPage.closeNewPage();

        // Closing lands on the forms list, so re-select the form for the next tests
        if (!onGridButtonClickFormPage.isFormLoaded(3)) {
            formsPage.selectForm("ONGRIDBUTTONCLICK");
            System.out.println("Re-selected ONGRIDBUTTONCLICK after OpenPage");
        }

        Assert.assertTrue(onGridButtonClickFormPage.isFormLoaded(10),
            "Form should be displayed again after closing the new page");

        System.out.println("********** verifyOpenPageIconOpensNewPageAndCloses PASSED **********");
    }

    @Test(priority = 4)
    public void verifyOpenDialogIconOpensNewPageAndCloses() {
        System.out.println("********** verifyOpenDialogIconOpensNewPageAndCloses STARTED **********");

        Assert.assertTrue(onGridButtonClickFormPage.isFormLoaded(10),
            "Precondition failed: form is not displayed before clicking OpenDialog");

        onGridButtonClickFormPage.clickOpenDialogIcon();

        Assert.assertTrue(onGridButtonClickFormPage.waitForFormToDisappear(10),
            "A new page should open after clicking OpenDialog");

        System.out.println("Scrolling down on the new page and clicking its close icon");
        onGridButtonClickFormPage.closeNewPage();

        System.out.println("********** verifyOpenDialogIconOpensNewPageAndCloses PASSED **********");
    }
    @Test(priority = 5)
    public void verifyHideIconHidesTextbox() {
        System.out.println("********** verifyHideIconHidesTextbox STARTED **********");

        Assert.assertTrue(onGridButtonClickFormPage.isTestingTextboxVisible(),
            "TestingTextbox should be visible before clicking Hide");

        onGridButtonClickFormPage.clickHideIcon();

        Assert.assertTrue(onGridButtonClickFormPage.waitForTestingTextboxVisibility(false),
            "TestingTextbox should be hidden after clicking Hide");

        System.out.println("********** verifyHideIconHidesTextbox PASSED **********");
    }

    @Test(priority = 6)
    public void verifyShowIconShowsTextbox() {
        System.out.println("********** verifyShowIconShowsTextbox STARTED **********");

        // Test 5 left it hidden, so this precondition makes the Show check meaningful
        Assert.assertFalse(onGridButtonClickFormPage.isTestingTextboxVisible(),
            "TestingTextbox should still be hidden before clicking Show");

        onGridButtonClickFormPage.clickShowIcon();

        Assert.assertTrue(onGridButtonClickFormPage.waitForTestingTextboxVisibility(true),
            "TestingTextbox should be visible after clicking Show");

        System.out.println("********** verifyShowIconShowsTextbox PASSED **********");
    }

    @Test(priority = 7)
    public void verifyGetDataIconShowsThousand() {
        System.out.println("********** verifyGetDataIconShowsThousand STARTED **********");

        onGridButtonClickFormPage.clickGetDataIcon();

        String value = onGridButtonClickFormPage.getGetDataValueText(20);
        Assert.assertTrue(value.matches("1000([.]0)?"),
            "Getdatalabel should show 1000 after clicking Getdata, but showed: '" + value + "'");

        System.out.println("********** verifyGetDataIconShowsThousand PASSED **********");
    }
    @Test(priority = 8)
    public void verifyFormulaIconAddsFive() {
        System.out.println("********** verifyFormulaIconAddsFive STARTED **********");

        onGridButtonClickFormPage.enterFormulaInput("50");
        onGridButtonClickFormPage.clickFormulaIcon();

        Assert.assertTrue(onGridButtonClickFormPage.waitForFormulaValue("55", 20),
            "Formulalabel should show 55 after entering 50 and clicking Formula");

        System.out.println("********** verifyFormulaIconAddsFive PASSED **********");
    }

    @Test(priority = 9)
    public void verifyPostFormulaIconAddsFive() {
        System.out.println("********** verifyPostFormulaIconAddsFive STARTED **********");

        // Different input from test 8 on purpose: Formulalabel still shows 55 from the last test,
        // so using 50 again could pass without Postformula doing anything.
        onGridButtonClickFormPage.enterFormulaInput("20");
        onGridButtonClickFormPage.clickPostFormulaIcon();

        Assert.assertTrue(onGridButtonClickFormPage.waitForFormulaValue("25", 20),
            "Formulalabel should show 25 after entering 20 and clicking Postformula");

        System.out.println("********** verifyPostFormulaIconAddsFive PASSED **********");
    }
}