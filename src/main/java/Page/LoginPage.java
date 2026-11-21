package Page;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

public class LoginPage extends BasePage {

    @AndroidFindBy(id = "com.mobi.dds_iuv:id/appSavyLogin")   
    private WebElement userIdField;

    @AndroidFindBy(id = "com.mobi.dds_iuv:id/login_password")  
    private WebElement passwordField;

    @AndroidFindBy(id = "com.mobi.dds_iuv:id/loginBtn")    
    private WebElement loginButton;

    public LoginPage(AndroidDriver driver) {
        super(driver);
    }

    public void login(String userId, String password) {
        userIdField.sendKeys(userId);
        System.out.println("Entered user ID: " + userId);
        passwordField.sendKeys(password);
        System.out.println("Entered password");
        loginButton.click();
        System.out.println("Login button clicked");
    }
}