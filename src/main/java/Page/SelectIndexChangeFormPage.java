package Page;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.remote.RemoteWebElement;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SelectIndexChangeFormPage extends BasePage {

    // ---------- Locators ----------
    private final By inActiveDropdown = AppiumBy.xpath(
        "//android.widget.TextView[@text='InActive']/following-sibling::android.widget.Spinner");
    private final By activeDropdown = AppiumBy.androidUIAutomator(
        "new UiSelector().className(\"android.widget.RelativeLayout\").instance(1)");
    private final By openPageDropdown = AppiumBy.androidUIAutomator(
        "new UiSelector().className(\"android.widget.RelativeLayout\").instance(2)");
    private final By openAndCloseDropdown = AppiumBy.androidUIAutomator(
        "new UiSelector().className(\"android.widget.RelativeLayout\").instance(3)");
    private final By hideDropdown = AppiumBy.androidUIAutomator(
        "new UiSelector().className(\"android.widget.RelativeLayout\").instance(4)");
    private final By showDropdown = AppiumBy.androidUIAutomator(
        "new UiSelector().className(\"android.widget.RelativeLayout\").instance(5)");

    private final By testingTextbox = AppiumBy.androidUIAutomator(
        "new UiSelector().className(\"android.widget.EditText\").instance(0)");

    // The "OpenPage" label exists only on this form, so it tells us whether the form is on screen.
    private final By formMarker = AppiumBy.xpath("//android.widget.TextView[@text='OpenPage']");
 // The dialog is a BottomSheet; this id exists only while it is open, so it is a safe open/close marker.
 //   private final By dialogMarker = AppiumBy.id("com.mobi.dds_iuv:id/design_bottom_sheet");
    private final By dialogMarker = AppiumBy.id("com.mobi.dds_iuv:id/imgbtn_close");
    // The form ALSO has a ScrollView with the same resource-id, so we scope the search
    // to the ScrollView that lives INSIDE the bottom sheet.
    private final By dialogScrollView = AppiumBy.xpath(
        "//*[@resource-id='com.mobi.dds_iuv:id/design_bottom_sheet']//android.widget.ScrollView");

    private final By onGridTextCloseLabel = AppiumBy.xpath(
        "//android.widget.TextView[@text='OnGridtextclose']");

    // following::...[1] = the first Spinner that comes after the label in the screen order.
    private final By onGridTextCloseDropdown = AppiumBy.xpath(
        "//android.widget.TextView[@text='OnGridtextclose']/following::android.widget.Spinner[1]");
    // preceding:: walks backwards from the PostFormula label; [1] is the NEAREST EditText.
    // When TestingTextbox is hidden, no EditText exists before the label, so the result is empty.
    private final By testingTextboxAnchored = AppiumBy.xpath(
        "//android.widget.TextView[@text='PostFormula']/preceding::android.widget.EditText[1]");

    
    private final By formulaDropdown = AppiumBy.androidUIAutomator(
    	    "new UiSelector().className(\"android.widget.RelativeLayout\").instance(6)");
    	private final By getDataDropdown = AppiumBy.androidUIAutomator(
    	    "new UiSelector().resourceId(\"com.mobi.dds_iuv:id/layoutSpinner\").instance(7)");
    	private final By postFormulaDropdown = AppiumBy.androidUIAutomator(
    	    "new UiSelector().className(\"android.widget.RelativeLayout\").instance(8)");

    public SelectIndexChangeFormPage(AndroidDriver driver) {
        super(driver);
        // Makes Appium use its XPath 1.0 engine, which supports the preceding:: axis.
        // The default XPath 2 engine crashes on it with the "cannot be cast to NodeType" error.
        driver.setSetting("enforceXPath1", true);
    }

    // ---------- Shared helper ----------
    private void selectOption(String option) {
        // (//*[@text='B'])[last()] takes the LAST match of the text. The popup is drawn on top,
        // so it comes last; earlier matches are the "B" already shown inside spinners.
        By optionLocator = AppiumBy.xpath("(//*[@text='" + option + "'])[last()]");
        System.out.println("Selecting option: " + option);
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.elementToBeClickable(optionLocator)).click();
    }

    // ---------- Test 1: InActive ----------
    public void clickInActiveDropdown() {
        System.out.println("Clicking InActive dropdown");
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.elementToBeClickable(inActiveDropdown)).click();
    }

    public void selectInActiveOption(String option) {
        clickInActiveDropdown();
        selectOption(option);
    }

    public boolean isTextboxTestingEnabled() {
        WebElement box = new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.presenceOfElementLocated(testingTextbox));
        boolean enabled = box.isEnabled();
        System.out.println("TestingTextbox enabled = " + enabled);
        return enabled;
    }

    // ---------- Test 2: Active ----------
    public void clickActiveDropdown() {
        System.out.println("Clicking Active dropdown");
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.elementToBeClickable(activeDropdown)).click();
    }

    public void selectActiveOption(String option) {
        clickActiveDropdown();
        selectOption(option);
    }

    public void enterTextIntoTextbox(String text) {
        WebElement box = new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.presenceOfElementLocated(testingTextbox));
        box.clear();
        box.sendKeys(text);
        System.out.println("Typed into TestingTextbox: " + text);
    }

    public String getTextboxValue() {
        String value = new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.presenceOfElementLocated(testingTextbox)).getText();
        System.out.println("TestingTextbox value = " + value);
        return value;
    }

    // ---------- Test 3: OpenPage ----------
    public void selectOpenPageOption(String option) {
        System.out.println("Clicking OpenPage dropdown");
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.elementToBeClickable(openPageDropdown)).click();
        selectOption(option);
    }

    public boolean isFormLoaded(int seconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(seconds))
                .until(ExpectedConditions.visibilityOfElementLocated(formMarker));
            System.out.println("Form is displayed");
            return true;
        } catch (TimeoutException e) {
            System.out.println("Form is NOT displayed");
            return false;
        }
    }

    public boolean waitForFormToDisappear(int seconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(seconds))
                .until(ExpectedConditions.invisibilityOfElementLocated(formMarker));
            System.out.println("Form disappeared, new screen is open");
            return true;
        } catch (TimeoutException e) {
            System.out.println("Form is still displayed");
            return false;
        }
    }

    // ---------- Test 4: OpenAndClose dialog ----------
    public void selectOpenAndCloseOption(String option) {
        System.out.println("Clicking OpenAndClose dropdown");
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.elementToBeClickable(openAndCloseDropdown)).click();
        selectOption(option);
    }

    

 
    public boolean isDialogOpen(int seconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(seconds))
                .until(ExpectedConditions.presenceOfElementLocated(dialogMarker));
            System.out.println("Dialog is open");
            return true;
        } catch (TimeoutException e) {
            System.out.println("Dialog is NOT open");
            return false;
        }
    }

    public boolean isDialogClosed(int seconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(seconds))
                .until(ExpectedConditions.invisibilityOfElementLocated(dialogMarker));
            System.out.println("Dialog is closed");
            return true;
        } catch (TimeoutException e) {
            System.out.println("Dialog is still open");
            return false;
        }
    }

    // ---------- Tests 5 and 6: Hide / Show ----------
    public void selectHideOption(String option) {
        System.out.println("Clicking Hide dropdown");
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.elementToBeClickable(hideDropdown)).click();
        selectOption(option);
    }

    public void selectShowOption(String option) {
        System.out.println("Clicking Show dropdown");
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.elementToBeClickable(showDropdown)).click();
        selectOption(option);
    }

    public boolean isTestingTextboxVisible() {
        List<WebElement> boxes = driver.findElements(testingTextboxAnchored);
        boolean visible = !boxes.isEmpty() && boxes.get(0).isDisplayed();
        System.out.println("TestingTextbox visible = " + visible);
        return visible;
    }

    public void waitForTestingTextboxVisible(boolean expected) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> isTestingTextboxVisible() == expected);
        } catch (TimeoutException e) {
            System.out.println("TestingTextbox did not become visible=" + expected + " in time");
        }
    }
    
    public void scrollToOnGridTextCloseDropdown() {
        WebElement sheetScroll = new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.presenceOfElementLocated(dialogScrollView));
        // scrollGesture needs the element id of the container we want to scroll
        String scrollId = ((RemoteWebElement) sheetScroll).getId();

        for (int attempt = 1; attempt <= 8; attempt++) {
            List<WebElement> found = driver.findElements(onGridTextCloseLabel);
            if (!found.isEmpty() && found.get(0).isDisplayed()) {
                System.out.println("OnGridtextclose label visible after " + (attempt - 1) + " scroll(s)");
                return;
            }
            System.out.println("Scroll attempt " + attempt + ": scrolling inside the dialog");
            driver.executeScript("mobile: scrollGesture", Map.of(
                "elementId", scrollId,
                "direction", "down",
                "percent", 0.5
            ));
        }
        System.out.println("OnGridtextclose label still not visible after scrolling");
    }

    public void selectOnGridTextCloseOption(String option) {
        scrollToOnGridTextCloseDropdown();
        System.out.println("Clicking OnGridtextclose dropdown");
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.elementToBeClickable(onGridTextCloseDropdown)).click();
        selectOption(option);
    }
    
    
    	public void selectFormulaOption(String option) {
    	    System.out.println("Clicking Formula dropdown");
    	    new WebDriverWait(driver, Duration.ofSeconds(10))
    	        .until(ExpectedConditions.elementToBeClickable(formulaDropdown)).click();
    	    selectOption(option);
    	}

    	public void selectGetDataOption(String option) {
    	    System.out.println("Clicking GetData dropdown");
    	    new WebDriverWait(driver, Duration.ofSeconds(10))
    	        .until(ExpectedConditions.elementToBeClickable(getDataDropdown)).click();
    	    selectOption(option);
    	}

    	public void selectPostFormulaOption(String option) {
    	    System.out.println("Clicking PostFormula dropdown");
    	    new WebDriverWait(driver, Duration.ofSeconds(10))
    	        .until(ExpectedConditions.elementToBeClickable(postFormulaDropdown)).click();
    	    selectOption(option);
    	}

    	// textMatches needs the WHOLE text to match the regex. "([.]0)?" makes ".0" optional,
    	// so "500" and "500.0" both pass. [.] is used instead of \. to avoid backslash escaping issues.
    	public boolean isValueTextDisplayed(String number, int seconds) {
    	    By valueLocator = AppiumBy.androidUIAutomator(
    	        "new UiSelector().textMatches(\"" + number + "([.]0)?\")");
    	    try {
    	        new WebDriverWait(driver, Duration.ofSeconds(seconds))
    	            .until(ExpectedConditions.visibilityOfElementLocated(valueLocator));
    	        System.out.println("Value " + number + " is displayed");
    	        return true;
    	    } catch (TimeoutException e) {
    	        System.out.println("Value " + number + " is NOT displayed");
    	        return false;
    	    }
    	}
}