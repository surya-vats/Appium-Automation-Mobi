package Page;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class CheckChangeEventFormPage extends BasePage {

    // No resource-id exists on this element (confirmed via Appium Inspector),
    // so it's located by class + position using UiAutomator. instance(0) = first
    // CheckBox in the hierarchy = ActiveInactive (only checkbox on this screen).
    @AndroidFindBy(uiAutomator = "new UiSelector().className(\"android.widget.CheckBox\").instance(0)")
    private WebElement activeInactiveCheckbox;

    // Only one EditText on this screen, so instance(0) is safe. Avoided
    // .text("TextboxTesting") since typing into the field changes its text,
    // which would break a text-based locator on the next lookup.
    @AndroidFindBy(uiAutomator = "new UiSelector().className(\"android.widget.EditText\").instance(0)")
    private WebElement remarksTextbox;

    public CheckChangeEventFormPage(AndroidDriver driver) {
        super(driver);
        // BasePage's constructor calls PageFactory.initElements(new AppiumFieldDecorator(driver), this),
        // which proxies these fields — each is re-located fresh from the @AndroidFindBy locator
        // every time a method touches it, not cached once at object creation.
    }

    public void toggleActiveInactive() {
        activeInactiveCheckbox.click();
        try {
            Thread.sleep(900); // UI ko checkbox state update karne ka time dena
        } catch (InterruptedException ignored) {}
        System.out.println("Clicked ActiveInactive checkbox. checked=" + activeInactiveCheckbox.isSelected());
    }

    public boolean isCheckboxChecked() {
        // Primary check via isSelected()
        boolean state = activeInactiveCheckbox.isSelected();
        // Fallback: some custom widgets don't update isSelected() reliably,
        // so also check the raw "checked" attribute directly from the accessibility tree
        String checkedAttr = activeInactiveCheckbox.getAttribute("checked");
        System.out.println("isSelected()=" + state + " | checked attribute=" + checkedAttr);
        return state || "true".equals(checkedAttr);
    }
    
    public boolean isTextboxEnabled() {
        boolean enabled = remarksTextbox.isEnabled();
        System.out.println("Textbox enabled state: " + enabled);
        return enabled;
    }

    public void enterTextInTextbox(String text) {
        remarksTextbox.clear();
        remarksTextbox.sendKeys(text);
        System.out.println("Entered text into textbox: " + text);
    }
    
 // In CheckChangeEventFormPage.java

 // OpenPage checkbox — instance(1) among CheckBox widgets
 @AndroidFindBy(uiAutomator = "new UiSelector().className(\"android.widget.CheckBox\").instance(1)")
 private WebElement openPageCheckbox;

 public void checkOpenPage() {
     openPageCheckbox.click();
     System.out.println("Clicked OpenPage checkbox — new screen expected to flash open and auto-close");
 }

 // Verifies we're back on CheckChangeEventForm after the flash-open-close behavior.
 // We check for a stable element that's always present on this screen (ActiveInactive checkbox).
 public boolean isBackOnCheckChangeEventForm(int timeoutSeconds) {
     try {
    	 WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
         wait.until(ExpectedConditions.presenceOfElementLocated(
             AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.CheckBox\").instance(0)")
         ));
         System.out.println("Confirmed: back on CheckChangeEventForm screen");
         return true;
     } catch (org.openqa.selenium.TimeoutException e) {
         System.out.println("App did NOT return to CheckChangeEventForm screen — possibly stuck elsewhere");
         return false;
     }
 }
 
//OpenAndCloseDailog checkbox — instance(2) among CheckBox widgets
@AndroidFindBy(uiAutomator = "new UiSelector().className(\"android.widget.CheckBox\").instance(2)")
private WebElement openCloseDialogCheckbox;

//Close (X) button inside the dialog — has a real resource-id, so this is reliable
@AndroidFindBy(id = "com.mobi.dds_iuv:id/imgbtn_close")
private WebElement dialogCloseButton;

public void checkOpenAndCloseDialog() {
  openCloseDialogCheckbox.click();
  System.out.println("Clicked OpenAndCloseDailog checkbox — dialog expected to appear");
}

//Confirms dialog opened by waiting for the close button to be present
public boolean isDialogOpen(int timeoutSeconds) {
  try {
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
      wait.until(ExpectedConditions.presenceOfElementLocated(
          AppiumBy.id("com.mobi.dds_iuv:id/imgbtn_close")
      ));
      System.out.println("Dialog opened successfully");
      return true;
  } catch (org.openqa.selenium.TimeoutException e) {
      System.out.println("Dialog did NOT open within " + timeoutSeconds + "s");
      return false;
  }
}

public void closeDialog() {
  dialogCloseButton.click();
  System.out.println("Clicked close (X) button on dialog");
}

//Confirms dialog closed by waiting for the close button to disappear,
//and we're back on the main CheckChangeEventForm (ActiveInactive checkbox visible)
public boolean isBackOnMainFormAfterDialogClose(int timeoutSeconds) {
  try {
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
      wait.until(ExpectedConditions.invisibilityOfElementLocated(
          AppiumBy.id("com.mobi.dds_iuv:id/imgbtn_close")
      ));
      System.out.println("Dialog closed successfully — back on main form");
      return true;
  } catch (org.openqa.selenium.TimeoutException e) {
      System.out.println("Dialog did NOT close within " + timeoutSeconds + "s");
      return false;
  }
}
//Formula checkbox — instance(3) among CheckBox widgets
@AndroidFindBy(uiAutomator = "new UiSelector().className(\"android.widget.CheckBox\").instance(3)")
private WebElement formulaCheckbox;

//Reuses the same GetDataValue EditText field (instance(0)) already declared as remarksTextbox
public void enterValueForFormula(String value) {
 remarksTextbox.clear();
 remarksTextbox.sendKeys(value);
 System.out.println("Entered value for formula calculation: " + value);
}

public void checkFormula() {
 formulaCheckbox.click();
 try {
     Thread.sleep(1000); // formula calculation complete hone ka time dena
 } catch (InterruptedException ignored) {}
 System.out.println("Clicked Formula checkbox — expecting value+100 calculation");
}

//Expected result text dynamically banaya jaata hai (jaise "110.0"), kyunki
//input value hum khud control karte hain — isliye hardcoded text() locator
//fragile nahi hai is case mein.
public boolean isCalculatedValueDisplayed(String expectedValue, int timeoutSeconds) {
 try {
     WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
     wait.until(ExpectedConditions.presenceOfElementLocated(
         AppiumBy.androidUIAutomator("new UiSelector().text(\"" + expectedValue + "\")")
     ));
     System.out.println("Confirmed calculated value displayed: " + expectedValue);
     return true;
 } catch (org.openqa.selenium.TimeoutException e) {
     System.out.println("Calculated value '" + expectedValue + "' NOT found within " + timeoutSeconds + "s");
     return false;
 }
}
//PostFormula checkbox — instance(4) among CheckBox widgets
@AndroidFindBy(uiAutomator = "new UiSelector().className(\"android.widget.CheckBox\").instance(4)")
private WebElement postFormulaCheckbox;

public void checkPostFormula() {
 postFormulaCheckbox.click();
 try {
     Thread.sleep(1000); // calculation complete hone ka time dena
 } catch (InterruptedException ignored) {}
 System.out.println("Clicked PostFormula checkbox — expecting value+100 calculation");
}

//HideShow checkbox — instance(5) among CheckBox widgets
@AndroidFindBy(uiAutomator = "new UiSelector().className(\"android.widget.CheckBox\").instance(5)")
private WebElement hideShowCheckbox;

public void toggleHideShow() {
 hideShowCheckbox.click();
 try {
     Thread.sleep(800); // UI ko hide/show update karne ka time dena
 } catch (InterruptedException ignored) {}
 System.out.println("Clicked HideShow checkbox");
}

//isDisplayed() visibility check karta hai — element ka enabled/disabled state
//nahi, balki wo screen par actually render/visible hai ya nahi
public boolean isTextboxVisible() {
 try {
     boolean visible = remarksTextbox.isDisplayed();
     System.out.println("Textbox visible state: " + visible);
     return visible;
 } catch (org.openqa.selenium.NoSuchElementException e) {
     // Agar element DOM/hierarchy se hi hata diya gaya ho (sirf hidden nahi,
     // completely removed), toh isDisplayed() exception throw karega —
     // is case mein bhi "not visible" hi maana jaayega
     System.out.println("Textbox not found in hierarchy — treating as not visible");
     return false;
 }
}
//Getdata checkbox — instance(6) among CheckBox widgets
@AndroidFindBy(uiAutomator = "new UiSelector().className(\"android.widget.CheckBox\").instance(6)")
private WebElement getdataCheckbox;

public void checkGetdata() {
 getdataCheckbox.click();
 try {
     Thread.sleep(1000); // naye "GetDataValue" line ke render hone ka time dena
 } catch (InterruptedException ignored) {}
 System.out.println("Clicked Getdata checkbox — expecting new GetDataValue line with value 21");
}

//HardFormula checkbox — instance(7) among CheckBox widgets
@AndroidFindBy(uiAutomator = "new UiSelector().className(\"android.widget.CheckBox\").instance(7)")
private WebElement hardFormulaCheckbox;

public void checkHardFormula() {
 hardFormulaCheckbox.click();
 try {
     Thread.sleep(1000); // calculation complete hone ka time dena
 } catch (InterruptedException ignored) {}
 System.out.println("Clicked HardFormula checkbox — expecting calculation on entered value");
}
public void closeDialogViaCheckbox() {
    WebElement checkbox = driver.findElement(AppiumBy.androidUIAutomator(
        "new UiScrollable(new UiSelector().scrollable(true))"
        + ".scrollIntoView(new UiSelector().className(\"android.widget.CheckBox\").instance(2))"
    ));
    System.out.println("Scrolled to and checking Checkchangeclosedailog checkbox to close dialog");
    checkbox.click();
}

}