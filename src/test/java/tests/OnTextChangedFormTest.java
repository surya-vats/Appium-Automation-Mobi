package tests;

import Page.OnTextChangedFormPage;
import io.appium.java_client.android.AndroidDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import base.BaseTest;
import driver.DriverManager;
import Utils.NavigationHelper;

public class OnTextChangedFormTest extends BaseTest {
    AndroidDriver driver;
    OnTextChangedFormPage textPage;
    @BeforeClass
    public void navigateToForm() {
        NavigationHelper.ensureLoggedInAndOnMenu("CONTEST");
        driver = DriverManager.getDriver();
        textPage = new OnTextChangedFormPage(driver);
        textPage.openForm();
        Assert.assertTrue(textPage.isFormLoaded(10), "Form should be loaded before capturing the button baseline");
        textPage.captureButtonBaseline();
        System.out.println("Now on OnTextChangedForm, ready to run test cases");
    }
    //@Test(priority = 1)
    public void verifyInActiveTextChangeDisablesButton() {
        System.out.println("********** verifyInActiveTextChangeDisablesButton STARTED **********");

        Assert.assertFalse(textPage.isButtonTestingInactive(), "ButtonTesting should look active initially");
        textPage.logButtonTestingAttributes("before InActive text change");
        System.out.println("Step 1 PASSED: ButtonTesting active initially");

        textPage.primeFocusHistory();

        // 'surya' ki jagah 'inactive' + Enter ke baad keyboard band, phir focus move
        textPage.typeIntoInActiveTextbox("inactive");
        textPage.pressEnter();
        textPage.hideKeyboardIfOpen();
        textPage.clickActiveTextbox();

        boolean becameInactive = textPage.waitForButtonTestingInactive(true, 10);
        // Assert se PEHLE log, warna fail hone par ye line chalti hi nahi
        textPage.logButtonTestingAttributes("after InActive text change");
        Assert.assertTrue(becameInactive, "ButtonTesting should become inactive after InActive text change");
        System.out.println("Step 2 PASSED: ButtonTesting inactive after InActive text change");

        System.out.println("********** verifyInActiveTextChangeDisablesButton PASSED **********");
    }

   // @Test(priority = 2)
    public void verifyActiveTextChangeEnablesButton() {
        System.out.println("********** verifyActiveTextChangeEnablesButton STARTED **********");

        // Test 1 pe depend nahi: fresh form + khud ka precondition
        textPage.resetFormAndCaptureBaseline();
        Assert.assertFalse(textPage.isButtonTestingInactive(), "ButtonTesting should be active on a fresh form");

        textPage.disableButtonViaInActiveTextbox();
        Assert.assertTrue(textPage.waitForButtonTestingInactive(true, 10),
            "Precondition failed: could not make ButtonTesting inactive");
        System.out.println("Step 1 PASSED: ButtonTesting inactive before Active text change");

        textPage.typeIntoActiveTextbox("hello");
        textPage.pressEnter();
        textPage.hideKeyboardIfOpen();
        textPage.clickInActiveTextbox();

        boolean becameActive = textPage.waitForButtonTestingInactive(false, 10);
        textPage.logButtonTestingAttributes("after Active text change");
        Assert.assertTrue(becameActive, "ButtonTesting should become active again after Active text change");
        System.out.println("Step 2 PASSED: ButtonTesting active after Active text change");

        System.out.println("********** verifyActiveTextChangeEnablesButton PASSED **********");
    }
    @Test(priority = 3)
    public void verifyOpenPageTextChangeOpensPage() {
        System.out.println("********** verifyOpenPageTextChangeOpensPage STARTED **********");

        Assert.assertTrue(textPage.isFormLoaded(10), "Precondition failed: form is not displayed before OpenPage text change");

        textPage.typeIntoOpenPageTextbox("page");
        textPage.pressEnter();

        // Enter may already open the page. If the form is still showing, move focus away like in tests 1 and 2.
        if (!textPage.waitForFormToDisappear(5)) {
            System.out.println("Form still showing after Enter, tapping Active textbox to move focus");
            textPage.clickActiveTextbox();
        }
        Assert.assertTrue(textPage.waitForFormToDisappear(10), "New page should open after OpenPage text change");
        System.out.println("Step 1 PASSED: New page opened");

        // Back lands on the form list, so re-select the form from the grid
        System.out.println("Clicking back to exit the page opened by OpenPage");
        textPage.navigateBackFromForm(); // inherited from BasePage
        textPage.reopenFormIfNeeded();
        Assert.assertTrue(textPage.isFormLoaded(10), "Form should be displayed again after OpenPage and back");
        System.out.println("Step 2 PASSED: Back on the form after re-selecting it");

        System.out.println("********** verifyOpenPageTextChangeOpensPage PASSED **********");
    }
    @Test(priority = 4)
    public void verifyOpenDialogTextChangeOpensDialog() {
        System.out.println("********** verifyOpenDialogTextChangeOpensDialog STARTED **********");

        Assert.assertTrue(textPage.isFormLoaded(10), "Precondition failed: form is not displayed before OpenDialog text change");

        textPage.typeIntoOpenDialogTextbox("dialog");
        textPage.pressEnter();

        // Enter may already open the dialog. If not, move focus away like in tests 1, 2 and 3.
        if (!textPage.waitForDialogToOpen(5)) {
            System.out.println("Dialog not showing after Enter, tapping Active textbox to move focus");
            textPage.clickActiveTextbox();
        }
        Assert.assertTrue(textPage.waitForDialogToOpen(10), "Dialog should open after OpenDialog text change");
        System.out.println("Step 1 PASSED: Dialog opened after text change");

        // Clicking the checkbox inside the dialog closes it and returns to the main form
        textPage.closeDialogViaCheckbox();
        Assert.assertTrue(textPage.waitForDialogToClose(10), "Dialog should close after clicking its checkbox");
        Assert.assertTrue(textPage.isFormLoaded(10), "Should be back on the form after closing the dialog");
        System.out.println("Step 2 PASSED: Dialog closed via checkbox, back on the form");

        System.out.println("********** verifyOpenDialogTextChangeOpensDialog PASSED **********");
    }
    @Test(priority = 5)
    public void verifyHideTextChangeHidesButton() {
        System.out.println("********** verifyHideTextChangeHidesButton STARTED **********");

        Assert.assertTrue(textPage.isButtonTestingVisible(), "ButtonTesting should be visible initially");
        System.out.println("Step 1 PASSED: ButtonTesting visible initially");

        // Type, press Enter, then tap another textbox to move focus (this tap fires the event and closes the keyboard).
        textPage.typeIntoHideTextbox("hidden");
        textPage.pressEnter();
        textPage.clickActiveTextbox();

        textPage.waitForButtonTestingVisible(false);
        Assert.assertFalse(textPage.isButtonTestingVisible(), "ButtonTesting should be hidden after Hide text change");
        System.out.println("Step 2 PASSED: ButtonTesting hidden after Hide text change");

        System.out.println("********** verifyHideTextChangeHidesButton PASSED **********");
    }

    @Test(priority = 6)
    public void verifyShowTextChangeShowsButton() {
        System.out.println("********** verifyShowTextChangeShowsButton STARTED **********");

        // Test 5 left the button hidden, so this precondition makes the Show check meaningful.
        Assert.assertFalse(textPage.isButtonTestingVisible(), "ButtonTesting should still be hidden before Show text change");
        System.out.println("Step 1 PASSED: ButtonTesting hidden before Show text change");

        textPage.typeIntoShowTextbox("visible");
        textPage.pressEnter();
        textPage.clickActiveTextbox();

        textPage.waitForButtonTestingVisible(true);
        Assert.assertTrue(textPage.isButtonTestingVisible(), "ButtonTesting should be visible again after Show text change");
        System.out.println("Step 2 PASSED: ButtonTesting visible after Show text change");

        System.out.println("********** verifyShowTextChangeShowsButton PASSED **********");
    }
    @Test(priority = 7)
    public void verifyFormulaTextChangeShowsValue() {
        System.out.println("********** verifyFormulaTextChangeShowsValue STARTED **********");

        Assert.assertTrue(textPage.isLabelformulaLabelDisplayed(10), "Labelformula label should be on the form");
        Assert.assertFalse(textPage.isLabelformulaValueDisplayed(2), "Labelformula should not show 50.0 before Formula text change");
        System.out.println("Step 1 PASSED: Labelformula value not shown before Formula text change");

        // Type, press Enter, then tap another textbox to move focus (this tap fires the event).
        textPage.typeIntoFormulaTextbox("formula");
        textPage.pressEnter();
        textPage.clickActiveTextbox();

        Assert.assertTrue(textPage.isLabelformulaValueDisplayed(10), "Labelformula should show 50.0 after Formula text change");
        System.out.println("Step 2 PASSED: Labelformula shows 50.0 after Formula text change");

        System.out.println("********** verifyFormulaTextChangeShowsValue PASSED **********");
    }

    @Test(priority = 8)
    public void verifyPostformulaTextChangeShowsValue() {
        System.out.println("********** verifyPostformulaTextChangeShowsValue STARTED **********");

        // Test 7 left Labelformula at 50.0 (same label is shared), so reset the form first,
        // otherwise this test would pass without Postformula doing anything.
        textPage.reopenFormFresh();
        Assert.assertTrue(textPage.isFormLoaded(10), "Form should be displayed again after re-opening");
        Assert.assertFalse(textPage.isLabelformulaValueDisplayed(2), "Labelformula should not show 50.0 before Postformula text change");
        System.out.println("Step 1 PASSED: Form reset, Labelformula value not shown");

        textPage.typeIntoPostformulaTextbox("postformula");
        textPage.pressEnter();
        textPage.clickActiveTextbox();

        Assert.assertTrue(textPage.isLabelformulaValueDisplayed(10), "Labelformula should show 50.0 after Postformula text change");
        System.out.println("Step 2 PASSED: Labelformula shows 50.0 after Postformula text change");

        System.out.println("********** verifyPostformulaTextChangeShowsValue PASSED **********");
    }

    @Test(priority = 9)
    public void verifyGetdataTextChangeShowsValue() {
        System.out.println("********** verifyGetdataTextChangeShowsValue STARTED **********");

        // GetdataValue uses "50" and Labelformula uses "50.0". Exact-text locators never match each other.
        Assert.assertFalse(textPage.isGetdataValueDisplayed(2), "GetdataValue should not show 50 before Getdata text change");
        System.out.println("Step 1 PASSED: GetdataValue not shown before Getdata text change");

        textPage.typeIntoGetdataTextbox("getdata");
        textPage.pressEnter();
        textPage.clickActiveTextbox();

        Assert.assertTrue(textPage.isGetdataValueDisplayed(50), "GetdataValue should show 50 after Getdata text change");
        System.out.println("Step 2 PASSED: GetdataValue shows 50 after Getdata text change");

        System.out.println("********** verifyGetdataTextChangeShowsValue PASSED **********");
    }
}