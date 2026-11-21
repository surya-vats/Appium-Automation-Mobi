package Page;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class BasePage {
    protected AndroidDriver driver;

    public BasePage(AndroidDriver driver) {
        this.driver = driver;
        // Required for @AndroidFindBy fields (e.g. LoginPage.userIdField) to be populated —
        // without this, every @AndroidFindBy field stays null and throws NullPointerException
        // the moment you try to use it (sendKeys, click, etc.)
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    protected WebElement scrollToTextWithRetry(String text, int timeoutSeconds) {
        String scrollSelector =
                "new UiScrollable(new UiSelector().scrollable(true))"
              + ".scrollIntoView(new UiSelector().text(\"" + text + "\"))";

        long deadline = System.currentTimeMillis() + Duration.ofSeconds(timeoutSeconds).toMillis();
        NoSuchElementException lastError = null;
        int attempt = 0;

        while (System.currentTimeMillis() < deadline) {
            attempt++;
            try {
                WebElement el = driver.findElement(AppiumBy.androidUIAutomator(scrollSelector));
                System.out.println("Found '" + text + "' on attempt " + attempt);
                return el;
            } catch (NoSuchElementException e) {
                lastError = e;
                System.out.println("Attempt " + attempt + ": '" + text + "' not ready yet, retrying...");
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            }
        }
        throw new NoSuchElementException(
                "'" + text + "' not found after retrying for " + timeoutSeconds + "s", lastError);
    }

    protected void clickWhenReady(WebElement element, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }
    public void navigateBackFromForm() {
        WebElement backButton = driver.findElement(AppiumBy.accessibilityId("Navigate up"));
        System.out.println("Clicking toolbar back button (Navigate up) to exit form");
        backButton.click();
    }
}