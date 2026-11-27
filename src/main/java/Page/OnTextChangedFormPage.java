package Page;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.pagefactory.AndroidFindBy;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class OnTextChangedFormPage extends BasePage {

    private final FormsPage formsPage;

    private static final String FORM_NAME = "ONTEXTCHANGEDFORM";
    // Toolbar title of this form (visible in Inspector). Used to know "are we on the form?".
    // It stays visible even when the keyboard is open, unlike ButtonTesting.
    private static final String FORM_TITLE = "OnTextChangedForm";

    // EditText order on screen (from Inspector): 0 = InActive, 1 = Active, 2 = OpenPage ...
    private static final int INACTIVE_INDEX = 0;
    private static final int ACTIVE_INDEX = 1;
    private static final int OPENPAGE_INDEX = 2;
    private static final int OPENDIALOG_INDEX = 3;   // verify in Inspector
    // OpenDailog textbox - located by text as you gave it
    @AndroidFindBy(uiAutomator = "new UiSelector().text(\"OpenDailog\")")
    private WebElement openDialogTextbox;

    
    // Clicking it moves focus away and fires the text-change event.
    @AndroidFindBy(uiAutomator = "new UiSelector().className(\"android.widget.EditText\").instance(1)")
    private WebElement nextTextbox;

    // Checkbox inside the dialog - clicking it closes the dialog
    private final By dialogCloseCheckbox = AppiumBy.androidUIAutomator(
            "new UiScrollable(new UiSelector().scrollable(true))"
            + ".scrollIntoView(new UiSelector().className(\"android.widget.CheckBox\").instance(2))");

    public OnTextChangedFormPage(AndroidDriver driver) {
        super(driver);
        this.formsPage = new FormsPage(driver);
    }

    // ---------- Locators ----------

    // The hint text (InActive, Active ...) is replaced once you type, so text() locators would
    // stop matching. instance(n) does not change when text is typed, so we use it for textboxes.
    private By editTextBy(int index) {
        return AppiumBy.androidUIAutomator(
            "new UiSelector().className(\"android.widget.EditText\").instance(" + index + ")");
    }

    // The button's text never changes, so a text locator is safe here.
    private By buttonTestingBy() {
        return AppiumBy.androidUIAutomator("new UiSelector().text(\"ButtonTesting\")");
    }

    private By formTitleBy() {
        return AppiumBy.androidUIAutomator("new UiSelector().text(\"" + FORM_TITLE + "\")");
    }

    // ---------- Navigation ----------

    public void openForm() {
        System.out.println("Selecting " + FORM_NAME + " from forms grid");
        formsPage.selectForm(FORM_NAME);
    }

    // ---------- Typing and focus ----------

    // Focuses the textbox, then types with REAL key events. sendKeys() sets text directly and
    // this app's text-change event did not react to it (confirmed by the probe run).
    // The keyboard stays open: the event needs focus to stay until the test moves it.
    private void typeInto(int index, String boxName, String text) {
        System.out.println("Waiting for " + boxName + " textbox");
        WebElement box = new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.elementToBeClickable(editTextBy(index)));
        System.out.println("Clicking " + boxName + " textbox to focus it");
        box.click();
        System.out.println("Typing '" + text + "' into " + boxName + " textbox using key events");
        for (char c : text.toCharArray()) {
            pressCharKey(c);
        }
    }

    // AndroidKey constants are named A..Z and DIGIT_0..DIGIT_9.
    // Supports lowercase letters and digits only.
    private void pressCharKey(char c) {
        AndroidKey key;
        if (c >= 'a' && c <= 'z') {
            key = AndroidKey.valueOf(String.valueOf(Character.toUpperCase(c)));
        } else if (c >= '0' && c <= '9') {
            key = AndroidKey.valueOf("DIGIT_" + c);
        } else {
            throw new IllegalArgumentException("pressCharKey supports only a-z and 0-9, got: '" + c + "'");
        }
        driver.pressKey(new KeyEvent(key));
    }

    // The probe showed this is the missing step: Enter commits the typed text to the app.
    public void pressEnter() {
        System.out.println("Pressing Enter key");
        driver.pressKey(new KeyEvent(AndroidKey.ENTER));
    }

    // Tapping another textbox moves focus away. Then we close the keyboard so it cannot cover the button.
    private void clickTextbox(int index, String boxName) {
        System.out.println("Waiting for " + boxName + " textbox to click");
        WebElement box = new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.elementToBeClickable(editTextBy(index)));
        System.out.println("Clicking " + boxName + " textbox (focus moves here)");
        box.click();
        // hideKeyboard() throws if no keyboard is open, so catch it.
        try {
            driver.hideKeyboard();
        } catch (Exception e) {
            System.out.println("Keyboard was not open");
        }
    }

    public void typeIntoInActiveTextbox(String text)  { typeInto(INACTIVE_INDEX, "InActive", text); }
    public void typeIntoActiveTextbox(String text)    { typeInto(ACTIVE_INDEX, "Active", text); }
    public void typeIntoOpenPageTextbox(String text)  { typeInto(OPENPAGE_INDEX, "OpenPage", text); }

    public void clickInActiveTextbox()  { clickTextbox(INACTIVE_INDEX, "InActive"); }
    public void clickActiveTextbox()    { clickTextbox(ACTIVE_INDEX, "Active"); }

    // THEORY (from the probe run): on a freshly opened form the first focus change does not
    // fire the text-change event; it only fired once some focus movement had already happened.
    // Tapping two textboxes without typing creates that focus history before the real flow.
    

    private void typeInto(By locator, String boxName, String text) {
        System.out.println("Waiting for " + boxName + " textbox");
        WebElement box = new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.elementToBeClickable(locator));
        System.out.println("Clicking " + boxName + " textbox to focus it");
        box.click();
        System.out.println("Typing '" + text + "' into " + boxName + " textbox using key events");
        for (char c : text.toCharArray()) {
            pressCharKey(c);
        }
    }
    public void primeFocusHistory() {
        System.out.println("Priming form: tapping Active then InActive (no typing) to create focus history");
        clickTextbox(ACTIVE_INDEX, "Active");
        clickTextbox(INACTIVE_INDEX, "InActive");
    }

    // ---------- ButtonTesting ----------

    public boolean isButtonTestingEnabled() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.ignoring(StaleElementReferenceException.class);
        boolean enabled = wait.until(ExpectedConditions.presenceOfElementLocated(buttonTestingBy())).isEnabled();
        System.out.println("ButtonTesting enabled state: " + enabled);
        return enabled;
    }

    // Polls until the enabled state matches. findElements returns an empty list (no exception)
    // when the button is covered by the keyboard, so we simply keep polling.
    public void waitForButtonTestingEnabled(boolean expected) {
        System.out.println("Waiting for ButtonTesting enabled = " + expected);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.ignoring(StaleElementReferenceException.class);
        wait.until(d -> {
            List<WebElement> found = d.findElements(buttonTestingBy());
            return !found.isEmpty() && found.get(0).isEnabled() == expected;
        });
        System.out.println("ButtonTesting enabled is now " + expected);
    }

    // ---------- Form state (same idea as OnLabelClickTestingFormPage) ----------
    private By hideTextboxBy() {
        return AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"com.mobi.dds_iuv:id/textInputLayout\").instance(4)");
    }

    // Show is located by its hint text. This works only while the field is empty,
    // because typing replaces the hint. Each test types into it once, so it is safe here.
    private By showTextboxBy() {
        return AppiumBy.androidUIAutomator("new UiSelector().text(\"Show\")");
    }

    public void typeIntoHideTextbox(String text) { typeInto(hideTextboxBy(), "Hide", text); }
    public void typeIntoShowTextbox(String text) { typeInto(showTextboxBy(), "Show", text); }
    public boolean isFormLoaded(int timeoutSeconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.presenceOfElementLocated(formTitleBy()));
            System.out.println("Form loaded: title '" + FORM_TITLE + "' is present");
            return true;
        } catch (TimeoutException e) {
            System.out.println("Form NOT loaded within " + timeoutSeconds + "s");
            return false;
        }
    }

    // Re-opens the form from the grid only when we are not already on it.
    public void reopenFormIfNeeded() {
        if (!isFormLoaded(3)) {
            formsPage.selectForm(FORM_NAME);
            System.out.println("Re-selected " + FORM_NAME);
        }
    }

    // Returns true if the form disappeared (the page opened by OpenPage is showing).
    public boolean waitForFormToDisappear(int timeoutSeconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.invisibilityOfElementLocated(formTitleBy()));
            System.out.println("Form left the screen: new page is showing");
            return true;
        } catch (TimeoutException e) {
            System.out.println("Form still on screen after " + timeoutSeconds + "s");
            return false;
        }
    }
    

    public void enterTextInOpenDialogField(String text) {
        openDialogTextbox.click();
        openDialogTextbox.sendKeys(text);
        System.out.println("Entered text in OpenDailog textbox: " + text);
    }

    public void clickNextTextbox() {
        nextTextbox.click();
        System.out.println("Clicked next textbox - text-change event should fire and dialog should open");
    }

    // The dialog appears after a few ms and its checkbox is below the fold, so we retry the scroll
    // every second until it is found. A plain wait won't help, because scrollIntoView throws immediately
    // if the dialog has not rendered yet.
    public boolean isOpenDialogDisplayed(int timeoutSeconds) {
        long end = System.currentTimeMillis() + timeoutSeconds * 1000L;
        while (System.currentTimeMillis() < end) {
            try {
                driver.findElement(dialogCloseCheckbox);
                System.out.println("Dialog opened - close checkbox found after scrolling");
                return true;
            } catch (org.openqa.selenium.NoSuchElementException e) {
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            }
        }
        System.out.println("Dialog did NOT open within " + timeoutSeconds + "s");
        return false;
    }

    public void closeOpenDialogViaCheckbox() {
        driver.findElement(dialogCloseCheckbox).click();
        System.out.println("Clicked checkbox inside dialog to close it");
    }

    // Dialog is gone when its checkbox disappears, and the main form's first textbox is back
    public boolean isBackOnMainFormAfterDialogClose(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.EditText\").instance(0)")));
            System.out.println("Dialog closed - back on main form");
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            System.out.println("Did NOT return to main form within " + timeoutSeconds + "s");
            return false;
        }
    }
    
    public void typeIntoOpenDialogTextbox(String text) { typeInto(OPENDIALOG_INDEX, "OpenDialog", text); }

 // Plain locator (no UiScrollable), used only to check that the dialog is gone.
 // A scroll-based locator would throw when the element no longer exists.
 private By dialogCheckboxPlainBy() {
     return AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.CheckBox\")");
 }

 // The dialog appears a few ms after the event and its checkbox is below the fold.
 // scrollIntoView throws immediately if the dialog hasn't rendered, so retry every second.
 public boolean waitForDialogToOpen(int timeoutSeconds) {
     long end = System.currentTimeMillis() + timeoutSeconds * 1000L;
     while (System.currentTimeMillis() < end) {
         try {
             driver.findElement(dialogCloseCheckbox);
             System.out.println("Dialog opened - close checkbox found after scrolling");
             return true;
         } catch (org.openqa.selenium.NoSuchElementException e) {
             try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
         }
     }
     System.out.println("Dialog did NOT open within " + timeoutSeconds + "s");
     return false;
 }

 public void closeDialogViaCheckbox() {
     driver.findElement(dialogCloseCheckbox).click();
     System.out.println("Clicked checkbox inside dialog to close it");
 }

 // invisibilityOfElementLocated returns true when the element is absent OR hidden,
 // so it works as "the dialog is gone".
 public boolean waitForDialogToClose(int timeoutSeconds) {
     try {
         new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
             .until(ExpectedConditions.invisibilityOfElementLocated(dialogCheckboxPlainBy()));
         System.out.println("Dialog closed");
         return true;
     } catch (TimeoutException e) {
         System.out.println("Dialog still open after " + timeoutSeconds + "s");
         return false;
     }
 }
//findElements returns an empty list (no exception) when the element is absent, so it is safe
//for "is it gone?" checks. isDisplayed() covers the case where the app keeps it in the
//hierarchy but invisible.
public boolean isButtonTestingVisible() {
  List<WebElement> found = driver.findElements(buttonTestingBy());
  boolean visible = !found.isEmpty() && found.get(0).isDisplayed();
  System.out.println("ButtonTesting visible state: " + visible);
  return visible;
}

public void waitForButtonTestingVisible(boolean expected) {
  System.out.println("Waiting for ButtonTesting visible = " + expected);
  WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  wait.ignoring(StaleElementReferenceException.class);
  wait.until(d -> {
      List<WebElement> found = d.findElements(buttonTestingBy());
      boolean visible = !found.isEmpty() && found.get(0).isDisplayed();
      return visible == expected;
  });
  System.out.println("ButtonTesting visible is now " + expected);
}
//Textbox locators - same textInputLayout container pattern as Hide (instance 4).
//Sequence from Inspector: Hide 4, Show 5, Formula 6, Postformula 7, Testingformula 8, Getdata 9.
//Postformula = 7 is inferred from that sequence. Please confirm it in Inspector.
private By formulaTextboxBy() {
 return AppiumBy.androidUIAutomator(
     "new UiSelector().resourceId(\"com.mobi.dds_iuv:id/textInputLayout\").instance(6)");
}
private By postformulaTextboxBy() {
 return AppiumBy.androidUIAutomator(
     "new UiSelector().resourceId(\"com.mobi.dds_iuv:id/textInputLayout\").instance(7)");
}
private By getdataTextboxBy() {
 return AppiumBy.androidUIAutomator(
     "new UiSelector().resourceId(\"com.mobi.dds_iuv:id/textInputLayout\").instance(9)");
}

//Result locators. These texts are fixed and the app writes them itself, so text() is safe.
private By labelformulaLabelBy() { return AppiumBy.androidUIAutomator("new UiSelector().text(\"Labelformula\")"); }
private By labelformulaValueBy() { return AppiumBy.androidUIAutomator("new UiSelector().text(\"50.0\")"); }
private By getdataValueBy()      { return AppiumBy.androidUIAutomator("new UiSelector().text(\"50\")"); }

public void typeIntoFormulaTextbox(String text)     { typeInto(formulaTextboxBy(), "Formula", text); }
public void typeIntoPostformulaTextbox(String text) { typeInto(postformulaTextboxBy(), "Postformula", text); }
public void typeIntoGetdataTextbox(String text)     { typeInto(getdataTextboxBy(), "Getdata", text); }

//Generic "is this element on screen within N seconds" check. Returns true/false instead of throwing,
//so the test can use it for both the "not yet shown" precondition and the final check.
private boolean isLocatorPresent(By locator, String name, int timeoutSeconds) {
 try {
     new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
         .until(ExpectedConditions.presenceOfElementLocated(locator));
     System.out.println(name + " is displayed");
     return true;
 } catch (TimeoutException e) {
     System.out.println(name + " NOT displayed within " + timeoutSeconds + "s");
     return false;
 }
}

public boolean isLabelformulaLabelDisplayed(int timeoutSeconds) {
 return isLocatorPresent(labelformulaLabelBy(), "Labelformula label", timeoutSeconds);
}
public boolean isLabelformulaValueDisplayed(int timeoutSeconds) {
 return isLocatorPresent(labelformulaValueBy(), "Labelformula value 50.0", timeoutSeconds);
}
public boolean isGetdataValueDisplayed(int timeoutSeconds) {
 return isLocatorPresent(getdataValueBy(), "GetdataValue 50", timeoutSeconds);
}

//Resets form state: back lands on the forms list, then the form is selected again.
public void reopenFormFresh() {
 System.out.println("Re-opening form to reset the Labelformula value");
 navigateBackFromForm(); // inherited from BasePage
 reopenFormIfNeeded();
}
//imports to add:
//Brightness (0-255) of the ButtonTesting area when the button is ACTIVE. Captured once after the form opens.
private double buttonBaselineBrightness = -1;

//How much lighter than the baseline counts as "inactive". The button is dark navy when active
//and visibly light when disabled, so a difference of 40 is a safe margin.
private static final double LIGHT_THRESHOLD = 40;

//Takes a screenshot of just the button and returns the average brightness of its pixels.
//A dark button gives a low number and a light (disabled) button gives a higher one.
private double getButtonTestingBrightness() {
 try {
     WebElement button = driver.findElement(buttonTestingBy());
     byte[] png = button.getScreenshotAs(OutputType.BYTES);
     BufferedImage img = ImageIO.read(new ByteArrayInputStream(png));
     long sum = 0;
     for (int x = 0; x < img.getWidth(); x++) {
         for (int y = 0; y < img.getHeight(); y++) {
             int rgb = img.getRGB(x, y);
             int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
             sum += (r + g + b) / 3;
         }
     }
     return (double) sum / ((long) img.getWidth() * img.getHeight());
 } catch (Exception e) {
     System.out.println("Could not measure ButtonTesting brightness: " + e.getMessage());
     return -1;
 }
}

//Call once while the button is active (right after the form opens) to learn what "active" looks like.
public void captureButtonBaseline() {
 buttonBaselineBrightness = getButtonTestingBrightness();
 System.out.println("ButtonTesting baseline (active) brightness = " + buttonBaselineBrightness);
}

public boolean isButtonTestingLight() {
 double now = getButtonTestingBrightness();
 boolean light = now >= 0 && now > buttonBaselineBrightness + LIGHT_THRESHOLD;
 System.out.println("ButtonTesting brightness = " + now + " (baseline " + buttonBaselineBrightness
     + ") -> light/inactive = " + light);
 return light;
}

//Polls until the button looks light (inactive) or dark (active). Returns true/false so the test can assert.
public boolean waitForButtonTestingLight(boolean expectedLight, int timeoutSeconds) {
 System.out.println("Waiting for ButtonTesting light/inactive = " + expectedLight);
 long end = System.currentTimeMillis() + timeoutSeconds * 1000L;
 while (System.currentTimeMillis() < end) {
     double now = getButtonTestingBrightness();
     if (now >= 0 && (now > buttonBaselineBrightness + LIGHT_THRESHOLD) == expectedLight) {
         System.out.println("ButtonTesting light/inactive is now " + expectedLight);
         return true;
     }
     try { Thread.sleep(500); } catch (InterruptedException ignored) {}
 }
 System.out.println("ButtonTesting did NOT reach light/inactive = " + expectedLight + " within " + timeoutSeconds + "s");
 return false;
}

//Diagnostic: shows which attributes the app really changes when the button goes inactive.
public void logButtonTestingAttributes(String when) {
 List<WebElement> found = driver.findElements(buttonTestingBy());
 if (found.isEmpty()) {
     System.out.println("[" + when + "] ButtonTesting not found");
     return;
 }
 WebElement b = found.get(0);
 System.out.println("[" + when + "] ButtonTesting enabled=" + b.getAttribute("enabled")
     + " | clickable=" + b.getAttribute("clickable")
     + " | focusable=" + b.getAttribute("focusable"));
}
//Button "inactive" mana jayega agar enabled attribute false ho YA colour light ho jaye.
//Pehle sirf brightness dekh rahe the, ab dono signals check hote hain.
public boolean isButtonTestingInactive() {
 List<WebElement> found = driver.findElements(buttonTestingBy());
 if (found.isEmpty()) {
     System.out.println("ButtonTesting not found on screen");
     return false;
 }
 // getAttribute() hamesha String deta hai ("true"/"false"), isliye parseBoolean
 boolean enabled = Boolean.parseBoolean(found.get(0).getAttribute("enabled"));
 double now = getButtonTestingBrightness();
 boolean lightByColor = now >= 0 && now > buttonBaselineBrightness + LIGHT_THRESHOLD;
 boolean inactive = !enabled || lightByColor;
 System.out.println("Poll -> enabled=" + enabled
         + " | brightness=" + now + " (baseline " + buttonBaselineBrightness + ")"
         + " | lightByColor=" + lightByColor + " | inactive=" + inactive);
 return inactive;
}

public boolean waitForButtonTestingInactive(boolean expectedInactive, int timeoutSeconds) {
 System.out.println("Waiting for ButtonTesting inactive = " + expectedInactive);
 long end = System.currentTimeMillis() + timeoutSeconds * 1000L;
 while (System.currentTimeMillis() < end) {
     if (isButtonTestingInactive() == expectedInactive) {
         System.out.println("ButtonTesting inactive is now " + expectedInactive);
         return true;
     }
     try { Thread.sleep(500); } catch (InterruptedException ignored) {}
 }
 System.out.println("ButtonTesting did NOT reach inactive = " + expectedInactive + " within " + timeoutSeconds + "s");
 return false;
}

//Enter ke baad keyboard band karne ke liye (tap sahi field pe lage)
public void hideKeyboardIfOpen() {
 try {
     driver.hideKeyboard();
     System.out.println("Keyboard hidden");
 } catch (Exception e) {
     System.out.println("Keyboard was not open");
 }
}

//Test 2 ko independent banane ke liye: fresh form + naya baseline
public void resetFormAndCaptureBaseline() {
 System.out.println("Resetting form so the test does not depend on previous test state");
 reopenFormFresh();
 isFormLoaded(10);
 captureButtonBaseline();
}

//Test 2 ka precondition: button ko khud inactive karo
public void disableButtonViaInActiveTextbox() {
 System.out.println("Setting precondition: making ButtonTesting inactive via InActive textbox");
 primeFocusHistory();
 typeIntoInActiveTextbox("inactive");
 pressEnter();
 hideKeyboardIfOpen();
 clickActiveTextbox();
}
}