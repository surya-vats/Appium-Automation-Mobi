package Page;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

public class GridTextAutoFormPage extends BasePage {

    // ---------- Locators ----------
    // UiSelector.text("...") is an EXACT match, so text("Active") does NOT match "InActive",
    // and text("Formula") does NOT match "PostFormula". Spelling and case must match the app exactly.
    private final By inActiveButton = AppiumBy.androidUIAutomator("new UiSelector().text(\"InActive\")");
    private final By activeButton = AppiumBy.androidUIAutomator("new UiSelector().text(\"Active\")");
    private final By hideButton = AppiumBy.androidUIAutomator("new UiSelector().text(\"Hide\")");
    private final By showButton = AppiumBy.androidUIAutomator("new UiSelector().text(\"Show\")");
    private final By openPageButton = AppiumBy.androidUIAutomator("new UiSelector().text(\"OpenPage\")");
    // "OpenCloseDailog" is written exactly as you gave it (with the 'Dailog' spelling)
    private final By openCloseDialogButton = AppiumBy.androidUIAutomator("new UiSelector().text(\"OpenCloseDailog\")");
    private final By closeButton = AppiumBy.androidUIAutomator("new UiSelector().text(\"Gridtextclose\")");
    private final By getDataButton = AppiumBy.androidUIAutomator("new UiSelector().text(\"Getdata\")");
    private final By formulaButton = AppiumBy.androidUIAutomator("new UiSelector().text(\"Formula\")");
    private final By postFormulaButton = AppiumBy.androidUIAutomator("new UiSelector().text(\"PostFormula\")");

    private final By testingTextbox = AppiumBy.androidUIAutomator("new UiSelector().text(\"TestingTextbox\")");

    // Once we type, the field text changes, so the text("TestingTextbox") locator stops matching.
    // The focused EditText is how we find the same field again after typing.
    private final By focusedTextbox = AppiumBy.androidUIAutomator(
        "new UiSelector().className(\"android.widget.EditText\").focused(true)");

    // The formula input: matches either the hint or the text "Testingformula".
    // The hint never changes after typing, so this locator keeps working once the field has a value.
    private final By formulaTextbox = AppiumBy.xpath(
        "//android.widget.EditText[@hint='Testingformula' or @text='Testingformula']");

    // "InActive" is on this form only (not on the page/dialog that OpenPage opens),
    // so it tells us whether the form is on screen. It sits at the top, so scroll to top first.
    private final By formMarker = inActiveButton;

    public GridTextAutoFormPage(AndroidDriver driver) {
        super(driver);
    }

    // ---------- Shared helpers ----------
    private void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // restore the interrupt flag so the caller's thread still knows it was interrupted
            Thread.currentThread().interrupt();
        }
    }

    // mobile: scrollGesture returns true while MORE scrolling is possible, so we loop until it returns false.
    public void scrollToTop() {
        System.out.println("Scrolling to top of the screen");
        for (int attempt = 1; attempt <= 10; attempt++) {
            Boolean canScrollMore = (Boolean) driver.executeScript("mobile: scrollGesture", Map.of(
                "left", 100, "top", 300, "width", 800, "height", 1400,
                "direction", "up",
                "percent", 1.0
            ));
            if (!Boolean.TRUE.equals(canScrollMore)) {
                break;
            }
        }
    }

    // findElements returns an EMPTY list (no exception) when nothing matches, so it is safe inside a loop.
    private WebElement scrollDownUntilVisible(By locator) {
        for (int attempt = 1; attempt <= 10; attempt++) {
            List<WebElement> found = driver.findElements(locator);
            if (!found.isEmpty() && found.get(0).isDisplayed()) {
                System.out.println("Element visible after " + (attempt - 1) + " scroll(s)");
                return found.get(0);
            }
            System.out.println("Scroll attempt " + attempt + ": scrolling down");
            driver.executeScript("mobile: scrollGesture", Map.of(
                "left", 100, "top", 300, "width", 800, "height", 1400,
                "direction", "down",
                "percent", 0.5
            ));
            pause(700);
        }
        throw new NoSuchElementException("Element not visible after scrolling: " + locator);
    }

    private void clickAfterScrolling(By locator, String name) {
        System.out.println("Looking for " + name);
        WebElement element = scrollDownUntilVisible(locator);
        Rectangle r = element.getRect();
        System.out.println(name + " bounds before click: x=" + r.getX() + " y=" + r.getY()
            + " w=" + r.getWidth() + " h=" + r.getHeight());
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.elementToBeClickable(element)).click();
        System.out.println("Clicked " + name);
    }

    // Every button sits somewhere down the form, so we start from the top each time.
    // That way the scroll-down search always begins from the same screen position.
    private void clickFromTop(By locator, String name) {
        scrollToTop();
        clickAfterScrolling(locator, name);
    }

    // The keyboard covers part of the screen and can interfere with the next scroll gesture.
    private void hideKeyboardIfShown() {
        try {
            if (driver.isKeyboardShown()) {
                driver.hideKeyboard();
                System.out.println("Keyboard hidden");
            }
        } catch (Exception e) {
            System.out.println("Could not hide keyboard: " + e.getMessage());
        }
    }

    // ---------- Form state ----------
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
            System.out.println("Form disappeared, new page is open");
            return true;
        } catch (TimeoutException e) {
            System.out.println("Form is still displayed");
            return false;
        }
    }

    // ---------- Tests 1 and 2: InActive / Active ----------
    public void clickInActiveButton() {
        clickFromTop(inActiveButton, "InActive button");
    }

    public void clickActiveButton() {
        clickFromTop(activeButton, "Active button");
    }

    // Start from the top, then scroll down to the textbox, so the search never begins below it.
    private WebElement findTestingTextbox() {
        scrollToTop();
        return scrollDownUntilVisible(testingTextbox);
    }

    public boolean isTestingTextboxEnabled() {
        WebElement box = findTestingTextbox();
        boolean enabled = box.isEnabled();
        System.out.println("TestingTextbox enabled = " + enabled);
        return enabled;
    }

    // Tries to type into the (disabled) textbox and reports whether the typing was blocked.
    // If typing worked, the field text would change and text("TestingTextbox") would no longer match.
    // So: the locator still finds the field = nothing was typed = typing is blocked.
    // click()/sendKeys() on a disabled field may throw, so we catch that and treat it as blocked too.
    public boolean isTypingBlockedInTestingTextbox() {
        WebElement box = findTestingTextbox();
        try {
            box.click();
            box.sendKeys("ShouldNotType");
            System.out.println("sendKeys did not throw on the disabled field");
        } catch (Exception e) {
            System.out.println("Typing into disabled field was rejected: " + e.getClass().getSimpleName());
        }
        hideKeyboardIfShown();
        List<WebElement> found = driver.findElements(testingTextbox);
        boolean blocked = !found.isEmpty();
        System.out.println("Typing blocked = " + blocked);
        return blocked;
    }

    // Types into the textbox and returns what the field shows.
    // We keep ONE WebElement for the whole method because the field text changes once we type.
    // clear() at the end brings the hint back so later tests can find the field by text again.
    public String typeIntoTestingTextbox(String text) {
        WebElement box = findTestingTextbox();
        box.click();
        box.sendKeys(text);

        WebElement typedBox = new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.presenceOfElementLocated(focusedTextbox));
        String value = typedBox.getText();
        System.out.println("Typed into TestingTextbox: " + text + ", field shows: " + value);

        typedBox.clear();
        hideKeyboardIfShown();
        return value;
    }

    // ---------- Tests 3 and 4: Hide / Show ----------
    public void clickHideButton() {
        clickFromTop(hideButton, "Hide button");
    }

    public void clickShowButton() {
        clickFromTop(showButton, "Show button");
    }

    // "Visible" = in the hierarchy AND displayed. When the app hides the textbox, findElements
    // returns an empty list (no exception). We only conclude "hidden" after reaching the bottom.
    public boolean isTestingTextboxVisible() {
        scrollToTop();
        for (int attempt = 1; attempt <= 12; attempt++) {
            List<WebElement> found = driver.findElements(testingTextbox);
            if (!found.isEmpty() && found.get(0).isDisplayed()) {
                System.out.println("TestingTextbox visible = true");
                return true;
            }
            Boolean canScrollMore = (Boolean) driver.executeScript("mobile: scrollGesture", Map.of(
                "left", 100, "top", 300, "width", 800, "height", 1400,
                "direction", "down",
                "percent", 0.5
            ));
            if (!Boolean.TRUE.equals(canScrollMore)) {
                break; // reached the bottom of the form
            }
            pause(500);
        }
        List<WebElement> found = driver.findElements(testingTextbox);
        boolean visible = !found.isEmpty() && found.get(0).isDisplayed();
        System.out.println("TestingTextbox visible = " + visible);
        return visible;
    }

    // Polls until the textbox reaches the expected state. The lambda re-runs the full check on each try.
    public boolean waitForTestingTextboxVisibility(boolean expected) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(d -> isTestingTextboxVisible() == expected);
            System.out.println("TestingTextbox reached visible = " + expected);
            return true;
        } catch (TimeoutException e) {
            System.out.println("TestingTextbox did not become visible = " + expected + " in time");
            return false;
        }
    }

    // ---------- Tests 5 and 6: OpenPage / OpenCloseDailog ----------
    public void clickOpenPageButton() {
        clickFromTop(openPageButton, "OpenPage button");
    }

    public void clickOpenCloseDialogButton() {
        clickFromTop(openCloseDialogButton, "OpenCloseDailog button");
    }

    // The new page/dialog is open when its Gridtextclose button can be found.
    // scrollDownUntilVisible throws NoSuchElementException if it never appears, so we turn that into false.
    public boolean isCloseButtonAvailable() {
        try {
            scrollDownUntilVisible(closeButton);
            System.out.println("Gridtextclose is available, new page/dialog is open");
            return true;
        } catch (NoSuchElementException e) {
            System.out.println("Gridtextclose NOT found");
            return false;
        }
    }

    // On the new page/dialog: scroll down to Gridtextclose and click it.
    // For OpenPage this lands on the forms LIST; for the dialog it returns to the form.
    public void closeNewPageOrDialog() {
        clickAfterScrolling(closeButton, "Gridtextclose button");
    }

    // ---------- Test 7: Getdata ----------
    public void clickGetDataButton() {
        clickFromTop(getDataButton, "Getdata button");
    }

    // ---------- Tests 8 and 9: Formula / PostFormula ----------
    public void enterFormulaInput(String value) {
        scrollToTop();
        WebElement box = scrollDownUntilVisible(formulaTextbox);
        box.click();
        box.clear(); // an earlier test may have left a value in the field
        box.sendKeys(value);
        System.out.println("Typed into Testingformula: " + value);
        hideKeyboardIfShown();
    }

    public void clickFormulaButton() {
        clickFromTop(formulaButton, "Formula button");
    }

    public void clickPostFormulaButton() {
        clickFromTop(postFormulaButton, "PostFormula button");
    }

    // ---------- Shared value check (Getdata, Formula, PostFormula) ----------
    // textMatches needs the WHOLE text to match the regex. "([.]0)?" makes ".0" optional, so "50"
    // and "50.0" both pass. [.] is used instead of \. to avoid backslash escaping in the Java string.
    // We poll until the expected value shows up (not just "any value"), because a label can keep its old
    // result. Each round scrolls down once; at the bottom we jump back to the top and keep looking.
    public boolean waitForValueText(String expectedNumber, int seconds) {
        By valueLocator = AppiumBy.androidUIAutomator(
            "new UiSelector().textMatches(\"" + expectedNumber + "([.]0)?\")");
        scrollToTop();
        long endTime = System.currentTimeMillis() + seconds * 1000L;
        while (System.currentTimeMillis() < endTime) {
            List<WebElement> found = driver.findElements(valueLocator);
            if (!found.isEmpty() && found.get(0).isDisplayed()) {
                System.out.println("Value '" + found.get(0).getText() + "' is displayed (expected " + expectedNumber + ")");
                return true;
            }
            Boolean canScrollMore = (Boolean) driver.executeScript("mobile: scrollGesture", Map.of(
                "left", 100, "top", 300, "width", 800, "height", 1400,
                "direction", "down",
                "percent", 0.5
            ));
            if (!Boolean.TRUE.equals(canScrollMore)) {
                scrollToTop();
            }
            pause(1000);
        }
        System.out.println("Value " + expectedNumber + " was NOT displayed within " + seconds + " seconds");
        return false;
    }
 // The caption "LabelforFormula" and its value are sibling TextViews in the same parent.
 // following-sibling::...[1] means "the first TextView after the caption at the same level".
 // Using the caption means we read the label's value whatever it is, even a wrong one.
 private final By formulaCaption = AppiumBy.xpath(
     "//android.widget.TextView[@text='LabelforFormula']");
 private final By formulaValue = AppiumBy.xpath(
     "//android.widget.TextView[@text='LabelforFormula']/following-sibling::android.widget.TextView[1]");

 // Scrolls to the caption (always present), then polls the value WITHOUT scrolling.
 // matches() needs the WHOLE text to match the regex, so "45([.]0)?" accepts "45" and "45.0".
 // We print the label every time it changes, so a failure shows what the app actually displayed.
 public boolean waitForFormulaValue(String expectedNumber, int seconds) {
     scrollToTop();
     try {
         scrollDownUntilVisible(formulaCaption);
     } catch (NoSuchElementException e) {
         System.out.println("LabelforFormula caption not found on the form");
         return false;
     }
     String lastText = null;
     long endTime = System.currentTimeMillis() + seconds * 1000L;
     while (System.currentTimeMillis() < endTime) {
         List<WebElement> found = driver.findElements(formulaValue);
         if (!found.isEmpty()) {
             String text = found.get(0).getText();
             if (!text.equals(lastText)) {
                 System.out.println("LabelforFormula value = '" + text + "' (expected " + expectedNumber + ")");
                 lastText = text;
             }
             if (text.matches(expectedNumber + "([.]0)?")) {
                 return true;
             }
         }
         pause(1000);
     }
     System.out.println("LabelforFormula never showed " + expectedNumber + ", last value = '" + lastText + "'");
     return false;
 }
}