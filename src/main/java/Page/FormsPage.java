// FormsPage.java
package Page;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;

public class FormsPage extends BasePage {

    public FormsPage(AndroidDriver driver) {
        super(driver);
    }

    public void selectMenu(String menuName) {
        WebElement menuButton = scrollToTextWithRetry(menuName, 60);
        clickWhenReady(menuButton, 15);
        System.out.println("Selected Menu: " + menuName);
    }

    public void selectForm(String formName) {
        WebElement formButton = scrollToTextWithRetry(formName, 25);
        clickWhenReady(formButton, 15);
        System.out.println("Selected form: " + formName);
    }
}