package Page;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.TimeoutException;
import java.time.Duration;
import java.util.List;

public class ONButtonClickEventFormPage extends BasePage {

    public ONButtonClickEventFormPage(AndroidDriver driver) {
        super(driver);
    }

    // ---------- Generic helpers ----------

    // Explicit wait: polls every ~500ms until the element exists or 10s pass.
    // More reliable than a fixed sleep, and it fixes "button not found" when the form is still loading.
    private WebElement getButtonByText(String text) {
        System.out.println("Waiting for button: " + text);
        return new WebDriverWait(driver, Duration.ofSeconds(10)).until(
            ExpectedConditions.presenceOfElementLocated(AppiumBy.androidUIAutomator(
                "new UiSelector().text(\"" + text + "\")")));
    }

    // instance(n) is 0-based: "the nth EditText on screen".
    // Unlike text("..."), it does not change when the test types into the field.
    private WebElement getEditTextByIndex(int index) {
        System.out.println("Locating EditText at index " + index);
        return driver.findElement(AppiumBy.androidUIAutomator(
            "new UiSelector().className(\"android.widget.EditText\").instance(" + index + ")"));
    }

    // The value TextView has no resource-id, so find its caption first,
    // then jump to the next sibling TextView (following-sibling::...[1]).
    private By valueByCaption(String caption) {
        return By.xpath("//android.widget.TextView[@resource-id='com.mobi.dds_iuv:id/textView1_horizontal_caption' and @text='"
            + caption + "']/following-sibling::android.widget.TextView[1]");
    }

    // ---------- Debug helper (remove once the order is confirmed) ----------

    public void printEditTexts() {
        // findElements returns an empty list instead of throwing when nothing matches
        List<WebElement> fields = driver.findElements(AppiumBy.className("android.widget.EditText"));
        System.out.println("EditText count: " + fields.size());
        for (int i = 0; i < fields.size(); i++) {
            System.out.println("EditText[" + i + "] text='" + fields.get(i).getText()
                + "' enabled=" + fields.get(i).isEnabled());
        }
    }

    // ---------- Active / InActive / Textbox ----------

    public void clickActiveButton() {
        WebElement btn = getButtonByText("ActiveButton");
        System.out.println("Clicking ActiveButton");
        btn.click();
    }

    public void clickInActiveButton() {
        WebElement btn = getButtonByText("InActive");
        System.out.println("Clicking InActive button");
        btn.click();
    }

    // ASSUMPTION: first EditText = TextboxTesting, second = FormulaTesting.
    // Confirm with printEditTexts(); if reversed, swap 0 and 1.
    public WebElement getTextboxTesting() {
        return getEditTextByIndex(0);
    }

    public WebElement getFormulaTesting() {
        return getEditTextByIndex(1);
    }

    public boolean isTextboxTestingEnabled() {
        boolean enabled = getTextboxTesting().isEnabled();
        System.out.println("TextboxTesting enabled state: " + enabled);
        return enabled;
    }

    public void enterTextIntoTextbox(String text) {
        WebElement textbox = getTextboxTesting();
        System.out.println("Attempting to enter text: " + text);
        textbox.sendKeys(text);
    }

    public String getTextboxValue() {
        String value = getTextboxTesting().getText();
        System.out.println("Current textbox value: " + value);
        return value;
    }

    // ---------- OpenPage / Dialog ----------

    public void clickOpenPageButton() {
        WebElement btn = getButtonByText("OpenPage");
        System.out.println("Clicking OpenPage button - new screen expected to flash open and auto-close");
        btn.click();
    }

    public void clickOpenAndCloseDialogButton() {
        WebElement btn = getButtonByText("OpenAndCloseDailog");
        System.out.println("Clicking OpenAndCloseDailog button - dialog expected to appear");
        btn.click();
    }

    // TODO: "Checkchangeclosedailog" is still a guess. Confirm the real close-button text in Inspector.
    public boolean isDialogOpen() {
        boolean open = driver.findElements(AppiumBy.androidUIAutomator(
            "new UiSelector().text(\"Checkchangeclosedailog\")")).size() > 0;
        System.out.println("Dialog open: " + open);
        return open;
    }

    public boolean isFormulavalueVisible() {
        boolean visible = !driver.findElements(valueByCaption("Formulavalue")).isEmpty();
        System.out.println("Formulavalue field visible: " + visible);
        return visible;
    }

    // Polls until the field appears (true) or disappears (false), so a slow UI update
    // after Hide/Show doesn't cause a false failure.
    public void waitForFormulaValueVisible(boolean shouldBeVisible) {
        System.out.println("Waiting for Formulavalue visible = " + shouldBeVisible);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        if (shouldBeVisible) {
            wait.until(ExpectedConditions.presenceOfElementLocated(valueByCaption("Formulavalue")));
        } else {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(valueByCaption("Formulavalue")));
        }
    }

    public void clickHideButton() {
        WebElement btn = getButtonByText("HIdebutton");
        System.out.println("Clicking HIdebutton");
        btn.click();
    }

    public void clickShowButton() {
        WebElement btn = getButtonByText("ShowButton");
        System.out.println("Clicking ShowButton");
        btn.click();
    }

    // ---------- Formula / Postformula / GetData ----------

    public void enterFormulaTestingValue(String value) {
        WebElement field = getFormulaTesting();
        field.clear();
        System.out.println("Entering value into FormulaTesting: " + value);
        field.sendKeys(value);
        // The keyboard covers the lower screen (Formula button is at y=1401-1545).
        // hideKeyboard() throws if no keyboard is open, so catch it.
        try {
            driver.hideKeyboard();
        } catch (Exception e) {
            System.out.println("Keyboard was not open");
        }
    }

    public void clickFormulaButton() {
        WebElement btn = getButtonByText("Formula");
        System.out.println("Clicking Formula button - expecting entered value + 60");
        btn.click();
    }

    public void clickPostFormulaButton() {
        WebElement btn = getButtonByText("Postformula");
        System.out.println("Clicking Postformula button - expecting entered value + 60");
        btn.click();
    }

    public void clickGetDataButton() {
        WebElement btn = getButtonByText("GetData");
        System.out.println("Clicking GetData button - expecting value 30 to display");
        btn.click();
    }

    // Returns the text of the value TextView (for example "70.0"), not the caption.
    public String getFormulaResultValue() {
        String value = driver.findElement(valueByCaption("Formulavalue")).getText();
        System.out.println("Formula result displayed: " + value);
        return value;
    }
    
 // Dialog detection copied from your working form: the X button has a real resource-id.
 // Explicit wait = poll until the element appears or the timeout passes.
 public boolean isDialogOpen(int timeoutSeconds) {
     try {
         new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds)).until(
             ExpectedConditions.presenceOfElementLocated(AppiumBy.id("com.mobi.dds_iuv:id/imgbtn_close")));
         System.out.println("Dialog opened successfully");
         return true;
     } catch (TimeoutException e) {
         System.out.println("Dialog did NOT open within " + timeoutSeconds + "s");
         return false;
     }
 }

 public void closeDialog() {
     System.out.println("Clicking close (X) button on dialog");
     driver.findElement(AppiumBy.id("com.mobi.dds_iuv:id/imgbtn_close")).click();
 }

 // invisibilityOfElementLocated also returns true when the element is gone from the tree
 public boolean isDialogClosed(int timeoutSeconds) {
     try {
         new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds)).until(
             ExpectedConditions.invisibilityOfElementLocated(AppiumBy.id("com.mobi.dds_iuv:id/imgbtn_close")));
         System.out.println("Dialog closed successfully");
         return true;
     } catch (TimeoutException e) {
         System.out.println("Dialog did NOT close within " + timeoutSeconds + "s");
         return false;
     }
 }

 // Real assertion for test 3: proves the form is on screen again after OpenPage
 public boolean isFormLoaded(int timeoutSeconds) {
     try {
         new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds)).until(
             ExpectedConditions.presenceOfElementLocated(AppiumBy.androidUIAutomator(
                 "new UiSelector().text(\"ActiveButton\")")));
         System.out.println("Form loaded: ActiveButton is present");
         return true;
     } catch (TimeoutException e) {
         System.out.println("Form NOT loaded within " + timeoutSeconds + "s");
         return false;
     }
 }

 // For GetData. className("android.widget.TextView") matters: test 8 leaves "30" typed in the
 // FormulaTesting EditText, and a plain text("30") would match that field and pass falsely.
//add these imports at the top of the page class:
//import org.openqa.selenium.NoSuchElementException;

public boolean isValueTextDisplayed(String expectedText, int timeoutSeconds) {
  System.out.println("Looking for TextView '" + expectedText + "' (with scroll), timeout " + timeoutSeconds + "s");
  try {
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
      // ignoring() tells the wait to keep polling when scrollIntoView throws
      // NoSuchElementException, instead of failing on the first attempt
      wait.ignoring(NoSuchElementException.class);

      wait.until(d -> {
          // scrollIntoView first checks if the element is already on screen.
          // If not, it scrolls the container to the top, then swipes down step by step
          // until the element appears. Once found, it returns the element.
          WebElement el = d.findElement(AppiumBy.androidUIAutomator(
              "new UiScrollable(new UiSelector().scrollable(true))"
              + ".scrollIntoView(new UiSelector().className(\"android.widget.TextView\")"
              + ".text(\"" + expectedText + "\"))"));
          System.out.println("Found TextView with text: " + el.getText());
          return el;
      });

      System.out.println("Confirmed TextView shows: " + expectedText);
      return true;
  } catch (TimeoutException e) {
      System.out.println("TextView '" + expectedText + "' NOT found within " + timeoutSeconds + "s");
      return false;
  }
}
 
//Counts EditTexts on screen. findElements returns an empty list instead of throwing,
//and the count doesn't depend on what text is typed into the fields.
//Shown = 2 (TextboxTesting + FormulaTesting), hidden = 1.
public int getEditTextCount() {
  int count = driver.findElements(AppiumBy.className("android.widget.EditText")).size();
  System.out.println("EditText count on screen: " + count);
  return count;
}

public boolean isFormulaTestingVisible() {
  boolean visible = getEditTextCount() >= 2;
  System.out.println("FormulaTesting textbox visible: " + visible);
  return visible;
}

//Explicit wait with a lambda: polls every 500ms until the count matches or 10s pass.
//Needed because the UI takes a moment to update after Hide/Show.
public void waitForFormulaTestingVisible(boolean shouldBeVisible) {
  int expectedCount = shouldBeVisible ? 2 : 1;
  System.out.println("Waiting for EditText count = " + expectedCount);
  new WebDriverWait(driver, Duration.ofSeconds(10)).until(
      d -> d.findElements(AppiumBy.className("android.widget.EditText")).size() == expectedCount);
}

//Closes the dialog page by scrolling down to the button and clicking it.
//UiScrollable.scrollIntoView() swipes the scrollable container until the target
//UiSelector is on screen, then returns that element.
public void closeDialogViaButton() {
  System.out.println("Scrolling to OnbuttonClickCloseDailog");
  WebElement closeBtn = driver.findElement(AppiumBy.androidUIAutomator(
      "new UiScrollable(new UiSelector().scrollable(true))"
      + ".scrollIntoView(new UiSelector().text(\"OnbuttonClickCloseDailog\"))"));
  System.out.println("Clicking OnbuttonClickCloseDailog to close dialog");
  closeBtn.click();
}

//Returns true if the form disappeared (OpenPage screen is showing),
//false if the form never went away (screen already auto-closed or never opened).
public boolean waitForFormToDisappear(int timeoutSeconds) {
 try {
     new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds)).until(
         ExpectedConditions.invisibilityOfElementLocated(AppiumBy.androidUIAutomator(
             "new UiSelector().text(\"ActiveButton\")")));
     System.out.println("Form left the screen: OpenPage screen is showing");
     return true;
 } catch (TimeoutException e) {
     System.out.println("Form still on screen after " + timeoutSeconds + "s: OpenPage screen did not stay open");
     return false;
 }
}
}