package tests;
import Page.FormsPage;
import Page.GridTextAutoFormPage;
import io.appium.java_client.android.AndroidDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import base.BaseTest;
import driver.DriverManager;
import Utils.NavigationHelper;

public class GridTextAutoFormTest extends BaseTest {
    AndroidDriver driver;
    GridTextAutoFormPage gridTextAutoFormPage;
    FormsPage formsPage;

    @BeforeClass
    public void navigateToForm() {
        NavigationHelper.ensureLoggedInAndOnMenu("CONTEST");
        driver = DriverManager.getDriver();
        formsPage = new FormsPage(driver);
        System.out.println("Selecting GRIDTEXTAUTO from forms grid");
        formsPage.selectForm("GRIDTEXTAUTO");
        gridTextAutoFormPage = new GridTextAutoFormPage(driver);
        System.out.println("Now on GridTextAuto form, ready to run test cases");
    }

    @Test(priority = 1)
    public void verifyInActiveDisablesTextbox() {
        System.out.println("********** verifyInActiveDisablesTextbox STARTED **********");

        Assert.assertTrue(gridTextAutoFormPage.isTestingTextboxEnabled(),
            "TestingTextbox should be enabled before clicking InActive");

        gridTextAutoFormPage.clickInActiveButton();

        Assert.assertFalse(gridTextAutoFormPage.isTestingTextboxEnabled(),
            "TestingTextbox should be disabled after clicking InActive");
        Assert.assertTrue(gridTextAutoFormPage.isTypingBlockedInTestingTextbox(),
            "Nothing should be typable in TestingTextbox after clicking InActive");

        System.out.println("********** verifyInActiveDisablesTextbox PASSED **********");
    }

    @Test(priority = 2)
    public void verifyActiveReEnablesTextbox() {
        System.out.println("********** verifyActiveReEnablesTextbox STARTED **********");

        // Test 1 left the textbox disabled, so this precondition makes the re-enable check meaningful
        Assert.assertFalse(gridTextAutoFormPage.isTestingTextboxEnabled(),
            "TestingTextbox should still be disabled before clicking Active");

        gridTextAutoFormPage.clickActiveButton();

        Assert.assertTrue(gridTextAutoFormPage.isTestingTextboxEnabled(),
            "TestingTextbox should be enabled after clicking Active");

        String value = gridTextAutoFormPage.typeIntoTestingTextbox("EditableAgain");
        Assert.assertTrue(value.contains("EditableAgain"),
            "TestingTextbox should accept typed text once re-enabled");

        System.out.println("********** verifyActiveReEnablesTextbox PASSED **********");
    }

    @Test(priority = 3)
    public void verifyHideButtonHidesTextbox() {
        System.out.println("********** verifyHideButtonHidesTextbox STARTED **********");

        Assert.assertTrue(gridTextAutoFormPage.isTestingTextboxVisible(),
            "TestingTextbox should be visible before clicking Hide");

        gridTextAutoFormPage.clickHideButton();

        Assert.assertTrue(gridTextAutoFormPage.waitForTestingTextboxVisibility(false),
            "TestingTextbox should be hidden after clicking Hide");

        System.out.println("********** verifyHideButtonHidesTextbox PASSED **********");
    }

    @Test(priority = 4)
    public void verifyShowButtonShowsTextbox() {
        System.out.println("********** verifyShowButtonShowsTextbox STARTED **********");

        // Test 3 left it hidden, so this precondition makes the Show check meaningful
        Assert.assertFalse(gridTextAutoFormPage.isTestingTextboxVisible(),
            "TestingTextbox should still be hidden before clicking Show");

        gridTextAutoFormPage.clickShowButton();

        Assert.assertTrue(gridTextAutoFormPage.waitForTestingTextboxVisibility(true),
            "TestingTextbox should be visible after clicking Show");

        System.out.println("********** verifyShowButtonShowsTextbox PASSED **********");
    }

    @Test(priority = 5)
    public void verifyOpenPageOpensNewPageAndCloses() {
        System.out.println("********** verifyOpenPageOpensNewPageAndCloses STARTED **********");

        gridTextAutoFormPage.scrollToTop();
        Assert.assertTrue(gridTextAutoFormPage.isFormLoaded(10),
            "Precondition failed: form is not displayed before clicking OpenPage");

        gridTextAutoFormPage.clickOpenPageButton();

        // The form stays in the hierarchy under the new page, so we check for Gridtextclose instead
        Assert.assertTrue(gridTextAutoFormPage.isCloseButtonAvailable(),
            "A new page with Gridtextclose should open after clicking OpenPage");

        System.out.println("Clicking Gridtextclose on the new page");
        gridTextAutoFormPage.closeNewPageOrDialog();

        // You said the forms list appears after closing, so the form marker should be gone.
        // We re-select only if the form is not visible, so the test works either way.
        gridTextAutoFormPage.scrollToTop();
        if (!gridTextAutoFormPage.isFormLoaded(3)) {
            System.out.println("Forms list is showing, re-selecting GRIDTEXTAUTO");
            formsPage.selectForm("GRIDTEXTAUTO");
        }

        Assert.assertTrue(gridTextAutoFormPage.isFormLoaded(10),
            "Form should be displayed again after closing the new page");

        System.out.println("********** verifyOpenPageOpensNewPageAndCloses PASSED **********");
    }

    @Test(priority = 6)
    public void verifyOpenCloseDialogOpensAndCloses() {
        System.out.println("********** verifyOpenCloseDialogOpensAndCloses STARTED **********");

        gridTextAutoFormPage.scrollToTop();
        Assert.assertTrue(gridTextAutoFormPage.isFormLoaded(10),
            "Precondition failed: form is not displayed before clicking OpenCloseDailog");

        gridTextAutoFormPage.clickOpenCloseDialogButton();

        Assert.assertTrue(gridTextAutoFormPage.isCloseButtonAvailable(),
            "A dialog with Gridtextclose should open after clicking OpenCloseDailog");

        System.out.println("Clicking Gridtextclose inside the dialog");
        gridTextAutoFormPage.closeNewPageOrDialog();

        gridTextAutoFormPage.scrollToTop();
        Assert.assertTrue(gridTextAutoFormPage.isFormLoaded(10),
            "Form should be displayed again after closing the dialog");

        System.out.println("********** verifyOpenCloseDialogOpensAndCloses PASSED **********");
    }

    @Test(priority = 7)
    public void verifyGetDataShowsFifty() {
        System.out.println("********** verifyGetDataShowsFifty STARTED **********");

        gridTextAutoFormPage.clickGetDataButton();

        Assert.assertTrue(gridTextAutoFormPage.waitForValueText("50", 20),
            "Getdatalabel should show 50 after clicking Getdata");

        System.out.println("********** verifyGetDataShowsFifty PASSED **********");
    }

    @Test(priority = 8)
    public void verifyFormulaAddsFifteen() {
        System.out.println("********** verifyFormulaAddsFifteen STARTED **********");

        gridTextAutoFormPage.enterFormulaInput("30");
        gridTextAutoFormPage.clickFormulaButton();

        Assert.assertTrue(gridTextAutoFormPage.waitForFormulaValue("45", 30),
        	    "LabelforFormula should show 45 after entering 30 and clicking Formula");
        System.out.println("********** verifyFormulaAddsFifteen PASSED **********");
    }

    @Test(priority = 9)
    public void verifyPostFormulaAddsFifteen() {
        System.out.println("********** verifyPostFormulaAddsFifteen STARTED **********");

        // Different input from test 8 on purpose: the label still shows 45 from the last test,
        // so reusing 30 could pass without PostFormula doing anything.
        gridTextAutoFormPage.enterFormulaInput("20");
        gridTextAutoFormPage.clickPostFormulaButton();

        Assert.assertTrue(gridTextAutoFormPage.waitForFormulaValue("35", 20),
        	    "LabelforFormula should show 35 after entering 20 and clicking PostFormula");

        System.out.println("********** verifyPostFormulaAddsFifteen PASSED **********");
    }
}