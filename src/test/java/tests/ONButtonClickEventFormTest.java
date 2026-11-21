package tests;
import Page.BasePage;
import Page.CheckChangeEventFormPage;
import Page.FormsPage;
import Page.ONButtonClickEventFormPage;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.InvalidElementStateException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import base.BaseTest;
import driver.DriverManager;
import Utils.NavigationHelper;

public class ONButtonClickEventFormTest extends BaseTest {
    AndroidDriver driver;
    ONButtonClickEventFormPage onButtonClickEventFormPage;

    @BeforeClass
    public void navigateToForm() {
        NavigationHelper.ensureLoggedInAndOnMenu("CONTEST");
        driver = DriverManager.getDriver();
        FormsPage formsPage = new FormsPage(driver);
        System.out.println("Selecting ONBUTTONCLICKEVENT from forms grid");
        formsPage.selectForm("ONBUTTONCLICKEVENT");
        onButtonClickEventFormPage = new ONButtonClickEventFormPage(driver);
        System.out.println("Now on ONButtonClickEvent form, ready to run test cases");
    }

    @Test(priority = 1)
    public void verifyInActiveDisablesTextbox() {
        System.out.println("********** verifyInActiveDisablesTextbox STARTED **********");

        boolean beforeClick = onButtonClickEventFormPage.isTextboxTestingEnabled();
        Assert.assertTrue(beforeClick, "Textbox should be enabled before clicking InActive");

        onButtonClickEventFormPage.clickInActiveButton();
        boolean afterClick = onButtonClickEventFormPage.isTextboxTestingEnabled();
        Assert.assertFalse(afterClick, "Textbox should be disabled after clicking InActive");

        String valueBefore = onButtonClickEventFormPage.getTextboxValue();
        // Typing into a disabled field makes Appium throw InvalidElementStateException.
        // That exception IS the correct behaviour, so catching it means "pass".
        // Assert.fail throws AssertionError, which this catch does not swallow.
        try {
            onButtonClickEventFormPage.enterTextIntoTextbox("ShouldNotAppear");
            Assert.fail("Typing should be blocked on a disabled field");
        } catch (InvalidElementStateException e) {
            System.out.println("Typing blocked on disabled field, as expected");
        }
        String valueAfter = onButtonClickEventFormPage.getTextboxValue();
        System.out.println("Textbox value before/after: " + valueBefore + " / " + valueAfter);
        Assert.assertEquals(valueAfter, valueBefore, "Textbox value should NOT change while disabled");

        System.out.println("********** verifyInActiveDisablesTextbox PASSED **********");
    }

    @Test(priority = 2)
    public void verifyActiveButtonReEnablesTextbox() {
        System.out.println("********** verifyActiveButtonReEnablesTextbox STARTED **********");

        onButtonClickEventFormPage.clickActiveButton();
        Assert.assertTrue(onButtonClickEventFormPage.isTextboxTestingEnabled(),
            "Textbox should be re-enabled after clicking ActiveButton");

        onButtonClickEventFormPage.enterTextIntoTextbox("EditableAgain");
        String value = onButtonClickEventFormPage.getTextboxValue();
        Assert.assertTrue(value.contains("EditableAgain"), "Textbox should accept typed text once re-enabled");

        System.out.println("********** verifyActiveButtonReEnablesTextbox PASSED **********");
    }

    @Test(priority = 3)
    public void verifyOpenPageButtonBehavior() {
        System.out.println("********** verifyOpenPageButtonBehavior STARTED **********");

        FormsPage formsPage = new FormsPage(driver);

        Assert.assertTrue(onButtonClickEventFormPage.isFormLoaded(10),
            "Precondition failed: form is not displayed before clicking OpenPage");

        onButtonClickEventFormPage.clickOpenPageButton();

        // Only press back if the new screen is actually still open
        boolean pageOpened = onButtonClickEventFormPage.waitForFormToDisappear(5);
        if (pageOpened) {
            System.out.println("Clicking back button to exit the screen opened by OpenPage");
            onButtonClickEventFormPage.navigateBackFromForm(); // inherited from BasePage
        } else {
            System.out.println("OpenPage screen already closed itself, skipping back click");
        }

        // Re-select the form only if we are not already on it
        if (!onButtonClickEventFormPage.isFormLoaded(3)) {
            formsPage.selectForm("ONBUTTONCLICKEVENT");
            System.out.println("Re-selected ONBUTTONCLICKEVENT after OpenPage");
        }

        Assert.assertTrue(onButtonClickEventFormPage.isFormLoaded(10),
            "Form should be displayed again after OpenPage");

        System.out.println("********** verifyOpenPageButtonBehavior PASSED **********");
    }
    @Test(priority = 4)
    public void verifyOpenAndCloseDialogButton() {
        System.out.println("********** verifyOpenAndCloseDialogButton STARTED **********");

        onButtonClickEventFormPage.clickOpenAndCloseDialogButton();
        Assert.assertTrue(onButtonClickEventFormPage.isDialogOpen(10),
            "Dialog should open after clicking OpenAndCloseDailog");

        onButtonClickEventFormPage.closeDialogViaButton();
        Assert.assertTrue(onButtonClickEventFormPage.isDialogClosed(10),
            "Dialog should close after clicking OnbuttonClickCloseDailog");

        System.out.println("********** verifyOpenAndCloseDialogButton PASSED **********");
    }

    @Test(priority = 5)
    public void verifyHideButtonHidesFormulaTextbox() {
        System.out.println("********** verifyHideButtonHidesFormulaTextbox STARTED **********");

        Assert.assertTrue(onButtonClickEventFormPage.isFormulaTestingVisible(),
            "FormulaTesting textbox should be visible before clicking HIdebutton");

        onButtonClickEventFormPage.clickHideButton();
        onButtonClickEventFormPage.waitForFormulaTestingVisible(false);

        Assert.assertFalse(onButtonClickEventFormPage.isFormulaTestingVisible(),
            "FormulaTesting textbox should be hidden after clicking HIdebutton");

        System.out.println("********** verifyHideButtonHidesFormulaTextbox PASSED **********");
    }

    @Test(priority = 6)
    public void verifyShowButtonShowsFormulaTextbox() {
        System.out.println("********** verifyShowButtonShowsFormulaTextbox STARTED **********");

        // Test 5 left it hidden, so this precondition makes the Show check meaningful
        Assert.assertFalse(onButtonClickEventFormPage.isFormulaTestingVisible(),
            "FormulaTesting textbox should still be hidden before clicking ShowButton");

        onButtonClickEventFormPage.clickShowButton();
        onButtonClickEventFormPage.waitForFormulaTestingVisible(true);

        Assert.assertTrue(onButtonClickEventFormPage.isFormulaTestingVisible(),
            "FormulaTesting textbox should be visible after clicking ShowButton");

        System.out.println("********** verifyShowButtonShowsFormulaTextbox PASSED **********");
    }
    @Test(priority = 7)
    public void verifyFormulaAddsSixty() {
        System.out.println("********** verifyFormulaAddsSixty STARTED **********");

        String inputValue = "10";
        // Double.parseDouble converts the String "10" to the number 10.0,
        // so we can do arithmetic on it. Expected = input + 60 = 70.0
        double expected = Double.parseDouble(inputValue) + 60;
        System.out.println("Input: " + inputValue + " | Expected result: " + expected);

        onButtonClickEventFormPage.enterFormulaTestingValue(inputValue);
        onButtonClickEventFormPage.clickFormulaButton();

        // The app displays "70.0", so parse it to a double before comparing
        double actual = Double.parseDouble(onButtonClickEventFormPage.getFormulaResultValue());
        System.out.println("Actual result: " + actual);

        // The third argument is the allowed difference (delta). Doubles should never be
        // compared with exact equality because of floating-point rounding.
        Assert.assertEquals(actual, expected, 0.001, "Formulavalue should be input + 60");

        System.out.println("********** verifyFormulaAddsSixty PASSED **********");
    }
    @Test(priority = 8)
    public void verifyPostFormulaAddsSixty() {
        System.out.println("********** verifyPostFormulaAddsSixty STARTED **********");

        String inputValue = "30";
        // Double.parseDouble turns the String "30" into the number 30.0 so we can add 60
        double expected = Double.parseDouble(inputValue) + 60; // 90.0
        System.out.println("Input: " + inputValue + " | Expected result: " + expected);

        onButtonClickEventFormPage.enterFormulaTestingValue(inputValue);
        onButtonClickEventFormPage.clickPostFormulaButton();

        double actual = Double.parseDouble(onButtonClickEventFormPage.getFormulaResultValue());
        System.out.println("Actual result: " + actual);

        // Delta 0.001: doubles should never be compared with exact equality
        Assert.assertEquals(actual, expected, 0.001, "Postformula should give input + 60");

        System.out.println("********** verifyPostFormulaAddsSixty PASSED **********");
    }

    @Test(priority = 9)
    public void verifyGetDataShowsValue30() {
        System.out.println("********** verifyGetDataShowsValue30 STARTED **********");

        onButtonClickEventFormPage.clickGetDataButton();

        // Your working form showed GetData as "21" (no decimal), so accept "30" or "30.0"
        boolean shown = onButtonClickEventFormPage.isValueTextDisplayed("30", 5)
                     || onButtonClickEventFormPage.isValueTextDisplayed("30.0", 5);
        Assert.assertTrue(shown, "GetData should display value 30");
        System.out.println("********** verifyGetDataShowsValue30 PASSED **********");
    }
}