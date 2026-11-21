package Page;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class OnLabelClickTestingFormPage extends BasePage {

    private final FormsPage formsPage;

    // Confirmed: the forms grid item text is "ONLABELCLICKTESTING"
    private static final String FORM_NAME = "ONLABELCLICKTESTING";
    private static final String CAPTION_ID = "com.mobi.dds_iuv:id/textView1_horizontal_caption";
    // Confirmed: scroll down inside the dialog and click the TextView "10" to close it
    private static final String DIALOG_CLOSE_VALUE = "10";

    public OnLabelClickTestingFormPage(AndroidDriver driver) {
        super(driver);
        this.formsPage = new FormsPage(driver);
    }

    // ---------- Generic helpers ----------

    // Caption TextView (InActive, Active, Getdatalabel ...) located by its text.
    private By captionBy(String caption) {
        return By.xpath("//android.widget.TextView[@resource-id='" + CAPTION_ID + "' and @text='" + caption + "']");
    }

    // The clickable number label (1..9) sits next to its caption, so we jump to the
    // next sibling TextView with following-sibling::...[1].
    // ASSUMPTION: the number is the sibling TextView right after the caption.
    private By valueByCaption(String caption) {
        return By.xpath("//android.widget.TextView[@resource-id='" + CAPTION_ID + "' and @text='" + caption
            + "']/following-sibling::android.widget.TextView[1]");
    }

    // Explicit wait: polls until the element exists (max 10s), then returns it.
    private WebElement waitForValueLabel(String caption) {
        System.out.println("Waiting for label next to caption: " + caption);
        return new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.presenceOfElementLocated(valueByCaption(caption)));
    }

    // Confirmed in Inspector: the clickable label is found by its own text, e.g. text("2").
    // className("android.widget.TextView") is added so the locator can never match an
    // EditText that happens to contain the same digits (the tests type numbers into textboxes).
    private WebElement waitForClickableNumber(String number) {
        System.out.println("Waiting for label with text: " + number);
        return new WebDriverWait(driver, Duration.ofSeconds(10)).until(
            ExpectedConditions.presenceOfElementLocated(AppiumBy.androidUIAutomator(
                "new UiSelector().className(\"android.widget.TextView\").text(\"" + number + "\")")));
    }

    private void clickLabel(String name, String number) {
        WebElement label = waitForClickableNumber(number);
        System.out.println("Clicking " + name + " label (text=" + number + ")");
        label.click();
    }

    // instance(n) is 0-based: "the nth EditText on screen". Does not change when text is typed.
    private By editTextBy(int index) {
        return AppiumBy.androidUIAutomator(
            "new UiSelector().className(\"android.widget.EditText\").instance(" + index + ")");
    }

    // Explicit wait instead of a bare findElement: the app redraws the form after a label click,
    // so the EditText can be missing for a moment. WebDriverWait ignores NoSuchElementException
    // by default and keeps polling; we also ignore StaleElementReferenceException.
    private WebElement getEditTextByIndex(int index) {
        System.out.println("Locating EditText at index " + index);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.ignoring(StaleElementReferenceException.class);
        return wait.until(d -> d.findElement(editTextBy(index)));
    }

    // ---------- Debug helper (remove once locators are confirmed) ----------

    public void printEditTexts() {
        List<WebElement> fields = driver.findElements(AppiumBy.className("android.widget.EditText"));
        System.out.println("EditText count: " + fields.size());
        for (int i = 0; i < fields.size(); i++) {
            System.out.println("EditText[" + i + "] text='" + fields.get(i).getText()
                + "' enabled=" + fields.get(i).isEnabled());
        }
    }

    // ---------- Form state ----------

    public boolean isFormLoaded(int timeoutSeconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.presenceOfElementLocated(captionBy("InActive")));
            System.out.println("Form loaded: InActive caption is present");
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

    // Returns true if the form disappeared (OpenPage screen is showing).
    public boolean waitForFormToDisappear(int timeoutSeconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.invisibilityOfElementLocated(captionBy("InActive")));
            System.out.println("Form left the screen: OpenPage screen is showing");
            return true;
        } catch (TimeoutException e) {
            System.out.println("Form still on screen after " + timeoutSeconds + "s");
            return false;
        }
    }

    // ---------- Label clicks (caption -> number) ----------

    // Numbers come from the form: InActive=1, Active=2, OpenPage=3, OpenAndClose=4,
    // Hide=5, Show=6, Getdata=7, Formula=8, Postformula=9
    public void clickInActiveLabel()      { clickLabel("InActive", "1"); }
    public void clickActiveLabel()        { clickLabel("Active", "2"); }
    public void clickOpenPageLabel()      { clickLabel("OpenPage", "3"); }
    public void clickOpenAndCloseLabel()  { clickLabel("OpenAndClose", "4"); }
    public void clickHideLabel()          { clickLabel("Hide", "5"); }
    public void clickShowLabel()          { clickLabel("Show", "6"); }
    public void clickGetdataLabel()       { clickLabel("Getdata", "7"); }
    public void clickFormulaLabel()       { clickLabel("Formula", "8"); }
    public void clickPostformulaLabel()   { clickLabel("Postformula", "9"); }

    // ---------- Textboxes ----------

    // Order on screen: first EditText = TestingTestbox, second = FormulaTest.
    // CAUTION: instance(n) counts only what is currently on screen. When Hide (5) hides
    // TestingTestbox, FormulaTest becomes instance(0). Run Show (6) first before using index 1.
    public WebElement getTestingTextbox() { return getEditTextByIndex(0); }
    public WebElement getFormulaTextbox() { return getEditTextByIndex(1); }

    public boolean isTestingTextboxEnabled() {
        boolean enabled = getTestingTextbox().isEnabled();
        System.out.println("TestingTestbox enabled state: " + enabled);
        return enabled;
    }

    // Polls until TestingTestbox enabled state equals the expected value (max 10s).
    // Needed because the enable/disable change happens a moment after the label click.
    public void waitForTestingTextboxEnabled(boolean expected) {
        System.out.println("Waiting for TestingTestbox enabled = " + expected);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.ignoring(StaleElementReferenceException.class);
        wait.until(d -> d.findElement(editTextBy(0)).isEnabled() == expected);
        System.out.println("TestingTestbox enabled is now " + expected);
    }

    public void enterTextIntoTestingTextbox(String text) {
        System.out.println("Attempting to enter text: " + text);
        getTestingTextbox().sendKeys(text);
    }

    public String getTestingTextboxValue() {
        String value = getTestingTextbox().getText();
        System.out.println("Current TestingTestbox value: " + value);
        return value;
    }

    public void enterFormulaTestValue(String value) {
        WebElement field = getFormulaTextbox();
        field.clear();
        System.out.println("Entering value into FormulaTest: " + value);
        field.sendKeys(value);
        // hideKeyboard() throws if no keyboard is open, so catch it.
        try {
            driver.hideKeyboard();
        } catch (Exception e) {
            System.out.println("Keyboard was not open");
        }
    }

    // Count of EditTexts: both shown = 2, after Hide (5) hides TestingTestbox = 1.
    public int getEditTextCount() {
        int count = driver.findElements(AppiumBy.className("android.widget.EditText")).size();
        System.out.println("EditText count on screen: " + count);
        return count;
    }

    public boolean isTestingTextboxVisible() {
        boolean visible = getEditTextCount() >= 2;
        System.out.println("TestingTestbox visible: " + visible);
        return visible;
    }

    // Polls until the count matches, because the UI updates a moment after Hide/Show.
    public void waitForTestingTextboxVisible(boolean shouldBeVisible) {
        int expectedCount = shouldBeVisible ? 2 : 1;
        System.out.println("Waiting for EditText count = " + expectedCount);
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
            d -> d.findElements(AppiumBy.className("android.widget.EditText")).size() == expectedCount);
    }

    // ---------- Result values ----------

    // Confirmed: the Formula result shows in the TextView that is the next sibling of the "Label" caption.
    public String getFormulaResultValue() {
        String value = waitForValueLabelText("Label");
        System.out.println("Formula result displayed: " + value);
        return value;
    }

    // ASSUMPTION: Getdata result shows in the value next to the "Getdatalabel" caption.
    public String getGetdatalabelValue() {
        String value = waitForValueLabelText("Getdatalabel");
        System.out.println("Getdatalabel displayed: " + value);
        return value;
    }

    // Waits until the value TextView has non-empty text (the app fills it after the click).
    private String waitForValueLabelText(String caption) {
        return new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> {
            List<WebElement> els = d.findElements(valueByCaption(caption));
            if (els.isEmpty()) return null;          // null = keep polling
            String text = els.get(0).getText();
            return (text == null || text.trim().isEmpty()) ? null : text;
        });
    }

    // ---------- Dialog ----------

    // This dialog has no X button. It opens, you scroll down to the TextView "10",
    // and clicking it closes the dialog.
    // UiScrollable.scrollIntoView() swipes the scrollable container until the target
    // is on screen, then returns that element.
    private By dialogValueBy() {
        return AppiumBy.androidUIAutomator(
            "new UiScrollable(new UiSelector().scrollable(true))"
            + ".scrollIntoView(new UiSelector().className(\"android.widget.TextView\").text(\""
            + DIALOG_CLOSE_VALUE + "\"))");
    }

    // Dialog is "open" when we can scroll to its value "10".
    // ignoring() keeps the wait polling when scrollIntoView throws NoSuchElementException.
    public boolean isDialogOpen(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.ignoring(NoSuchElementException.class);
            wait.until(d -> d.findElement(dialogValueBy()));
            System.out.println("Dialog opened: found value " + DIALOG_CLOSE_VALUE + " after scrolling");
            return true;
        } catch (TimeoutException e) {
            System.out.println("Dialog did NOT open within " + timeoutSeconds + "s");
            return false;
        }
    }

    public void closeDialogBySelectingValue() {
        System.out.println("Scrolling to value " + DIALOG_CLOSE_VALUE + " inside the dialog");
        WebElement value = driver.findElement(dialogValueBy());
        System.out.println("Clicking value " + DIALOG_CLOSE_VALUE + " to close the dialog");
        value.click();
    }

    // Closed = the dialog's "10" is gone AND the form is back on screen.
    // invisibilityOfElementLocated also returns true when the element is gone from the tree.
    public boolean isDialogClosed(int timeoutSeconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds)).until(
                ExpectedConditions.invisibilityOfElementLocated(AppiumBy.androidUIAutomator(
                    "new UiSelector().className(\"android.widget.TextView\").text(\"" + DIALOG_CLOSE_VALUE + "\")")));
            System.out.println("Dialog value " + DIALOG_CLOSE_VALUE + " is gone");
            return isFormLoaded(timeoutSeconds);
        } catch (TimeoutException e) {
            System.out.println("Dialog did NOT close within " + timeoutSeconds + "s");
            return false;
        }
    }
}