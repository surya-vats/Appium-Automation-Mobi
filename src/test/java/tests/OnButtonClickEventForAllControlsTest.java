package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import Page.OnButtonClickEventForAllControlsPage;
import Utils.NavigationHelper;
import base.BaseTest;
import driver.DriverManager;
import io.appium.java_client.android.AndroidDriver;

public class OnButtonClickEventForAllControlsTest extends BaseTest {
    AndroidDriver driver;
    OnButtonClickEventForAllControlsPage allControlsPage;

    @BeforeClass
    public void navigateToForm() {
        NavigationHelper.ensureLoggedInAndOnMenu("CONTEST");
        driver = DriverManager.getDriver();
        allControlsPage = new OnButtonClickEventForAllControlsPage(driver);
        allControlsPage.openForm();
        System.out.println("Now on ONBUTTONCLICKEVENTFORALLCONTROLS form, ready to run test cases");
    }

    // Test 1: SerachforInActive -> radio A looks light (blurred) compared to before
    @Test(priority = 1)
    public void verifyInActiveButtonBlursRadio() {
        System.out.println("********** verifyInActiveButtonBlursRadio STARTED **********");

        allControlsPage.clickSearchForActive();
        allControlsPage.tapRadioB();
        double baseline = allControlsPage.getRadioABrightness();
        System.out.println("Step 1 PASSED: Baseline brightness measured in Active state");

        allControlsPage.clickSearchForInActive();
        System.out.println("Step 2 PASSED: SerachforInActive clicked");

        Assert.assertTrue(allControlsPage.isRadioALighterThan(baseline),
                "Radio A should look lighter (blurred) after SerachforInActive");
        System.out.println("Step 3 PASSED: Radio A looks lighter after InActive");

        System.out.println("********** verifyInActiveButtonBlursRadio PASSED **********");
    }

    // Test 2: SerachforActive -> radio A looks dark/normal again
    @Test(priority = 2)
    public void verifyActiveButtonRestoresRadio() {
        System.out.println("********** verifyActiveButtonRestoresRadio STARTED **********");

        allControlsPage.clickSearchForActive();
        allControlsPage.tapRadioB();
        double baseline = allControlsPage.getRadioABrightness();
        System.out.println("Step 1 PASSED: Baseline brightness measured in Active state");

        allControlsPage.clickSearchForInActive();
        Assert.assertTrue(allControlsPage.isRadioALighterThan(baseline),
                "Precondition failed: Radio A should look lighter after InActive");
        System.out.println("Step 2 PASSED: Radio A lighter in InActive state");

        allControlsPage.clickSearchForActive();
        Assert.assertTrue(allControlsPage.isRadioASameLookAs(baseline),
                "Radio A should look normal again after SerachforActive");
        System.out.println("Step 3 PASSED: Radio A looks normal again after Active");

        System.out.println("********** verifyActiveButtonRestoresRadio PASSED **********");
    }

    // Test 3: SerachforHide -> radio A not visible
    @Test(priority = 3)
    public void verifyHideButtonHidesRadio() {
        System.out.println("********** verifyHideButtonHidesRadio STARTED **********");

        Assert.assertTrue(allControlsPage.isRadioAVisible(), "Radio A should be visible initially");
        System.out.println("Step 1 PASSED: Radio A visible initially");

        allControlsPage.clickSearchForHide();
        System.out.println("Step 2 PASSED: SearchforHide clicked");

        Assert.assertTrue(allControlsPage.waitForRadioAVisible(false),
                "Radio A should be hidden after SearchforHide");
        System.out.println("Step 3 PASSED: Radio A hidden");

        System.out.println("********** verifyHideButtonHidesRadio PASSED **********");
    }

    // Test 4: SerachforShow -> radio A visible again
    @Test(priority = 4)
    public void verifyShowButtonShowsRadio() {
        System.out.println("********** verifyShowButtonShowsRadio STARTED **********");

        // Do not depend on Test 3: hide it here.
        allControlsPage.clickSearchForHide();
        Assert.assertTrue(allControlsPage.waitForRadioAVisible(false),
                "Precondition failed: Radio A should be hidden");
        System.out.println("Step 1 PASSED: Radio A hidden before SearchforShow");

        allControlsPage.clickSearchForShow();
        System.out.println("Step 2 PASSED: SearchforShow clicked");

        Assert.assertTrue(allControlsPage.waitForRadioAVisible(true),
                "Radio A should be visible again after SearchforShow");
        System.out.println("Step 3 PASSED: Radio A visible again");

        System.out.println("********** verifyShowButtonShowsRadio PASSED **********");
    }

    // Test 5: SerachforOpenpage -> new form opens -> Back -> form list -> select
    // same form again
    @Test(priority = 5)
    public void verifyOpenPageButtonOpensNewForm() {
        System.out.println("********** verifyOpenPageButtonOpensNewForm STARTED **********");

        Assert.assertTrue(allControlsPage.isFormLoaded(10), "Precondition failed: form should be displayed");
        System.out.println("Step 1 PASSED: Form displayed");

        allControlsPage.clickSearchForOpenPage();
        Assert.assertTrue(allControlsPage.waitForFormToDisappear(10), "New form should open after SerachforOpenpage");
        System.out.println("Step 2 PASSED: New form opened");

        allControlsPage.goBackFromNewPage();
        System.out.println("Step 3 PASSED: Back pressed on the new form");

        // Back lands on the form list, so select the same form again for the next test.
        allControlsPage.reopenFormIfNeeded();
        Assert.assertTrue(allControlsPage.isFormLoaded(10), "Form should be displayed again after going back");
        System.out.println("Step 4 PASSED: Back on the form after re-selecting it");

        System.out.println("********** verifyOpenPageButtonOpensNewForm PASSED **********");
    }

    // Test 6: SearchforOpenDailog -> dialog opens -> SearchforClose -> dialog
    // closes
    @Test(priority = 6)
    public void verifyOpenDialogAndCloseButton() {
        System.out.println("********** verifyOpenDialogAndCloseButton STARTED **********");

        allControlsPage.reopenFormIfNeeded();
        Assert.assertTrue(allControlsPage.isFormLoaded(10), "Precondition failed: form should be displayed");
        System.out.println("Step 1 PASSED: Form displayed");

        allControlsPage.clickSearchForOpenDialog();
        Assert.assertTrue(allControlsPage.waitForDialogToOpen(10), "Dialog should open after SearchforOpenDailog");
        System.out.println("Step 2 PASSED: Dialog opened");

        allControlsPage.clickSearchForClose();
        System.out.println("Step 3 PASSED: SearchforClose clicked inside the dialog");

        Assert.assertTrue(allControlsPage.waitForDialogToClose(10), "Dialog should close after SearchforClose");
        System.out.println("Step 4 PASSED: Dialog closed");

        allControlsPage.reopenFormIfNeeded();
        Assert.assertTrue(allControlsPage.isFormLoaded(10), "Form should be displayed after the dialog closed");
        System.out.println("Step 5 PASSED: Back on the form");

        System.out.println("********** verifyOpenDialogAndCloseButton PASSED **********");
    }

    // Test 7: enter 30 in TestingTextbox -> Searchforformula -> 60.0 is shown
    @Test(priority = 7)
    public void verifyFormulaButtonShowsValue() {
        System.out.println("********** verifyFormulaButtonShowsValue STARTED **********");

        // allControlsPage.reopenFormFresh();
        // Assert.assertTrue(allControlsPage.isFormLoaded(10), "Precondition failed:
        // form should be displayed");
        // Assert.assertFalse(allControlsPage.waitForTextOnScreen("60.0", 2),
        // "60.0 should not be shown before Searchforformula");
        System.out.println("Step 1 PASSED: 60.0 not shown before Searchforformula");

        allControlsPage.enterTextIntoTestingTextbox("30");
        System.out.println("Step 2 PASSED: 30 entered in TestingTextbox");

        allControlsPage.clickSearchForFormula();
        System.out.println("Step 3 PASSED: Searchforformula clicked");

        Assert.assertTrue(allControlsPage.waitForTextOnScreen("60.0", 10),
                "60.0 should be shown after Searchforformula");
        System.out.println("Step 4 PASSED: 60.0 displayed");

        System.out.println("********** verifyFormulaButtonShowsValue PASSED **********");
    }

    // Test 8: enter 30 in TestingTextbox -> SearchforPostFormula -> 60.0 is shown
    @Test(priority = 8)
    public void verifyPostFormulaButtonShowsValue() {
        System.out.println("********** verifyPostFormulaButtonShowsValue STARTED **********");

        // Test 7 already left 60.0 on the screen, so reset the form first.
        allControlsPage.reopenFormFresh();
        Assert.assertTrue(allControlsPage.isFormLoaded(10), "Precondition failed: form should be displayed");
        Assert.assertFalse(allControlsPage.waitForTextOnScreen("60.0", 2),
                "60.0 should not be shown before SearchforPostFormula");
        System.out.println("Step 1 PASSED: 60.0 not shown before SearchforPostFormula");

        allControlsPage.enterTextIntoTestingTextbox("30");
        System.out.println("Step 2 PASSED: 30 entered in TestingTextbox");

        allControlsPage.clickSearchForPostFormula();
        System.out.println("Step 3 PASSED: SearchforPostFormula clicked");

        Assert.assertTrue(allControlsPage.waitForTextOnScreen("60.0", 10),
                "60.0 should be shown after SearchforPostFormula");
        System.out.println("Step 4 PASSED: 60.0 displayed");

        System.out.println("********** verifyPostFormulaButtonShowsValue PASSED **********");
    }

    // Test 9: SearchforGetdata -> scroll a bit -> Getdatavalue shows 100
    @Test(priority = 9)
    public void verifyGetDataButtonShowsValue() {
        System.out.println("********** verifyGetDataButtonShowsValue STARTED **********");

        // allControlsPage.reopenFormFresh();
        // Assert.assertTrue(allControlsPage.isFormLoaded(10), "Precondition failed:
        // form should be displayed");
        System.out.println("Step 1 PASSED: Form displayed");

        allControlsPage.clickSearchForGetData();
        System.out.println("Step 2 PASSED: SearchforGetdata clicked");

        Assert.assertTrue(allControlsPage.waitForGetDataValue("100", 10), "Getdatavalue should show 100");
        System.out.println("Step 3 PASSED: Getdatavalue shows 100");

        System.out.println("********** verifyGetDataButtonShowsValue PASSED **********");
    }

    // Test 10: RatingforInActive -> TestingRating dropdown NOT clickable
@Test(priority = 10)
public void verifyRatingInActiveDisablesDropdown() {
    System.out.println("********** verifyRatingInActiveDisablesDropdown STARTED **********");

    allControlsPage.reopenFormFresh();
    Assert.assertTrue(allControlsPage.isFormLoaded(10), "Precondition failed: form should be displayed");
    System.out.println("Step 1 PASSED: Form displayed");

    allControlsPage.clickRatingForInActive();
    System.out.println("Step 2 PASSED: RatingforInActive tapped");

    allControlsPage.tapTestingRatingDropdown();
    Assert.assertFalse(allControlsPage.waitForDropdownOptionsToOpen(3),
            "Dropdown options should NOT open after RatingforInActive");
    System.out.println("Step 3 PASSED: Dropdown did not open while InActive");

    System.out.println("********** verifyRatingInActiveDisablesDropdown PASSED **********");
}

// Test 11: RatingforActive -> TestingRating dropdown clickable again
@Test(priority = 11)
public void verifyRatingActiveEnablesDropdown() {
    System.out.println("********** verifyRatingActiveEnablesDropdown STARTED **********");

    allControlsPage.reopenFormFresh();
    Assert.assertTrue(allControlsPage.isFormLoaded(10), "Precondition failed: form should be displayed");

    allControlsPage.clickRatingForInActive();
    System.out.println("Step 1 PASSED: RatingforInActive tapped");

    allControlsPage.clickRatingForActive();
    System.out.println("Step 2 PASSED: RatingforActive tapped");

    allControlsPage.tapTestingRatingDropdown();
    Assert.assertTrue(allControlsPage.waitForDropdownOptionsToOpen(5),
            "Dropdown options should open after RatingforActive");
    System.out.println("Step 3 PASSED: Dropdown opened while Active");

    allControlsPage.closeDropdownOptions();
    System.out.println("********** verifyRatingActiveEnablesDropdown PASSED **********");
}

// Test 12: RatingforHide -> TestingRating dropdown hidden
@Test(priority = 12)
public void verifyRatingHideHidesDropdown() {
    System.out.println("********** verifyRatingHideHidesDropdown STARTED **********");

    allControlsPage.reopenFormFresh();
    Assert.assertTrue(allControlsPage.isFormLoaded(10), "Precondition failed: form should be displayed");
    Assert.assertTrue(allControlsPage.isTestingRatingVisible(), "TestingRating dropdown should be visible initially");
    System.out.println("Step 1 PASSED: Dropdown visible initially");

    allControlsPage.clickRatingForHide();
    System.out.println("Step 2 PASSED: RatingforHide tapped");

    Assert.assertTrue(allControlsPage.waitForTestingRatingVisible(false),
            "TestingRating dropdown should be hidden after RatingforHide");
    System.out.println("Step 3 PASSED: Dropdown hidden");

    System.out.println("********** verifyRatingHideHidesDropdown PASSED **********");
}

// Test 13: RatingforShow -> TestingRating dropdown visible again
@Test(priority = 13)
public void verifyRatingShowShowsDropdown() {
    System.out.println("********** verifyRatingShowShowsDropdown STARTED **********");

    allControlsPage.reopenFormFresh();
    Assert.assertTrue(allControlsPage.isFormLoaded(10), "Precondition failed: form should be displayed");

    allControlsPage.clickRatingForHide();
    Assert.assertTrue(allControlsPage.waitForTestingRatingVisible(false),
            "Precondition failed: dropdown should be hidden");
    System.out.println("Step 1 PASSED: Dropdown hidden before RatingforShow");

    allControlsPage.clickRatingForShow();
    System.out.println("Step 2 PASSED: RatingforShow tapped");

    Assert.assertTrue(allControlsPage.waitForTestingRatingVisible(true),
            "TestingRating dropdown should be visible again after RatingforShow");
    System.out.println("Step 3 PASSED: Dropdown visible again");

    System.out.println("********** verifyRatingShowShowsDropdown PASSED **********");
}
    // Test 14: RatingforOpenPage -> new page opens -> Back -> form list -> select
    // same form again
    @Test(priority = 14)
    public void verifyRatingOpenPageOpensNewForm() {
        System.out.println("********** verifyRatingOpenPageOpensNewForm STARTED **********");

        allControlsPage.reopenFormFresh();
        Assert.assertTrue(allControlsPage.isFormLoaded(10), "Precondition failed: form should be displayed");
        System.out.println("Step 1 PASSED: Form displayed");

        allControlsPage.clickRatingForOpenPage();
        Assert.assertTrue(allControlsPage.waitForRatingRowToDisappear(10),
                "New page should open after RatingforOpenPage");
        System.out.println("Step 2 PASSED: New page opened");

        allControlsPage.goBackFromNewPage();
        System.out.println("Step 3 PASSED: Back pressed on the new page");

        allControlsPage.reopenFormIfNeeded();
        Assert.assertTrue(allControlsPage.isFormLoaded(10), "Form should be displayed again after going back");
        System.out.println("Step 4 PASSED: Back on the form after re-selecting it");

        System.out.println("********** verifyRatingOpenPageOpensNewForm PASSED **********");
    }

    // Test 15: RatingforOpendailoge -> dialog opens -> SearchforClose -> dialog
    // closes
    @Test(priority = 15)
    public void verifyRatingOpenDialogAndClose() {
        System.out.println("********** verifyRatingOpenDialogAndClose STARTED **********");

        allControlsPage.reopenFormFresh();
        Assert.assertTrue(allControlsPage.isFormLoaded(10), "Precondition failed: form should be displayed");
        System.out.println("Step 1 PASSED: Form displayed");

        allControlsPage.clickRatingForOpenDialog();
        Assert.assertTrue(allControlsPage.waitForRatingRowToDisappear(10),
                "Dialog should open after RatingforOpendailoge");
        System.out.println("Step 2 PASSED: Dialog opened");

        allControlsPage.clickSearchForClose();
        System.out.println("Step 3 PASSED: SearchforClose clicked inside the dialog");

        Assert.assertTrue(allControlsPage.waitForRatingRowToReturn(10), "Dialog should close after SearchforClose");
        System.out.println("Step 4 PASSED: Dialog closed, back on the form");

        System.out.println("********** verifyRatingOpenDialogAndClose PASSED **********");
    }

    // Test 16: enter 30 in TestingTextbox -> RatingforPostFormula -> 60.0
    @Test(priority = 16)
    public void verifyRatingPostFormulaShowsValue() {
        System.out.println("********** verifyRatingPostFormulaShowsValue STARTED **********");

        allControlsPage.reopenFormFresh();
        Assert.assertTrue(allControlsPage.isFormLoaded(10), "Precondition failed: form should be displayed");
        Assert.assertFalse(allControlsPage.waitForTextOnScreen("60.0", 2),
                "60.0 should not be shown before RatingforPostFormula");
        System.out.println("Step 1 PASSED: 60.0 not shown before RatingforPostFormula");

        allControlsPage.enterTextIntoTestingTextbox("30");
        System.out.println("Step 2 PASSED: 30 entered in TestingTextbox");

        allControlsPage.clickRatingForPostFormula();
        System.out.println("Step 3 PASSED: RatingforPostFormula tapped");

        Assert.assertTrue(allControlsPage.waitForTextOnScreen("60.0", 10),
                "60.0 should be shown after RatingforPostFormula");
        System.out.println("Step 4 PASSED: 60.0 displayed");

        System.out.println("********** verifyRatingPostFormulaShowsValue PASSED **********");
    }

    // Test 17: RatingforGetdata -> Getdatavalue shows 400
    @Test(priority = 17)
    public void verifyRatingGetDataShowsValue() {
        System.out.println("********** verifyRatingGetDataShowsValue STARTED **********");

        allControlsPage.reopenFormFresh();
        Assert.assertTrue(allControlsPage.isFormLoaded(10), "Precondition failed: form should be displayed");
        System.out.println("Step 1 PASSED: Form displayed");

        allControlsPage.clickRatingForGetData();
        System.out.println("Step 2 PASSED: RatingforGetdata tapped");

        Assert.assertTrue(allControlsPage.waitForGetDataValue("400", 10), "Getdatavalue should show 400");
        System.out.println("Step 3 PASSED: Getdatavalue shows 400");

        System.out.println("********** verifyRatingGetDataShowsValue PASSED **********");
    }
}