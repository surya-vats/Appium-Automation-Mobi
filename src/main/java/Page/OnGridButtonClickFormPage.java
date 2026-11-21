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
public class OnGridButtonClickFormPage extends BasePage {

    // ---------- Locators ----------
    // Every grid row has a hand icon with the SAME resource-id (blinkImg).
    // instance(n) picks the nth match, so 0/2/4/6 are the first-row icons of InActive/Active/OpenPage/OpenDialog.
    private final By inActiveIcon = AppiumBy.androidUIAutomator(
        "new UiSelector().resourceId(\"com.mobi.dds_iuv:id/blinkImg\").instance(0)");
    private final By activeIcon = AppiumBy.androidUIAutomator(
        "new UiSelector().resourceId(\"com.mobi.dds_iuv:id/blinkImg\").instance(2)");
    private final By openPageIcon = AppiumBy.androidUIAutomator(
        "new UiSelector().resourceId(\"com.mobi.dds_iuv:id/blinkImg\").instance(4)");
    private final By openDialogIcon = AppiumBy.androidUIAutomator(
        "new UiSelector().resourceId(\"com.mobi.dds_iuv:id/blinkImg\").instance(6)");

    // On the NEW page that OpenPage / OpenDialog opens, the closing grid's icon is instance(0).
    private final By closePageIcon = AppiumBy.androidUIAutomator(
        "new UiSelector().resourceId(\"com.mobi.dds_iuv:id/blinkImg\").instance(0)");

    private final By testingTextbox = AppiumBy.androidUIAutomator(
        "new UiSelector().text(\"TestingTextbox\")");

    // The "InActive" grid title exists only on this form (not on the page it opens), so it tells us
    // whether the form is on screen. It sits at the top, so scroll to top before checking.
    private final By formMarker = AppiumBy.xpath("//android.widget.TextView[@text='InActive']");
    
 private final By focusedTextbox = AppiumBy.androidUIAutomator(
		    "new UiSelector().className(\"android.widget.EditText\").focused(true)");
 
 
 private By gridFirstRowIcon(String gridTitle) {
	    return AppiumBy.xpath("(//android.widget.TextView[@text='" + gridTitle + "']"
	        + "/following-sibling::android.widget.ScrollView"
	        + "//android.widget.ImageView[@resource-id='com.mobi.dds_iuv:id/blinkImg'])[1]");
	}
 public void clickHideIcon()        { scrollToTop(); clickAfterScrolling(gridFirstRowIcon("Hide"), "Hide hand icon"); }
 public void clickShowIcon()        { scrollToTop(); clickAfterScrolling(gridFirstRowIcon("Show"), "Show hand icon"); }
 public void clickGetDataIcon()     { scrollToTop(); clickAfterScrolling(gridFirstRowIcon("Getdata"), "Getdata hand icon"); }
 public void clickFormulaIcon()     { scrollToTop(); clickAfterScrolling(gridFirstRowIcon("Formula"), "Formula hand icon"); }
 public void clickPostFormulaIcon() { scrollToTop(); clickAfterScrolling(gridFirstRowIcon("Postformula"), "Postformula hand icon"); }
    public OnGridButtonClickFormPage(AndroidDriver driver) {
        super(driver);
    }

    // ---------- Shared helpers ----------
    private void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Scrolls the whole screen up until it cannot scroll any more.
    // mobile: scrollGesture returns true while more scrolling is possible, so we loop until it returns false.
    // left/top/width/height is the swipe area in pixels; adjust if your screen needs it.
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
    // The short pause gives a freshly opened page time to render before the next check.
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
 // scrollGesture with "elementId" scrolls exactly that container, not a screen area.
 // presenceOfElementLocated also waits for the sheet to render before the first scroll.
 
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

    // ---------- Test 1: InActive ----------
    // Scroll to top first so instance(n) numbering always starts from the same screen position.
    public void clickInActiveIcon() {
        scrollToTop();
        clickAfterScrolling(inActiveIcon, "InActive hand icon");
    }

    public boolean isTestingTextboxEnabled() {
        WebElement box = scrollDownUntilVisible(testingTextbox);
        boolean enabled = box.isEnabled();
        System.out.println("TestingTextbox enabled = " + enabled);
        return enabled;
    }

    // ---------- Test 2: Active ----------
    public void clickActiveIcon() {
        scrollToTop(); // the Active icon is above the textbox, so scroll back up first
        clickAfterScrolling(activeIcon, "Active hand icon");
    }

    // Types into TestingTextbox and returns what the field shows.
    // We keep ONE WebElement for the whole method: if "TestingTextbox" is the field's hint, its text
    // changes once we type, and a second lookup by that text would fail.
    // clear() at the end brings the hint back so later tests can find the field by text again.
 
    public String typeIntoTestingTextbox(String text) {
        WebElement box = scrollDownUntilVisible(testingTextbox);
        box.click();          // field focus mein aa jaata hai
        box.sendKeys(text);

        // Ab text badal chuka hai, isliye focused EditText se dhundho
        WebElement typedBox = new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.presenceOfElementLocated(focusedTextbox));
        String value = typedBox.getText();
        System.out.println("Typed into TestingTextbox: " + text + ", field shows: " + value);

        typedBox.clear();     // hint wapas aa jaata hai, to text("TestingTextbox") phir kaam karega
        if (driver.isKeyboardShown()) {
            driver.hideKeyboard(); // keyboard agle scroll gesture ke beech mein na aaye
        }
        return value;
    }
    // ---------- Tests 3 and 4: OpenPage / OpenDialog ----------
    public void clickOpenPageIcon() {
        scrollToTop();
        clickAfterScrolling(openPageIcon, "OpenPage hand icon");
    }

    public void clickOpenDialogIcon() {
        scrollToTop();
        clickAfterScrolling(openDialogIcon, "OpenDialog hand icon");
    }

    // On the new page: scroll down to the closing grid and click its hand icon.
    // That closes the page and returns to the forms LIST (not the form itself).
    public void closeNewPage() {
        // Same helper that works on the form: scroll down until the icon is visible, then click it
        clickAfterScrolling(closePageIcon, "close hand icon on the new page");
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
            System.out.println("Form disappeared, new page is open");
            return true;
        } catch (TimeoutException e) {
            System.out.println("Form is still displayed");
            return false;
        }
    }
    
 // "Visible" = TestingTextbox is in the hierarchy AND displayed. When the app hides it, the element
 // is removed from the hierarchy, so findElements returns an empty list (no exception).
 // It sits far down the form, so we scroll from the top in steps and check after every scroll.
 // We only conclude "hidden" after reaching the bottom without finding it.
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

 // Polls until the textbox reaches the expected state. The app may need a moment to update the UI
 // after the click, and the lambda re-runs the full check on each try.
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

 // textMatches needs the WHOLE text to match the regex. "([.]0)?" makes ".0" optional, so "1000"
 // and "1000.0" both pass. [.] is used instead of \. to avoid backslash escaping in the Java string.
 // scrollDownUntilVisible throws NoSuchElementException if the value never appears, so we turn that into false.
 public boolean isValueTextDisplayed(String number) {
     By valueLocator = AppiumBy.androidUIAutomator(
         "new UiSelector().textMatches(\"" + number + "([.]0)?\")");
     try {
         scrollDownUntilVisible(valueLocator);
         System.out.println("Value " + number + " is displayed");
         return true;
     } catch (NoSuchElementException e) {
         System.out.println("Value " + number + " is NOT displayed");
         return false;
     }
 }
//Caption "Getdatalabel" and its value are two TextViews in the same parent. The value is empty
//until Getdata runs, so we scroll to it first, then poll its text WITHOUT scrolling.
private final By getDataValue = AppiumBy.xpath(
  "//android.widget.TextView[@text='Getdatalabel']/following-sibling::android.widget.TextView[1]");

public String getGetDataValueText(int seconds) {
  scrollToTop();
  try {
      scrollDownUntilVisible(getDataValue);
  } catch (NoSuchElementException e) {
      System.out.println("Getdatalabel value field not found on the form");
      return "";
  }
  long endTime = System.currentTimeMillis() + seconds * 1000L;
  String text = "";
  while (System.currentTimeMillis() < endTime) {
      List<WebElement> found = driver.findElements(getDataValue);
      if (!found.isEmpty()) {
          text = found.get(0).getText();
          if (!text.trim().isEmpty()) {
              break;
          }
      }
      pause(1000);
  }
  System.out.println("Getdatalabel value = '" + text + "'");
  return text;
}
//Located by the "hint" attribute, which stays "FormulaTesting" even after typing.
//A text("FormulaTesting") locator would break once the field holds a value.
private final By formulaTextbox = AppiumBy.xpath("//android.widget.EditText[@hint='FormulaTesting']");

//Same pattern as Getdatalabel: the caption and its value are sibling TextViews in the same parent.
private final By formulaValue = AppiumBy.xpath(
 "//android.widget.TextView[@text='Formulalabel']/following-sibling::android.widget.TextView[1]");

public void enterFormulaInput(String value) {
 WebElement box = scrollDownUntilVisible(formulaTextbox);
 box.click();
 box.clear();      // an earlier test may have left a value in the field
 box.sendKeys(value);
 System.out.println("Typed into FormulaTesting: " + value);
 // The keyboard covers part of the screen and can interfere with the scroll gestures that follow
 try {
     if (driver.isKeyboardShown()) {
         driver.hideKeyboard();
     }
 } catch (Exception e) {
     System.out.println("Could not hide keyboard: " + e.getMessage());
 }
}

//Polls the label until it matches the expected number, instead of reading it once.
//Waiting for a MATCH (not just "non-empty") matters here: Formulalabel keeps the previous
//result, so a non-empty check would pass immediately in the second test.
//"([.]0)?" accepts both "55" and "55.0".
public boolean waitForFormulaValue(String expectedNumber, int seconds) {
 try {
     scrollDownUntilVisible(formulaValue);
 } catch (NoSuchElementException e) {
     System.out.println("Formulalabel value field not found on the form");
     return false;
 }
 String text = "";
 long endTime = System.currentTimeMillis() + seconds * 1000L;
 while (System.currentTimeMillis() < endTime) {
     List<WebElement> found = driver.findElements(formulaValue);
     if (!found.isEmpty()) {
         text = found.get(0).getText();
         if (text.matches(expectedNumber + "([.]0)?")) {
             System.out.println("Formulalabel value = '" + text + "' (expected " + expectedNumber + ")");
             return true;
         }
     }
     pause(1000);
 }
 System.out.println("Formulalabel value = '" + text + "', expected " + expectedNumber);
 return false;
}
}