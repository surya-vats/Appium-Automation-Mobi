package Page;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.Map;

public class RadioButtonClickFormPage extends BasePage {

    private final FormsPage formsPage;

    private static final String FORM_NAME = "ONRADIOBUTTONCLICK";
    private static final String FORM_TITLE = "OnRadioButtonClick";
    private static final String TEXTBOX_TEXT = "TestingTextbox";
    private static final String TOP_CAPTION = "InActive";

    public RadioButtonClickFormPage(AndroidDriver driver) {
        super(driver);
        this.formsPage = new FormsPage(driver);
        
     // XPath 2 engine following:: axis pe crash hota hai, isliye XPath 1 force karte hain.
        System.out.println("Setting enforceXPath1 = true for this session");
        driver.setSetting("enforceXPath1", true);
    }

    // ---------- Locators ----------

    // InActive row ka B = pehla "B", Active row ka A = doosra "A" (form top pe ho tab).
    private By inActiveRadioBBy() {
        return AppiumBy.androidUIAutomator("new UiSelector().text(\"B\").instance(0)");
    }

    private By activeRadioABy() {
        return AppiumBy.androidUIAutomator("new UiSelector().text(\"A\").instance(1)");
    }

    private By activeRadioBBy() {
        return AppiumBy.androidUIAutomator("new UiSelector().text(\"B\").instance(1)");
    }

    // OpenPage row ka A = teesra "A" (form top pe ho tab).
    private By openPageRadioABy() {
        return AppiumBy.androidUIAutomator("new UiSelector().text(\"A\").instance(2)");
    }

    // Toolbar title: form pe hone ka signal.
    private By formTitleBy() {
        return AppiumBy.androidUIAutomator("new UiSelector().text(\"" + FORM_TITLE + "\")");
    }

    private By scrollToTextBy(String text) {
        return AppiumBy.androidUIAutomator(
            "new UiScrollable(new UiSelector().scrollable(true))"
            + ".scrollIntoView(new UiSelector().text(\"" + text + "\"))");
    }

    // Type karne ke baad text badal jata hai, isliye textContains se dobara locate karte hain.
    private By scrollToTextContainsBy(String text) {
        return AppiumBy.androidUIAutomator(
            "new UiScrollable(new UiSelector().scrollable(true))"
            + ".scrollIntoView(new UiSelector().textContains(\"" + text + "\"))");
    }

    // Naye page ka radio A: neeche scroll karke dhoondhta hai.
    private By newPageRadioABy() {
        return AppiumBy.androidUIAutomator(
            "new UiScrollable(new UiSelector().scrollable(true))"
            + ".scrollIntoView(new UiSelector().className(\"android.widget.RadioButton\").text(\"A\"))");
    }

    // Row ke label ke baad aane wala pehla radio. Scroll se number nahi badalta,
    // aur row ka layout alag ho (GetDataforRadio) tab bhi chalta hai.
    private By radioAfterLabelBy(String label, String option) {
        return AppiumBy.xpath("//android.widget.TextView[@text=\"" + label + "\"]"
            + "/following::android.widget.RadioButton[@text=\"" + option + "\"][1]");
    }

    // ---------- Navigation ----------

    public void openForm() {
        System.out.println("Selecting " + FORM_NAME + " from forms grid");
        formsPage.selectForm(FORM_NAME);
    }

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

    // invisibilityOfElementLocated true deta hai jab element absent YA hidden ho.
    public boolean waitForFormToDisappear(int timeoutSeconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.invisibilityOfElementLocated(formTitleBy()));
            System.out.println("Form left the screen: new page/dialog is showing");
            return true;
        } catch (TimeoutException e) {
            System.out.println("Form still on screen after " + timeoutSeconds + "s");
            return false;
        }
    }

    // Form list pe aa gaye to form dobara select karo, warna kuch mat karo.
    public void reopenFormIfNeeded() {
        if (!isFormLoaded(3)) {
            openForm();
            System.out.println("Re-selected " + FORM_NAME);
        }
    }

    // Form ko fresh state me lao (typed text / hide-show state reset).
    public void reopenFormFresh() {
        System.out.println("Re-opening form to reset its state");
        navigateBackFromForm(); // BasePage se inherited
        reopenFormIfNeeded();
    }

    // Radios top pe hain. Scroll down ke baad wo UiAutomator tree se bahar ho jate hain,
    // isliye radio click se pehle hamesha top pe wapas aao.
    public void scrollToFormTop() {
        System.out.println("Scrolling back to top (anchor caption: " + TOP_CAPTION + ")");
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(d -> d.findElement(scrollToTextBy(TOP_CAPTION)));
    }

    // ---------- Tap helpers ----------

    // Scroll animation ruke tab tak ruko, warna tap scroll rokne me kharch ho jata hai.
    private void waitForScrollToSettle() {
        System.out.println("Waiting 1s for scroll animation to settle");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    }

    // Sirf tap, readback nahi: click se page badal sakta hai.
    private void tapElement(WebElement element, String name) {
        System.out.println("Tapping " + name + " with clickGesture");
        ((JavascriptExecutor) driver).executeScript("mobile: clickGesture",
            Map.of("elementId", ((RemoteWebElement) element).getId()));
    }

    // Top pe jao, locator se radio lo, tap karo, checked state print karo (page change na ho tab).
    private void tapRadio(By locator, String name) {
        scrollToFormTop();
        waitForScrollToSettle();
        System.out.println("Waiting for " + name + " radio");
        WebElement radio = new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.elementToBeClickable(locator));
        System.out.println(name + " before click: checked=" + radio.getAttribute("checked"));
        tapElement(radio, name);
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        System.out.println(name + " after click: checked="
            + driver.findElement(locator).getAttribute("checked"));
    }

    // Label wali row ka radio. Scroll wait ke BAHAR, sirf ek baar hota hai.
    private void tapRowRadio(String label, String option) {
        scrollToFormTop();
        waitForScrollToSettle();

        // Neeche wali rows (Postformula, GetDataforRadio) ke liye label ko screen pe lao.
        try {
            driver.findElement(scrollToTextBy(label));
        } catch (NoSuchElementException e) {
            System.out.println("Label '" + label + "' not found by scrolling");
        }
        waitForScrollToSettle();

        System.out.println("Waiting for " + label + " radio " + option);
        WebElement radio = new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.elementToBeClickable(radioAfterLabelBy(label, option)));
        System.out.println(label + " " + option + " before click: checked=" + radio.getAttribute("checked"));
        tapElement(radio, label + " " + option);
    }

    // ---------- Radio clicks ----------

    public void selectInActiveB()        { tapRadio(inActiveRadioBBy(), "InActive B"); }
    public void selectActiveA()          { tapRadio(activeRadioABy(), "Active A"); }
    public void selectActiveB()          { tapRadio(activeRadioBBy(), "Active B"); }

    public void selectHideA()            { tapRowRadio("Hide", "A"); }
    public void selectShowA()            { tapRowRadio("Show", "A"); }
    public void selectFormulaA()         { tapRowRadio("Formula", "A"); }
    public void selectPostformulaA()     { tapRowRadio("Postformula", "A"); }
    public void selectGetDataForRadioA() { tapRowRadio("GetDataforRadio", "A"); }

    // OpenPage: tap ke baad naya page khulta hai, isliye readback nahi.
    public void selectOpenPageA() {
        scrollToFormTop();
        waitForScrollToSettle();
        System.out.println("Waiting for OpenPage radio A (text A, instance 2)");
        WebElement radio = new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.elementToBeClickable(openPageRadioABy()));
        tapElement(radio, "OpenPage A");
    }

    // OpenDialog row ka label form me "OpenDailog" spelled hai.
    public void selectOpenDialogA() { tapRowRadio("OpenDailog", "A"); }

    // ---------- Radio checked state ----------

    public boolean isInActiveBChecked() {
        scrollToFormTop();
        boolean checked = Boolean.parseBoolean(
            driver.findElement(inActiveRadioBBy()).getAttribute("checked"));
        System.out.println("InActive B checked state: " + checked);
        return checked;
    }

    public boolean isActiveAChecked() {
        scrollToFormTop();
        boolean checked = Boolean.parseBoolean(
            driver.findElement(activeRadioABy()).getAttribute("checked"));
        System.out.println("Active A checked state: " + checked);
        return checked;
    }

    public boolean isActiveBChecked() {
        scrollToFormTop();
        boolean checked = Boolean.parseBoolean(
            driver.findElement(activeRadioBBy()).getAttribute("checked"));
        System.out.println("Active B checked state: " + checked);
        return checked;
    }

    // ---------- OpenPage / OpenDialog ----------

    // Naya page kuch ms baad render hota hai aur scrollIntoView element na mile to turant
    // exception deta hai, isliye har second retry.
    public void clickRadioAOnNewPage() {
        System.out.println("Looking for radio A on the new page (scrolling down)");
        long end = System.currentTimeMillis() + 10_000L;
        while (System.currentTimeMillis() < end) {
            try {
                WebElement radio = driver.findElement(newPageRadioABy());
                System.out.println("Radio A found on new page");
                waitForScrollToSettle();
                tapElement(radio, "new page radio A");
                return;
            } catch (NoSuchElementException e) {
                System.out.println("Radio A not found yet, retrying...");
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            }
        }
        throw new NoSuchElementException("Radio A not found on the new page within 10s");
    }

    // Dialog khulne par form ka toolbar title tree se hat jata hai.
    public boolean waitForDialogToOpen(int timeoutSeconds) {
        boolean opened = waitForFormToDisappear(timeoutSeconds);
        System.out.println(opened ? "Dialog opened" : "Dialog did NOT open within " + timeoutSeconds + "s");
        return opened;
    }

    // Dialog ke upar toolbar tap nahi hota, isliye device ka Back key.
    public void closeDialogWithBack() {
        System.out.println("Pressing device Back to close the dialog");
        driver.pressKey(new KeyEvent(AndroidKey.BACK));
    }

    // ---------- TestingTextbox ----------

    // Neeche scroll karke textbox return karta hai.
    public WebElement getTestingTextbox() {
        System.out.println("Scrolling to / locating textbox: " + TEXTBOX_TEXT);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.ignoring(StaleElementReferenceException.class);
        return wait.until(d -> d.findElement(scrollToTextBy(TEXTBOX_TEXT)));
    }

    public boolean isTestingTextboxEnabled() {
        boolean enabled = getTestingTextbox().isEnabled();
        System.out.println("TestingTextbox enabled state: " + enabled);
        return enabled;
    }

    // Har poll print hota hai, to fail hone par log me dikhta hai ki enabled badla ya nahi.
    public boolean waitForTestingTextboxEnabled(boolean expected) {
        System.out.println("Waiting for TestingTextbox enabled = " + expected);
        long end = System.currentTimeMillis() + 10_000L;
        while (System.currentTimeMillis() < end) {
            try {
                boolean enabled = driver.findElement(scrollToTextBy(TEXTBOX_TEXT)).isEnabled();
                System.out.println("Poll -> TestingTextbox enabled=" + enabled + " (expected " + expected + ")");
                if (enabled == expected) {
                    System.out.println("TestingTextbox enabled is now " + expected);
                    return true;
                }
            } catch (StaleElementReferenceException | NoSuchElementException e) {
                System.out.println("Poll -> textbox not ready: " + e.getClass().getSimpleName());
            }
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        }
        System.out.println("TestingTextbox did NOT reach enabled = " + expected + " within 10s");
        return false;
    }

    // Disabled field me sendKeys pe InvalidElementStateException aata hai (test ise catch karta hai).
    public void enterTextIntoTestingTextbox(String text) {
        System.out.println("Attempting to enter text: " + text);
        getTestingTextbox().sendKeys(text);
    }

    // Type ke baad hint text badal jata hai, isliye typed text se element dobara dhoondh ke padhte hain.
    public String typeIntoTestingTextboxAndGetValue(String text) {
        WebElement box = getTestingTextbox();
        System.out.println("Entering text: " + text);
        box.sendKeys(text);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.ignoring(StaleElementReferenceException.class);
        String value = wait.until(d -> d.findElement(scrollToTextContainsBy(text))).getText();
        System.out.println("Textbox value after typing: " + value);
        return value;
    }

    // Sirf tab sahi jab textbox khali ho (hint text "TestingTextbox" match hota hai).
    public String getTestingTextboxValue() {
        String value = getTestingTextbox().getText();
        System.out.println("Current TestingTextbox value: " + value);
        return value;
    }

    // Hidden hone par scrollIntoView NoSuchElementException deta hai, use "hidden" maante hain.
    public boolean isTestingTextboxPresent() {
        try {
            boolean shown = driver.findElement(scrollToTextBy(TEXTBOX_TEXT)).isDisplayed();
            System.out.println("TestingTextbox present, displayed=" + shown);
            return shown;
        } catch (NoSuchElementException e) {
            System.out.println("TestingTextbox NOT found on the form (hidden)");
            return false;
        }
    }

    // UI thodi der baad update hoti hai, isliye poll.
    public boolean waitForTestingTextboxVisible(boolean expected) {
        System.out.println("Waiting for TestingTextbox visible = " + expected);
        long end = System.currentTimeMillis() + 10_000L;
        while (System.currentTimeMillis() < end) {
            try {
                if (isTestingTextboxPresent() == expected) {
                    System.out.println("TestingTextbox visible is now " + expected);
                    return true;
                }
            } catch (StaleElementReferenceException ignored) { }
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        }
        System.out.println("TestingTextbox did NOT reach visible = " + expected + " within 10s");
        return false;
    }

    // ---------- Result text (85.0, 100) ----------

    // Koi bhi text screen pe aaya ya nahi (neeche scroll karke bhi dhoondhta hai).
    public boolean waitForTextOnScreen(String text, int timeoutSeconds) {
        long end = System.currentTimeMillis() + timeoutSeconds * 1000L;
        while (System.currentTimeMillis() < end) {
            try {
                driver.findElement(scrollToTextBy(text));
                System.out.println("Text '" + text + "' is displayed");
                return true;
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            }
        }
        System.out.println("Text '" + text + "' NOT displayed within " + timeoutSeconds + "s");
        return false;
    }
}