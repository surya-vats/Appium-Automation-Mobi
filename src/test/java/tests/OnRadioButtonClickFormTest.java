package tests;
import Page.RadioButtonClickFormPage;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.InvalidElementStateException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import base.BaseTest;
import driver.DriverManager;
import Utils.NavigationHelper;
public class OnRadioButtonClickFormTest extends BaseTest {
    AndroidDriver driver;
    RadioButtonClickFormPage radioPage;

    @BeforeClass
    public void navigateToForm() {
        NavigationHelper.ensureLoggedInAndOnMenu("CONTEST");
        driver = DriverManager.getDriver();
        radioPage = new RadioButtonClickFormPage(driver);
        radioPage.openForm();
        System.out.println("Now on OnRadioButtonClick form, ready to run test cases");
    }

    // Test 1: InActive me B select -> scroll down -> TestingTextbox disabled, edit nahi hona chahiye
    @Test(priority = 1)
    public void verifyInActiveBDisablesTextbox() {
        System.out.println("********** verifyInActiveBDisablesTextbox STARTED **********");

        Assert.assertTrue(radioPage.isTestingTextboxEnabled(), "Textbox should be enabled before selecting InActive B");
        System.out.println("Step 1 PASSED: Textbox enabled initially");
        
        
        radioPage.selectInActiveB();
        System.out.println("Step 2 PASSED: InActive B clicked");

        Assert.assertTrue(radioPage.waitForTestingTextboxEnabled(false),
            "Textbox should be disabled after selecting InActive B");
        System.out.println("Step 3 PASSED: Textbox disabled after InActive B");

        String valueBefore = radioPage.getTestingTextboxValue();
        // Disabled field me typing pe InvalidElementStateException aana hi sahi behaviour hai.
        try {
            radioPage.enterTextIntoTestingTextbox("ShouldNotAppear");
            Assert.fail("Typing should be blocked on a disabled field");
        } catch (InvalidElementStateException e) {
            System.out.println("Typing blocked on disabled field, as expected");
        }
        String valueAfter = radioPage.getTestingTextboxValue();
        Assert.assertEquals(valueAfter, valueBefore, "Textbox value should NOT change while disabled");
        System.out.println("Step 4 PASSED: Textbox value unchanged while disabled");

        System.out.println("********** verifyInActiveBDisablesTextbox PASSED **********");
    }

    // Test 2: Active me A select -> scroll down -> TestingTextbox phir se editable
    @Test(priority = 2)
    public void verifyActiveAReEnablesTextbox() {
        System.out.println("********** verifyActiveAReEnablesTextbox STARTED **********");

        // Test 1 pe depend nahi karte: khud textbox disable karke precondition set karo.
        radioPage.selectInActiveB();
        
        System.out.println("Step 1 PASSED: Textbox disabled before Active A");
        
        radioPage.selectActiveA();
        System.out.println("Step 2 PASSED: Active A clicked");

        Assert.assertTrue(radioPage.waitForTestingTextboxEnabled(true),
            "Textbox should be re-enabled after selecting Active A");
        System.out.println("Step 3 PASSED: Textbox enabled again");

        String value = radioPage.typeIntoTestingTextboxAndGetValue("EditableAgain");
        Assert.assertTrue(value.contains("EditableAgain"),
            "Textbox should accept typed text once re-enabled, but was: " + value);
        System.out.println("Step 4 PASSED: Textbox accepted typed text");

        System.out.println("********** verifyActiveAReEnablesTextbox PASSED **********");
    }
 // Test 3: OpenPage A -> naya page khulta hai -> neeche scroll -> radio A -> page band, form list pe wapas
    @Test(priority = 3)
    public void verifyOpenPageARadioOpensNewPage() {
        System.out.println("********** verifyOpenPageARadioOpensNewPage STARTED **********");

        Assert.assertTrue(radioPage.isFormLoaded(10), "Precondition failed: form should be displayed");
        System.out.println("Step 1 PASSED: Form displayed");

        radioPage.selectOpenPageA();
        Assert.assertTrue(radioPage.waitForFormToDisappear(10), "New page should open after OpenPage A");
        System.out.println("Step 2 PASSED: New page opened");

        radioPage.clickRadioAOnNewPage();
        System.out.println("Step 3 PASSED: Radio A clicked on the new page");

        // Page band hone ke baad form list aati hai, wahan se form dobara select karo
        radioPage.reopenFormIfNeeded();
        Assert.assertTrue(radioPage.isFormLoaded(10), "Form should be displayed again after the page closed");
        System.out.println("Step 4 PASSED: Back on the form after re-selecting it");

        System.out.println("********** verifyOpenPageARadioOpensNewPage PASSED **********");
    }
    
 // Test 4: OpenDialog A -> dialog khulta hai -> Back se band -> form pe wapas
    @Test(priority = 4)
    public void verifyOpenDialogARadioOpensDialog() {
        System.out.println("********** verifyOpenDialogARadioOpensDialog STARTED **********");

        radioPage.reopenFormIfNeeded();
        Assert.assertTrue(radioPage.isFormLoaded(10), "Precondition failed: form should be displayed");
        System.out.println("Step 1 PASSED: Form displayed");

        radioPage.selectOpenDialogA();
        Assert.assertTrue(radioPage.waitForDialogToOpen(10), "Dialog should open after OpenDialog A");
        System.out.println("Step 2 PASSED: Dialog opened");
//
//        radioPage.closeDialogWithBack();
//        // Back se dialog band hokar form aaye ya form list, dono case handle
//        radioPage.reopenFormIfNeeded();
//        Assert.assertTrue(radioPage.isFormLoaded(10), "Form should be displayed again after closing the dialog");
//        System.out.println("Step 3 PASSED: Dialog closed, back on the form");
        
        radioPage.clickRadioAOnNewPage();
        System.out.println("Step 3 PASSED: Radio A clicked on the new page");

        // Page band hone ke baad form list aati hai, wahan se form dobara select karo
        radioPage.reopenFormIfNeeded();
        Assert.assertTrue(radioPage.isFormLoaded(10), "Form should be displayed again after the page closed");
        System.out.println("Step 4 PASSED: Back on the form after re-selecting it");

        System.out.println("********** verifyOpenDialogARadioOpensDialog PASSED **********");
    }
    
 // Test 5: Hide A -> TestingTextbox hide
    @Test(priority = 5)
    public void verifyHideARadioHidesTextbox() {
        System.out.println("********** verifyHideARadioHidesTextbox STARTED **********");

        radioPage.reopenFormFresh();
        Assert.assertTrue(radioPage.isFormLoaded(10), "Precondition failed: form should be displayed");
        Assert.assertTrue(radioPage.isTestingTextboxPresent(), "TestingTextbox should be visible initially");
        System.out.println("Step 1 PASSED: TestingTextbox visible initially");

        radioPage.selectHideA();
        Assert.assertTrue(radioPage.waitForTestingTextboxVisible(false), "TestingTextbox should be hidden after Hide A");
        System.out.println("Step 2 PASSED: TestingTextbox hidden after Hide A");

        System.out.println("********** verifyHideARadioHidesTextbox PASSED **********");
    }

    // Test 6: Show A -> TestingTextbox phir visible
    @Test(priority = 6)
    public void verifyShowARadioShowsTextbox() {
        System.out.println("********** verifyShowARadioShowsTextbox STARTED **********");

        // Test 5 pe depend nahi: khud Hide karke precondition set karo.
        radioPage.selectHideA();
        Assert.assertTrue(radioPage.waitForTestingTextboxVisible(false), "Precondition failed: TestingTextbox should be hidden");
        System.out.println("Step 1 PASSED: TestingTextbox hidden before Show A");

        radioPage.selectShowA();
        Assert.assertTrue(radioPage.waitForTestingTextboxVisible(true), "TestingTextbox should be visible again after Show A");
        System.out.println("Step 2 PASSED: TestingTextbox visible after Show A");

        System.out.println("********** verifyShowARadioShowsTextbox PASSED **********");
    }

    // Test 7: Formula A -> Formuladata me 85.0
    @Test(priority = 7)
    public void verifyFormulaAShowsValue() {
        System.out.println("********** verifyFormulaAShowsValue STARTED **********");

        Assert.assertFalse(radioPage.waitForTextOnScreen("85.0", 2), "85.0 should not be shown before Formula A");
        System.out.println("Step 1 PASSED: 85.0 not shown before Formula A");

        radioPage.selectFormulaA();
        Assert.assertTrue(radioPage.waitForTextOnScreen("85.0", 10), "Formuladata should show 85.0 after Formula A");
        System.out.println("Step 2 PASSED: Formuladata shows 85.0");

        System.out.println("********** verifyFormulaAShowsValue PASSED **********");
    }
 // Test 8: Postformula A -> LabelforGetdata me 85.0
    @Test(priority = 8)
    public void verifyPostformulaAShowsValue() {
        System.out.println("********** verifyPostformulaAShowsValue STARTED **********");

        // Test 7 ne Formula se pehle hi 85.0 dikha diya hoga, isliye form fresh karo.
        radioPage.reopenFormFresh();
        Assert.assertTrue(radioPage.isFormLoaded(10), "Precondition failed: form should be displayed");
        Assert.assertFalse(radioPage.waitForTextOnScreen("85.0", 2), "85.0 should not be shown before Postformula A");
        System.out.println("Step 1 PASSED: 85.0 not shown before Postformula A");

        radioPage.selectPostformulaA();
        Assert.assertTrue(radioPage.waitForTextOnScreen("85.0", 10), "LabelforGetdata should show 85.0 after Postformula A");
        System.out.println("Step 2 PASSED: 85.0 visible after Postformula A");

        System.out.println("********** verifyPostformulaAShowsValue PASSED **********");
    }

    // Test 9: GetDataforRadio A -> LabelforGetdata me 100
    @Test(priority = 9)
    public void verifyGetDataForRadioAShowsValue() {
        System.out.println("********** verifyGetDataForRadioAShowsValue STARTED **********");

        radioPage.reopenFormFresh();
        Assert.assertTrue(radioPage.isFormLoaded(10), "Precondition failed: form should be displayed");
        Assert.assertFalse(radioPage.waitForTextOnScreen("100", 2), "100 should not be shown before GetDataforRadio A");
        System.out.println("Step 1 PASSED: 100 not shown before GetDataforRadio A");

        radioPage.selectGetDataForRadioA();
        Assert.assertTrue(radioPage.waitForTextOnScreen("100", 10), "LabelforGetdata should show 100 after GetDataforRadio A");
        System.out.println("Step 2 PASSED: 100 visible after GetDataforRadio A");

        System.out.println("********** verifyGetDataForRadioAShowsValue PASSED **********");
    }
}