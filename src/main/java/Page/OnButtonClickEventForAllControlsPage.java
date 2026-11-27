package Page;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;

public class OnButtonClickEventForAllControlsPage extends BasePage {

    private final FormsPage formsPage;

    private static final String FORM_NAME = "ONBUTTONCLICKEVENTFORALLCONTROLS";

    // Texts are kept EXACTLY as spelled in the app (Serach / Dailog).
    private static final String BTN_INACTIVE = "SerachforInActive";
    private static final String BTN_ACTIVE = "SerachforActive";
    private static final String BTN_HIDE = "SerachforHide";
    private static final String BTN_SHOW = "SerachforShow";
    private static final String BTN_OPEN_PAGE = "SerachforOpenpage";
    private static final String BTN_OPEN_DIALOG = "SearchforOpenDailog";
    private static final String BTN_CLOSE = "SearchforClose";
    private static final String BTN_FORMULA = "Searchforformula";
    private static final String BTN_POSTFORMULA = "SearchforPostFormula";
    private static final String BTN_GETDATA = "SearchforGetdata";
    private static final String RADIO_A_TEXT = "A";
    private static final String RADIO_B_TEXT = "B";
    private static final String TEXTBOX_HINT = "TestingTextbox";
    private static final String GETDATA_LABEL = "Getdatavalue";
    // Rating row labels (exactly as shown in the app)
    private static final String R_INACTIVE = "RatingforInActive";
    private static final String R_ACTIVE = "RatingforActive";
    private static final String R_HIDE = "RatingforHide";
    private static final String R_SHOW = "RatingforShow";
    private static final String R_OPEN_PAGE = "RatingforOpenPage";
    private static final String R_OPEN_DIALOG = "RatingforOpendailoge";
    private static final String R_POSTFORMULA = "RatingforPostFormula";
    private static final String R_GETDATA = "RatingforGetdata";
    private static final int RATING_STARS = 5; // stars in each rating bar
    private static final int RATING_STAR_TO_TAP = 4; // any star works, so tap the 4th
    private String lastRatingLabel = ""; // remembers which rating row was tapped last
    // Brightness tuning (0-255 scale). Change only these if the blur test is too
    // strict/loose.
    private static final double BLUR_MIN_DIFF = 10.0;
    private static final double RESTORE_TOLERANCE = 5.0;
    private static final String DROPDOWN_LABEL = "TestingRating";
private static final String DROPDOWN_SHEET_TITLE = "Select Option";   // title of the options bottom sheet

    public OnButtonClickEventForAllControlsPage(AndroidDriver driver) {
        super(driver);
        this.formsPage = new FormsPage(driver);
        // Needed for the following-sibling XPath used in the Getdata check.
        driver.setSetting("enforceXPath1", true);
    }

    // ---------- Locators ----------

    private By radioABy() {
        return AppiumBy.androidUIAutomator("new UiSelector().text(\"" + RADIO_A_TEXT + "\")");
    }

    private By radioBBy() {
        return AppiumBy.androidUIAutomator("new UiSelector().text(\"" + RADIO_B_TEXT + "\")");
    }

    // Form marker: first button of the form. A page or dialog covering the form
    // removes it from the UI tree.
    private By formMarkerBy() {
        return textBy(BTN_INACTIVE);
    }

    // Plain locator: for elements already on screen (e.g. inside a dialog).
    private By textBy(String text) {
        return AppiumBy.androidUIAutomator("new UiSelector().text(\"" + text + "\")");
    }

    // Scroll locator: scrolls until the text is visible, then returns that element.
    private By scrollToTextBy(String text) {
        return AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))"
                        + ".scrollIntoView(new UiSelector().text(\"" + text + "\"))");
    }

    // The value is the TextView right after the "Getdatavalue" label (same row).
    private By getDataValueBy() {
        return AppiumBy.xpath("//android.widget.TextView[@text=\"" + GETDATA_LABEL + "\"]"
                + "/following-sibling::android.widget.TextView[1]");
    }

    // ---------- Navigation ----------

    public void openForm() {
        System.out.println("Selecting " + FORM_NAME + " from forms grid");
        formsPage.selectForm(FORM_NAME);
    }

    public boolean isFormLoaded(int timeoutSeconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                    .until(ExpectedConditions.presenceOfElementLocated(formMarkerBy()));
            System.out.println("Form loaded: marker '" + BTN_INACTIVE + "' is present");
            return true;
        } catch (TimeoutException e) {
            System.out.println("Form NOT loaded within " + timeoutSeconds + "s");
            return false;
        }
    }

    // invisibilityOfElementLocated is true when the element is absent OR hidden.
    public boolean waitForFormToDisappear(int timeoutSeconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                    .until(ExpectedConditions.invisibilityOfElementLocated(formMarkerBy()));
            System.out.println("Form left the screen: new page/dialog is showing");
            return true;
        } catch (TimeoutException e) {
            System.out.println("Form still on screen after " + timeoutSeconds + "s");
            return false;
        }
    }

    // If we landed on the forms list, select the form again; otherwise do nothing.
    public void reopenFormIfNeeded() {
        if (!isFormLoaded(3)) {
            openForm();
            System.out.println("Re-selected " + FORM_NAME);
        }
    }

    // Fresh form = typed text and old results cleared. navigateBackFromForm() comes
    // from BasePage.
    // Fresh form = typed text and old results cleared. navigateBackFromForm() comes from BasePage.
public void reopenFormFresh() {
    System.out.println("Re-opening form to reset its state");
    try {
        navigateBackFromForm();
        // The form closes with an animation, so wait until it has really left the screen.
        waitForFormToDisappear(5);
    } catch (Exception e) {
        // On the forms list there is no toolbar back arrow, which is fine: just open the form.
        System.out.println("Toolbar back not available (already on forms list?): " + e.getClass().getSimpleName());
    }
    reopenFormIfNeeded();
}

    // ---------- Tap helpers ----------

    // Tap only, no read-back, because a click can change the page.
    private void tapElement(WebElement element, String name) {
        System.out.println("Tapping " + name + " with clickGesture");
        ((JavascriptExecutor) driver).executeScript("mobile: clickGesture",
                Map.of("elementId", ((RemoteWebElement) element).getId()));
    }

    // Scrolls the button into view (works even if already visible), waits until
    // clickable, taps.
    private void tapButton(String text) {
        System.out.println("Waiting for button: " + text);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.ignoring(StaleElementReferenceException.class);
        WebElement button = wait.until(d -> d.findElement(scrollToTextBy(text)));
        wait.until(ExpectedConditions.elementToBeClickable(button));
        tapElement(button, text);
    }

    // ---------- Button clicks ----------

    public void clickSearchForInActive() {
        tapButton(BTN_INACTIVE);
    }

    public void clickSearchForActive() {
        tapButton(BTN_ACTIVE);
    }

    public void clickSearchForHide() {
        tapButton(BTN_HIDE);
    }

    public void clickSearchForShow() {
        tapButton(BTN_SHOW);
    }

    public void clickSearchForOpenPage() {
        tapButton(BTN_OPEN_PAGE);
    }

    public void clickSearchForOpenDialog() {
        tapButton(BTN_OPEN_DIALOG);
    }

    public void clickSearchForFormula() {
        tapButton(BTN_FORMULA);
    }

    public void clickSearchForPostFormula() {
        tapButton(BTN_POSTFORMULA);
    }

    public void clickSearchForGetData() {
        tapButton(BTN_GETDATA);
    }

    // The Close button is inside the dialog, so a plain locator is enough (no
    // scrolling).
    public void clickSearchForClose() {
        System.out.println("Waiting for dialog button: " + BTN_CLOSE);
        WebElement close = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(textBy(BTN_CLOSE)));
        tapElement(close, BTN_CLOSE);
    }

    // ---------- Radio A / B ----------

    // Waits up to 10s for radio A to exist, so we never read it while the screen is
    // rendering.
    private WebElement waitForRadioA() {
        System.out.println("Waiting for Radio A to be present");
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(radioABy()));
    }

    private WebElement waitForRadioB() {
        System.out.println("Waiting for Radio B to be present");
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(radioBBy()));
    }

    public void tapRadioA() {
        tapElement(waitForRadioA(), "Radio A");
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }
    }

    public void tapRadioB() {
        tapElement(waitForRadioB(), "Radio B");
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }
    }

    // findElements returns an empty list instead of throwing, so it is safe for
    // "hidden" checks.
    public boolean isRadioAVisible() {
        List<WebElement> found = driver.findElements(radioABy());
        boolean visible = !found.isEmpty() && found.get(0).isDisplayed();
        System.out.println("Radio A visible: " + visible);
        return visible;
    }

    public boolean waitForRadioAVisible(boolean expected) {
        System.out.println("Waiting for Radio A visible = " + expected);
        long end = System.currentTimeMillis() + 10_000L;
        while (System.currentTimeMillis() < end) {
            try {
                if (isRadioAVisible() == expected) {
                    System.out.println("Radio A visible is now " + expected);
                    return true;
                }
            } catch (StaleElementReferenceException ignored) {
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }
        System.out.println("Radio A did NOT reach visible = " + expected + " within 10s");
        return false;
    }

    // ---------- Radio A blur check (screenshot brightness) ----------

    // Screenshot of ONLY radio A; returns the brightness of its darkest 5% pixels
    // (circle + letter).
    // A blurred/dimmed radio has lighter "ink", so this number goes UP.
    public double getRadioABrightness() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }
        WebElement radio = waitForRadioA();
        try {
            byte[] png = radio.getScreenshotAs(OutputType.BYTES);
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(png));
            double[] lum = new double[img.getWidth() * img.getHeight()];
            int i = 0;
            for (int y = 0; y < img.getHeight(); y++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    int rgb = img.getRGB(x, y);
                    int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
                    lum[i++] = 0.299 * r + 0.587 * g + 0.114 * b;
                }
            }
            Arrays.sort(lum);
            int n = Math.max(1, (int) (lum.length * 0.05));
            double sum = 0;
            for (int k = 0; k < n; k++) {
                sum += lum[k];
            }
            double brightness = sum / n;
            System.out.println("Radio A brightness (darkest 5% of pixels): " + brightness);
            return brightness;
        } catch (IOException e) {
            throw new RuntimeException("Could not read the radio A screenshot", e);
        }
    }

    public boolean isRadioALighterThan(double baseline) {
        double now = getRadioABrightness();
        boolean lighter = (now - baseline) >= BLUR_MIN_DIFF;
        System.out.println("Radio A lighter than baseline? baseline=" + baseline + ", now=" + now + " -> " + lighter);
        return lighter;
    }

    public boolean isRadioASameLookAs(double baseline) {
        double now = getRadioABrightness();
        boolean same = Math.abs(now - baseline) <= RESTORE_TOLERANCE;
        System.out.println("Radio A same look as baseline? baseline=" + baseline + ", now=" + now + " -> " + same);
        return same;
    }

    // ---------- OpenPage / OpenDialog ----------

    // Device Back key. If your app needs the toolbar arrow instead, use
    // navigateBackFromForm().
    public void goBackFromNewPage() {
        System.out.println("Pressing device Back to leave the new page");
        driver.pressKey(new KeyEvent(AndroidKey.BACK));
    }

    // Dialog covers the form, so the form marker leaves the UI tree.
    public boolean waitForDialogToOpen(int timeoutSeconds) {
        boolean opened = waitForFormToDisappear(timeoutSeconds);
        System.out.println(opened ? "Dialog opened" : "Dialog did NOT open within " + timeoutSeconds + "s");
        return opened;
    }

    // After SearchforClose the form marker must be back.
    public boolean waitForDialogToClose(int timeoutSeconds) {
        boolean closed = isFormLoaded(timeoutSeconds);
        System.out.println(closed ? "Dialog closed" : "Dialog still open after " + timeoutSeconds + "s");
        return closed;
    }

    // ---------- Formula / Postformula ----------

    // Scrolls down to the textbox, types, then closes the keyboard so it cannot
    // cover the buttons.
    public void enterTextIntoTestingTextbox(String text) {
        System.out.println("Scrolling to / locating textbox: " + TEXTBOX_HINT);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.ignoring(StaleElementReferenceException.class);
        WebElement box = wait.until(d -> d.findElement(scrollToTextBy(TEXTBOX_HINT)));
        System.out.println("Entering text: " + text);
        box.sendKeys(text);
        hideKeyboardIfShown();
    }

    // hideKeyboard() throws if no keyboard is open, so a failure here is not an
    // error.
    private void hideKeyboardIfShown() {
        try {
            driver.hideKeyboard();
            System.out.println("Keyboard hidden");
        } catch (Exception e) {
            System.out.println("No keyboard to hide");
        }
    }

    // Looks for the text anywhere on the form (scrolls to find it). Retries until
    // timeout.
    public boolean waitForTextOnScreen(String text, int timeoutSeconds) {
        long end = System.currentTimeMillis() + timeoutSeconds * 1000L;
        while (System.currentTimeMillis() < end) {
            try {
                driver.findElement(scrollToTextBy(text));
                System.out.println("Text '" + text + "' is displayed");
                return true;
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
            }
        }
        System.out.println("Text '" + text + "' NOT displayed within " + timeoutSeconds + "s");
        return false;
    }

    // ---------- Getdata ----------

    private boolean matchesNumber(String actual, String number) {
        return actual != null && actual.trim().matches(number + "(\\.0)?");
    }

    // Scrolls a bit to the Getdatavalue row, then reads the value next to it.
    // Checks text AND content-desc, because this app stores the value in either.
    public boolean waitForGetDataValue(String number, int timeoutSeconds) {
        System.out.println("Waiting for Getdatavalue to show " + number);
        long end = System.currentTimeMillis() + timeoutSeconds * 1000L;
        while (System.currentTimeMillis() < end) {
            try {
                driver.findElement(scrollToTextBy(GETDATA_LABEL));
                WebElement value = driver.findElement(getDataValueBy());
                String text = value.getText();
                String desc = value.getAttribute("contentDescription");
                System.out.println("Getdatavalue value element -> text=[" + text + "], content-desc=[" + desc + "]");
                if (matchesNumber(text, number) || matchesNumber(desc, number)) {
                    System.out.println("Getdatavalue shows " + number);
                    return true;
                }
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                System.out.println("Getdatavalue value not ready: " + e.getClass().getSimpleName());
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }
        System.out.println("Getdatavalue did NOT show " + number + " within " + timeoutSeconds + "s");
        return false;
    }

    // ---------- Rating controls ----------

    // The rating bar of a row = first RatingBar after the row's label (XPath 1 is
    // already enforced).
    private By ratingBarBy(String label) {
        return AppiumBy.xpath("//android.widget.TextView[@text=\"" + label + "\"]"
                + "/following::android.widget.RatingBar[1]");
    }

    // Scrolls the row into view, then taps one star by screen coordinates.
    // A RatingBar is usually ONE element, so click() would always hit its centre.
    // x = left + width * (star - 0.5) / stars -> middle of the chosen star.
    private void tapRating(String label) {
        lastRatingLabel = label;
        System.out.println("Scrolling to rating row: " + label);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.ignoring(StaleElementReferenceException.class);
        wait.until(d -> d.findElement(scrollToTextBy(label)));
        WebElement bar = wait.until(ExpectedConditions.presenceOfElementLocated(ratingBarBy(label)));

        Rectangle r = bar.getRect();
        int x = r.getX() + (int) (r.getWidth() * (RATING_STAR_TO_TAP - 0.5) / RATING_STARS);
        int y = r.getY() + r.getHeight() / 2;
        System.out.println("Tapping star " + RATING_STAR_TO_TAP + " of '" + label + "' at x=" + x + ", y=" + y);
        ((JavascriptExecutor) driver).executeScript("mobile: clickGesture", Map.of("x", x, "y", y));
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }
    }

    // Rating rows are at the bottom. Radio A is near the top and leaves the UI tree
    // after a scroll down,
    // so for radio checks we scroll back to the top first (scrollIntoView always
    // starts from the top).
    // private void scrollToFormTop() {
    //     System.out.println("Scrolling back to the top of the form");
    //     driver.findElement(scrollToTextBy(BTN_INACTIVE));
    // }

    public void clickRatingForInActive() { tapRating(R_INACTIVE); }
public void clickRatingForActive()   { tapRating(R_ACTIVE); }
public void clickRatingForHide()     { tapRating(R_HIDE); }
public void clickRatingForShow()     { tapRating(R_SHOW); }

    // Rating taps that open a page / dialog: no scrolling after the tap (the screen
    // has changed).
    public void clickRatingForOpenPage() {
        tapRating(R_OPEN_PAGE);
    }

    public void clickRatingForOpenDialog() {
        tapRating(R_OPEN_DIALOG);
    }

    // Rating taps whose result is searched by the existing helpers (they scroll on
    // their own).
    public void clickRatingForPostFormula() {
        tapRating(R_POSTFORMULA);
    }

    public void clickRatingForGetData() {
        tapRating(R_GETDATA);
    }

    // The form marker (SerachforInActive) is scrolled out of the tree on a scrolled
    // form,
    // so for rating tests "form left / came back" is judged by the tapped rating
    // row itself.
    public boolean waitForRatingRowToDisappear(int timeoutSeconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                    .until(ExpectedConditions.invisibilityOfElementLocated(textBy(lastRatingLabel)));
            System.out.println("Rating row '" + lastRatingLabel + "' left the screen: new page/dialog is showing");
            return true;
        } catch (TimeoutException e) {
            System.out.println("Rating row '" + lastRatingLabel + "' still on screen after " + timeoutSeconds + "s");
            return false;
        }
    }

    public boolean waitForRatingRowToReturn(int timeoutSeconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                    .until(ExpectedConditions.presenceOfElementLocated(textBy(lastRatingLabel)));
            System.out.println("Rating row '" + lastRatingLabel + "' is back on the form");
            return true;
        } catch (TimeoutException e) {
            System.out.println("Rating row '" + lastRatingLabel + "' NOT back within " + timeoutSeconds + "s");
            return false;
        }
    }

    // ---------- TestingRating dropdown ----------

// The dropdown = first RelativeLayout after the "TestingRating" label.
private By testingRatingDropdownBy() {
    return AppiumBy.xpath("//android.widget.TextView[@text=\"" + DROPDOWN_LABEL + "\"]"
            + "/following::android.widget.RelativeLayout[1]");
}

// Scrolls to the dropdown and taps it. The attributes are printed so the log shows what the app reports.
public void tapTestingRatingDropdown() {
    System.out.println("Scrolling to dropdown: " + DROPDOWN_LABEL);
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.ignoring(StaleElementReferenceException.class);
    wait.until(d -> d.findElement(scrollToTextBy(DROPDOWN_LABEL)));
    WebElement dropdown = wait.until(ExpectedConditions.presenceOfElementLocated(testingRatingDropdownBy()));
    System.out.println("Dropdown attributes -> class=" + dropdown.getAttribute("class")
            + ", clickable=" + dropdown.getAttribute("clickable")
            + ", enabled=" + dropdown.getAttribute("enabled"));
    tapElement(dropdown, "TestingRating dropdown");
    try { Thread.sleep(500); } catch (InterruptedException ignored) { }
}

// "Clickable" is judged by behaviour: does the options bottom sheet open after a tap?
public boolean waitForDropdownOptionsToOpen(int timeoutSeconds) {
    try {
        new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.presenceOfElementLocated(textBy(DROPDOWN_SHEET_TITLE)));
        System.out.println("Dropdown options opened ('" + DROPDOWN_SHEET_TITLE + "' is visible)");
        return true;
    } catch (TimeoutException e) {
        System.out.println("Dropdown options did NOT open within " + timeoutSeconds + "s");
        return false;
    }
}

public void closeDropdownOptions() {
    System.out.println("Pressing device Back to close the dropdown options");
    driver.pressKey(new KeyEvent(AndroidKey.BACK));
}

// Visible = the label can be scrolled to AND the dropdown is displayed.
// If the whole row is removed, scrollIntoView throws NoSuchElementException, which also means hidden.
public boolean isTestingRatingVisible() {
    try {
        driver.findElement(scrollToTextBy(DROPDOWN_LABEL));
    } catch (NoSuchElementException e) {
        System.out.println("TestingRating row NOT found (hidden)");
        return false;
    }
    List<WebElement> found = driver.findElements(testingRatingDropdownBy());
    boolean visible = !found.isEmpty() && found.get(0).isDisplayed();
    System.out.println("TestingRating dropdown visible: " + visible);
    return visible;
}

public boolean waitForTestingRatingVisible(boolean expected) {
    System.out.println("Waiting for TestingRating visible = " + expected);
    long end = System.currentTimeMillis() + 10_000L;
    while (System.currentTimeMillis() < end) {
        try {
            if (isTestingRatingVisible() == expected) {
                System.out.println("TestingRating visible is now " + expected);
                return true;
            }
        } catch (StaleElementReferenceException ignored) { }
        try { Thread.sleep(500); } catch (InterruptedException ignored) { }
    }
    System.out.println("TestingRating did NOT reach visible = " + expected + " within 10s");
    return false;
}
}