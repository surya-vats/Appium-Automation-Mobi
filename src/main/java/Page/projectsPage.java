package Page;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class projectsPage extends BasePage {

    public projectsPage(AndroidDriver driver) {
        super(driver);
    }

    public FormsPage selectProject(String projectName) {
        System.out.println("Scrolling to find project: " + projectName);

        String scrollSelector =
                "new UiScrollable(new UiSelector().scrollable(true))"
              + ".scrollIntoView(new UiSelector().text(\"" + projectName + "\"))";

        WebElement projectCard = null;
        long deadline = System.currentTimeMillis() + Duration.ofSeconds(40).toMillis();
        NoSuchElementException lastError = null;
        int attempt = 0;

        // Manual retry loop: re-invoke scrollIntoView itself, not just re-check presence.
        // A single wait.until() only retries the SAME query; here the underlying list
        // may not even exist yet, so we need to retry the scroll call as the list loads in.
        while (System.currentTimeMillis() < deadline) {
            attempt++;
            try {
                projectCard = driver.findElement(AppiumBy.androidUIAutomator(scrollSelector));
                System.out.println("Found project on attempt " + attempt);
                break;
            } catch (NoSuchElementException e) {
                lastError = e;
                System.out.println("Attempt " + attempt + ": project list not ready yet, retrying...");
                try {
                    Thread.sleep(1000); // give the app more time to render items, then retry the scroll
                } catch (InterruptedException ignored) {}
            }
        }

        if (projectCard == null) {
            throw new NoSuchElementException(
                    "Project '" + projectName + "' not found after retrying for 40s", lastError);
        }

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.elementToBeClickable(projectCard));
        projectCard.click();
        System.out.println("Selected project: " + projectName);

        System.out.println("Waiting for forms grid to load (this can take up to 30s)...");
        WebDriverWait gridWait = new WebDriverWait(driver, Duration.ofSeconds(40));
        gridWait.until(ExpectedConditions.presenceOfElementLocated(
                AppiumBy.androidUIAutomator(
                    "new UiSelector().resourceIdMatches(\".*dynamicFormButton.*\")")
        ));
        System.out.println("Forms grid loaded — project selection complete");

        return new FormsPage(driver);
    }
}