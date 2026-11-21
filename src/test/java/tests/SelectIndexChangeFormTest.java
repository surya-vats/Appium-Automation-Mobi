package tests;
import Page.FormsPage;
import Page.SelectIndexChangeFormPage;
import io.appium.java_client.android.AndroidDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import base.BaseTest;
import driver.DriverManager;
import Utils.NavigationHelper;

public class SelectIndexChangeFormTest extends BaseTest {
    AndroidDriver driver;
    SelectIndexChangeFormPage selectIndexChangeFormPage;
    FormsPage formsPage;
    @BeforeClass
    public void navigateToForm() {
        NavigationHelper.ensureLoggedInAndOnMenu("CONTEST");
        driver = DriverManager.getDriver();
        formsPage = new FormsPage(driver);
        System.out.println("Selecting SELECTINDEXCHANGE from forms grid");
        formsPage.selectForm("SELECTEDINDEXCHANGEDTESTING");
        selectIndexChangeFormPage = new SelectIndexChangeFormPage(driver);
        System.out.println("Now on SelectIndexChange form, ready to run test cases");
    }

    @Test(priority = 1)
    public void verifyInActiveOptionBDisablesTextbox() {
        System.out.println("********** verifyInActiveOptionBDisablesTextbox STARTED **********");
        Assert.assertTrue(selectIndexChangeFormPage.isTextboxTestingEnabled(), "TestingTextbox should be enabled before selecting B in InActive");
        selectIndexChangeFormPage.selectInActiveOption("B");
        Assert.assertFalse(selectIndexChangeFormPage.isTextboxTestingEnabled(), "TestingTextbox should be disabled after selecting B in InActive");
        System.out.println("********** verifyInActiveOptionBDisablesTextbox PASSED **********");
    }
    @Test(priority = 2)
    public void verifyActiveOptionBReEnablesTextbox() {
        System.out.println("********** verifyActiveOptionBReEnablesTextbox STARTED **********");

        // Test 1 left the textbox disabled, so this precondition makes the re-enable check meaningful
        Assert.assertFalse(selectIndexChangeFormPage.isTextboxTestingEnabled(),
            "TestingTextbox should still be disabled before selecting B in Active");

        selectIndexChangeFormPage.selectActiveOption("B");

        Assert.assertTrue(selectIndexChangeFormPage.isTextboxTestingEnabled(),
            "TestingTextbox should be enabled after selecting B in Active");

        selectIndexChangeFormPage.enterTextIntoTextbox("EditableAgain");
        String value = selectIndexChangeFormPage.getTextboxValue();
        Assert.assertTrue(value.contains("EditableAgain"),
            "TestingTextbox should accept typed text once re-enabled");

        System.out.println("********** verifyActiveOptionBReEnablesTextbox PASSED **********");
    }
    @Test(priority = 3)
    public void verifyOpenPageOptionBOpensNewPage() {
        System.out.println("********** verifyOpenPageOptionBOpensNewPage STARTED **********");

        Assert.assertTrue(selectIndexChangeFormPage.isFormLoaded(10),
            "Precondition failed: form is not displayed before selecting B in OpenPage");

        selectIndexChangeFormPage.selectOpenPageOption("B");

        Assert.assertTrue(selectIndexChangeFormPage.waitForFormToDisappear(10),
            "A new page should open after selecting B in OpenPage");

        System.out.println("Clicking back button to exit the screen opened by OpenPage");
        selectIndexChangeFormPage.navigateBackFromForm(); // inherited from BasePage

        // Back lands on the forms list, so re-select the form for the next tests
        if (!selectIndexChangeFormPage.isFormLoaded(3)) {
            formsPage.selectForm("SELECTEDINDEXCHANGEDTESTING");
            System.out.println("Re-selected SELECTINDEXCHANGE after OpenPage");
        }

        Assert.assertTrue(selectIndexChangeFormPage.isFormLoaded(10),
            "Form should be displayed again after going back");

        System.out.println("********** verifyOpenPageOptionBOpensNewPage PASSED **********");
    } 
    @Test(priority = 4)
    public void verifyOpenAndCloseDialogOpensAndAutoCloses() {
        System.out.println("********** verifyOpenAndCloseDialogOpensAndAutoCloses STARTED **********");

        selectIndexChangeFormPage.selectOpenAndCloseOption("B");
        Assert.assertTrue(selectIndexChangeFormPage.isDialogOpen(10),
            "Dialog should open after selecting B in OpenAndClose");

        selectIndexChangeFormPage.selectOnGridTextCloseOption("B");
        Assert.assertTrue(selectIndexChangeFormPage.isDialogClosed(10),
            "Dialog should auto close after selecting B in OnGridtextclose");

        System.out.println("********** verifyOpenAndCloseDialogOpensAndAutoCloses PASSED **********");
    }
    @Test(priority = 5)
    public void verifyHideOptionBHidesTextbox() {
        System.out.println("********** verifyHideOptionBHidesTextbox STARTED **********");

        Assert.assertTrue(selectIndexChangeFormPage.isTestingTextboxVisible(),
            "TestingTextbox should be visible before selecting B in Hide");

        selectIndexChangeFormPage.selectHideOption("B");
        selectIndexChangeFormPage.waitForTestingTextboxVisible(false);

        Assert.assertFalse(selectIndexChangeFormPage.isTestingTextboxVisible(),
            "TestingTextbox should be hidden after selecting B in Hide");

        System.out.println("********** verifyHideOptionBHidesTextbox PASSED **********");
    }

   @Test(priority = 6)
    public void verifyShowOptionBShowsTextbox() {
        System.out.println("********** verifyShowOptionBShowsTextbox STARTED **********");

        // Test 5 left it hidden, so this precondition makes the Show check meaningful
        Assert.assertFalse(selectIndexChangeFormPage.isTestingTextboxVisible(),
            "TestingTextbox should still be hidden before selecting B in Show");

        selectIndexChangeFormPage.selectShowOption("B");
        selectIndexChangeFormPage.waitForTestingTextboxVisible(true);

        Assert.assertTrue(selectIndexChangeFormPage.isTestingTextboxVisible(),
            "TestingTextbox should be visible after selecting B in Show");

        System.out.println("********** verifyShowOptionBShowsTextbox PASSED **********");
    }
   
   @Test(priority = 7)
   public void verifyFormulaOptionBShowsFiftyFive() {
       System.out.println("********** verifyFormulaOptionBShowsFiftyFive STARTED **********");

       selectIndexChangeFormPage.selectFormulaOption("B");
       Assert.assertTrue(selectIndexChangeFormPage.isValueTextDisplayed("55", 10),
           "FormulaLabel should show 55 after selecting B in Formula");

       System.out.println("********** verifyFormulaOptionBShowsFiftyFive PASSED **********");
   }

   @Test(priority = 8)
   public void verifyGetDataOptionBShowsFiveHundred() {
       System.out.println("********** verifyGetDataOptionBShowsFiveHundred STARTED **********");

       selectIndexChangeFormPage.selectGetDataOption("B");
       Assert.assertTrue(selectIndexChangeFormPage.isValueTextDisplayed("500", 10),
           "GetdataLabel should show 500 after selecting B in GetData");

       System.out.println("********** verifyGetDataOptionBShowsFiveHundred PASSED **********");
   }

   @Test(priority = 9)
   public void verifyPostFormulaOptionBShowsFifty() {
       System.out.println("********** verifyPostFormulaOptionBShowsFifty STARTED **********");

       selectIndexChangeFormPage.selectPostFormulaOption("B");
       Assert.assertTrue(selectIndexChangeFormPage.isValueTextDisplayed("55", 10),
           "FormulaLabel should show 55 after selecting B in PostFormula");

       System.out.println("********** verifyPostFormulaOptionBShowsFifty PASSED **********");
   }
}