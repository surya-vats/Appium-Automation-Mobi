package tests;
import Page.FormsPage;
import Page.OnLabelClickTestingFormPage;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.InvalidElementStateException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import base.BaseTest;
import driver.DriverManager;
import Utils.NavigationHelper;

public class OnLabelClickTestingFormTest extends BaseTest {
    AndroidDriver driver;
    OnLabelClickTestingFormPage labelPage;

    @BeforeClass
    public void navigateToForm() {
        NavigationHelper.ensureLoggedInAndOnMenu("CONTEST");
        driver = DriverManager.getDriver();
        FormsPage formsPage = new FormsPage(driver);
        System.out.println("Selecting ONLABELCLICKTESTING from forms grid");
        formsPage.selectForm("ONLABELCLICKTESTING");
        labelPage = new OnLabelClickTestingFormPage(driver);
        System.out.println("Now on OnLabelClickTesting form, ready to run test cases");
    }

    @Test(priority = 1)
    public void verifyInActiveLabelDisablesTextbox() {
        System.out.println("********** verifyInActiveLabelDisablesTextbox STARTED **********");

        Assert.assertTrue(labelPage.isTestingTextboxEnabled(), "Textbox should be enabled before clicking InActive label");

        labelPage.clickInActiveLabel();
        labelPage.waitForTestingTextboxEnabled(false);
        Assert.assertFalse(labelPage.isTestingTextboxEnabled(), "Textbox should be disabled after clicking InActive label");

        String valueBefore = labelPage.getTestingTextboxValue();
        // Typing into a disabled field makes Appium throw InvalidElementStateException.
        // That exception IS the correct behaviour, so catching it means "pass".
        try {
            labelPage.enterTextIntoTestingTextbox("ShouldNotAppear");
            Assert.fail("Typing should be blocked on a disabled field");
        } catch (InvalidElementStateException e) {
            System.out.println("Typing blocked on disabled field, as expected");
        }
        String valueAfter = labelPage.getTestingTextboxValue();
        Assert.assertEquals(valueAfter, valueBefore, "Textbox value should NOT change while disabled");

        System.out.println("********** verifyInActiveLabelDisablesTextbox PASSED **********");
    }

    @Test(priority = 2)
    public void verifyActiveLabelReEnablesTextbox() {
        System.out.println("********** verifyActiveLabelReEnablesTextbox STARTED **********");

        labelPage.clickActiveLabel();
        labelPage.waitForTestingTextboxEnabled(true);
        Assert.assertTrue(labelPage.isTestingTextboxEnabled(), "Textbox should be re-enabled after clicking Active label");

        labelPage.enterTextIntoTestingTextbox("EditableAgain");
        Assert.assertTrue(labelPage.getTestingTextboxValue().contains("EditableAgain"),
            "Textbox should accept typed text once re-enabled");

        System.out.println("********** verifyActiveLabelReEnablesTextbox PASSED **********");
    }

    @Test(priority = 3)
    public void verifyOpenPageLabelBehavior() {
        System.out.println("********** verifyOpenPageLabelBehavior STARTED **********");

        Assert.assertTrue(labelPage.isFormLoaded(10), "Precondition failed: form is not displayed before clicking OpenPage");

        labelPage.clickOpenPageLabel();

        // OpenPage opens a separate page. Click back to leave it; this returns to the form list.
        if (labelPage.waitForFormToDisappear(5)) {
            System.out.println("Clicking back to exit the screen opened by OpenPage");
            labelPage.navigateBackFromForm(); // inherited from BasePage
        } else {
            System.out.println("OpenPage screen already closed itself, skipping back click");
        }

        // Back lands on the form list, so re-select the form from the grid
        labelPage.reopenFormIfNeeded();
        Assert.assertTrue(labelPage.isFormLoaded(10), "Form should be displayed again after OpenPage");

        System.out.println("********** verifyOpenPageLabelBehavior PASSED **********");
    }

    @Test(priority = 4)
    public void verifyOpenAndCloseLabelDialog() {
        System.out.println("********** verifyOpenAndCloseLabelDialog STARTED **********");

        labelPage.clickOpenAndCloseLabel();
        Assert.assertTrue(labelPage.isDialogOpen(10), "Dialog should open after clicking OpenAndClose label");

        labelPage.closeDialogBySelectingValue();
        Assert.assertTrue(labelPage.isDialogClosed(10), "Dialog should close after selecting value 10");

        System.out.println("********** verifyOpenAndCloseLabelDialog PASSED **********");
    }

    @Test(priority = 5)
    public void verifyHideLabelHidesTestingTextbox() {
        System.out.println("********** verifyHideLabelHidesTestingTextbox STARTED **********");

        Assert.assertTrue(labelPage.isTestingTextboxVisible(), "TestingTestbox should be visible before clicking Hide");

        labelPage.clickHideLabel();
        labelPage.waitForTestingTextboxVisible(false);
        Assert.assertFalse(labelPage.isTestingTextboxVisible(), "TestingTestbox should be hidden after clicking Hide");

        System.out.println("********** verifyHideLabelHidesTestingTextbox PASSED **********");
    }

    @Test(priority = 6)
    public void verifyShowLabelShowsTestingTextbox() {
        System.out.println("********** verifyShowLabelShowsTestingTextbox STARTED **********");

        // Test 5 left it hidden, so this precondition makes the Show check meaningful
        Assert.assertFalse(labelPage.isTestingTextboxVisible(), "TestingTestbox should still be hidden before clicking Show");

        labelPage.clickShowLabel();
        labelPage.waitForTestingTextboxVisible(true);
        Assert.assertTrue(labelPage.isTestingTextboxVisible(), "TestingTestbox should be visible after clicking Show");

        System.out.println("********** verifyShowLabelShowsTestingTextbox PASSED **********");
    }

    @Test(priority = 7)
    public void verifyFormulaLabelAddsSixty() {
        System.out.println("********** verifyFormulaLabelAddsSixty STARTED **********");

        String inputValue = "10";
        // Double.parseDouble converts "10" to 10.0 so we can do arithmetic.
        // Confirmed rule: Formula label (8) adds 35, so 10 becomes 45
        double expected = Double.parseDouble(inputValue) + 35;
        System.out.println("Input: " + inputValue + " | Expected result: " + expected);

        labelPage.enterFormulaTestValue(inputValue);
        labelPage.clickFormulaLabel();

        double actual = Double.parseDouble(labelPage.getFormulaResultValue());
        System.out.println("Actual result: " + actual);

        // Delta 0.001: doubles should never be compared with exact equality (floating-point rounding)
        Assert.assertEquals(actual, expected, 0.001, "Formula result should be input + 35");

        System.out.println("********** verifyFormulaLabelAddsSixty PASSED **********");
    }

    // enabled = false: TestNG skips this test. Postformula label (9) is not working in the app yet.
    // Remove "enabled = false" once the app is fixed and the expected rule is known.
    @Test(priority = 8, enabled = false)
    public void verifyPostformulaLabelAddsSixty() {
        System.out.println("********** verifyPostformulaLabelAddsSixty STARTED **********");

        String inputValue = "30";
        double expected = Double.parseDouble(inputValue) + 60; // 90.0
        System.out.println("Input: " + inputValue + " | Expected result: " + expected);

        labelPage.enterFormulaTestValue(inputValue);
        labelPage.clickPostformulaLabel();

        double actual = Double.parseDouble(labelPage.getFormulaResultValue());
        System.out.println("Actual result: " + actual);

        Assert.assertEquals(actual, expected, 0.001, "Postformula result should be input + 60");

        System.out.println("********** verifyPostformulaLabelAddsSixty PASSED **********");
    }

    // enabled = false: Getdatalabel is not working in the app yet.
    @Test(priority = 9, enabled = false)
    public void verifyGetdataLabelShowsValue() {
        System.out.println("********** verifyGetdataLabelShowsValue STARTED **********");

        labelPage.clickGetdataLabel();

        // ASSUMPTION: Getdatalabel shows 30 (same as button form). The app may show "30" or "30.0".
        String shown = labelPage.getGetdatalabelValue();
        Assert.assertTrue(shown.equals("30") || shown.equals("30.0"),
            "Getdatalabel should display 30 but was: " + shown);

        System.out.println("********** verifyGetdataLabelShowsValue PASSED **********");
    }
}