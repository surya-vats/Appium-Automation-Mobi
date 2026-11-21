
package tests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import Page.BasePage;
import Page.CheckChangeEventFormPage;
import Page.FormsPage;
import Utils.NavigationHelper;
import base.BaseTest;
import driver.DriverManager;


public class CheckChangeEventFormTest extends BaseTest {
	@BeforeClass
	public void navigateToForm() {
        NavigationHelper.ensureLoggedInAndOnMenu("CONTEST");
        FormsPage formsPage = new FormsPage(DriverManager.getDriver());
        formsPage.selectForm("CHECKCHANGEEVENTFORM");
        System.out.println("Navigated to CheckChangeEventForm");
    }

    @Test(priority = 2)
    public void verifyActiveInactiveTogglesTextbox() {
        System.out.println("********** CheckChangeEventFormTest STARTED **********");

        CheckChangeEventFormPage formPage = new CheckChangeEventFormPage(DriverManager.getDriver());

        // Step 1: Initial state
        Assert.assertTrue(formPage.isTextboxEnabled(), "Textbox should be enabled initially");
        formPage.enterTextInTextbox("QA Test Remark");
        System.out.println("Step 1 PASSED: Textbox editable initially");

        // Step 2: Check the box -> textbox should become disabled
        formPage.toggleActiveInactive();
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}
        boolean checkedLogOnly = formPage.isCheckboxChecked(); // just for log/debug, not asserted
        System.out.println("Checkbox state after click (info only): " + checkedLogOnly);
        Assert.assertFalse(formPage.isTextboxEnabled(), "Textbox should be disabled after checking the box");
        System.out.println("Step 2 PASSED: Textbox disabled after checking");

        // Step 3: Uncheck -> textbox should re-enable
        formPage.toggleActiveInactive();
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}
        Assert.assertTrue(formPage.isTextboxEnabled(), "Textbox should be enabled again after unchecking");
        formPage.enterTextInTextbox("Editable again");
        System.out.println("Step 3 PASSED: Textbox editable again after unchecking");

        System.out.println("********** CheckChangeEventFormTest PASSED **********");
    }
  
    @Test(priority = 3)
    public void verifyOpenPageCheckboxBehavior() {
        System.out.println("********** verifyOpenPageCheckboxBehavior STARTED **********");

        CheckChangeEventFormPage formPage = new CheckChangeEventFormPage(DriverManager.getDriver());
        BasePage basePage = new BasePage(DriverManager.getDriver());

        formPage.checkOpenPage();

        // OpenPage ke baad ab screen khud-ba-khud forms list pe wapas nahi jaata —
        // pehle back button click karna padta hai, tabhi forms grid pe pahunchte hain
        System.out.println("Clicking back button to exit the screen opened by OpenPage");
        basePage.navigateBackFromForm();

        // Ab forms list pe hain, CONTEST menu already open hai — sirf form dobara select karna hai
        FormsPage formsPage = new FormsPage(DriverManager.getDriver());
        formsPage.selectForm("CHECKCHANGEEVENTFORM");
        System.out.println("Re-selected CHECKCHANGEEVENTFORM from forms list after OpenPage + back button");

        System.out.println("********** verifyOpenPageCheckboxBehavior PASSED **********");
        System.out.println("NOTE: OpenPage checkbox opens a new screen that requires an explicit back "
            + "button click to return to the forms list ");
    }
    @Test(priority = 4)
    public void verifyOpenAndCloseDialogCheckbox() {
        System.out.println("********** verifyOpenAndCloseDialogCheckbox STARTED **********");

        CheckChangeEventFormPage formPage = new CheckChangeEventFormPage(DriverManager.getDriver());

        // Step 1: check the box, dialog should appear
        formPage.checkOpenAndCloseDialog();
        boolean dialogOpened = formPage.isDialogOpen(10);
        Assert.assertTrue(dialogOpened, "Dialog should open after checking OpenAndCloseDailog checkbox");
        System.out.println("Step 1 PASSED: Dialog opened");

        // Step 2: close the dialog by checking the Checkchangeclosedailog checkbox
        formPage.closeDialogViaCheckbox();
        boolean backOnMainForm = formPage.isBackOnMainFormAfterDialogClose(10);
        Assert.assertTrue(backOnMainForm, "Should return to CheckChangeEventForm after checking Checkchangeclosedailog");
        System.out.println("Step 2 PASSED: Dialog closed via checkbox, back on main form");

        System.out.println("********** verifyOpenAndCloseDialogCheckbox PASSED **********");
    }
    @Test(priority = 5)
    public void verifyFormulaAddsHundred() {
        System.out.println("********** verifyFormulaAddsHundred STARTED **********");

        CheckChangeEventFormPage formPage = new CheckChangeEventFormPage(DriverManager.getDriver());

        String inputValue = "10";
        double expectedResult = Double.parseDouble(inputValue) + 100; // 110.0
        String expectedResultText = String.valueOf(expectedResult);   // "110.0" — Java double.toString() 
                                                                         // ka default format, jo app ke
                                                                         // display format se match karta hai

        formPage.enterValueForFormula(inputValue);
        System.out.println("Step 1: Entered input value " + inputValue);

        formPage.checkFormula();

        boolean resultShown = formPage.isCalculatedValueDisplayed(expectedResultText, 10);
        Assert.assertTrue(resultShown, "Value field should show " + expectedResultText + " after checking Formula");
        System.out.println("Step 2 PASSED: Value field correctly shows " + expectedResultText);

        System.out.println("********** verifyFormulaAddsHundred PASSED **********");
    }
    
    @Test(priority = 6)
    public void verifyPostFormulaAddsHundred() {
        System.out.println("********** verifyPostFormulaAddsHundred STARTED **********");

        CheckChangeEventFormPage formPage = new CheckChangeEventFormPage(DriverManager.getDriver());

        String inputValue = "20";
        double expectedResult = Double.parseDouble(inputValue) + 100; // 120.0
        String expectedResultText = String.valueOf(expectedResult);   // "120.0"

        formPage.enterValueForFormula(inputValue); // same GetDataValue EditText reused
        System.out.println("Step 1: Entered input value " + inputValue);

        formPage.checkPostFormula();

        boolean resultShown = formPage.isCalculatedValueDisplayed(expectedResultText, 10);
        Assert.assertTrue(resultShown, "Value field should show " + expectedResultText + " after checking PostFormula");
        System.out.println("Step 2 PASSED: Value field correctly shows " + expectedResultText);

        System.out.println("********** verifyPostFormulaAddsHundred PASSED **********");
    }
    @Test(priority = 7)
    public void verifyHideShowTogglesTextboxVisibility() {
        System.out.println("********** verifyHideShowTogglesTextboxVisibility STARTED **********");

        CheckChangeEventFormPage formPage = new CheckChangeEventFormPage(DriverManager.getDriver());

        // Step 1: Initial state — textbox should be visible
        Assert.assertTrue(formPage.isTextboxVisible(), "Textbox should be visible initially");
        System.out.println("Step 1 PASSED: Textbox visible initially");

        // Step 2: Check the box -> textbox should hide
        formPage.toggleHideShow();
        Assert.assertFalse(formPage.isTextboxVisible(), "Textbox should be hidden after checking HideShow");
        System.out.println("Step 2 PASSED: Textbox hidden after checking");

        // Step 3: Uncheck the box -> textbox should reappear
        formPage.toggleHideShow();
        Assert.assertTrue(formPage.isTextboxVisible(), "Textbox should reappear after unchecking HideShow");
        System.out.println("Step 3 PASSED: Textbox visible again after unchecking");

        System.out.println("********** verifyHideShowTogglesTextboxVisibility PASSED **********");
    }
    @Test(priority = 8)
    public void verifyGetdataShowsValue21() {
        System.out.println("********** verifyGetdataShowsValue21 STARTED **********");

        CheckChangeEventFormPage formPage = new CheckChangeEventFormPage(DriverManager.getDriver());

        formPage.checkGetdata();

        boolean valueShown = formPage.isCalculatedValueDisplayed("21", 10);
        Assert.assertTrue(valueShown, "GetDataValue line should show value '21' after checking Getdata checkbox");
        System.out.println("Step 1 PASSED: GetDataValue value 21 displayed correctly");

        System.out.println("********** verifyGetdataShowsValue21 PASSED **********");
    }
    @Test(priority = 9)
    public void verifyHardFormulaCalculation() {
        System.out.println("********** verifyHardFormulaCalculation STARTED **********");

        CheckChangeEventFormPage formPage = new CheckChangeEventFormPage(DriverManager.getDriver());

        String inputValue = "100";
        String expectedResultText = "200.0"; // confirmed manually: 100 -> 200

        formPage.enterValueForFormula(inputValue); // same GetDataValue EditText reused
        System.out.println("Step 1: Entered input value " + inputValue);

        formPage.checkHardFormula();

        boolean resultShown = formPage.isCalculatedValueDisplayed(expectedResultText, 10);
        Assert.assertTrue(resultShown, "Value field should show " + expectedResultText + " after checking HardFormula");
        System.out.println("Step 2 PASSED: Value field correctly shows " + expectedResultText);

        System.out.println("********** verifyHardFormulaCalculation PASSED **********");
    }
    
}