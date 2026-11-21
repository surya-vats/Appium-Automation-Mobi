package Base;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import org.openqa.selenium.remote.DesiredCapabilities;
import io.appium.java_client.android.AndroidDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
public class baseClass {
	protected AndroidDriver driver;		
	@BeforeClass
	public void setup1() throws MalformedURLException, InterruptedException {
		DesiredCapabilities cap = new DesiredCapabilities();
		cap.setCapability("deviceName", "Realme P4");
		cap.setCapability("platformName", "Android");
		cap.setCapability("automationName", "uiautomator2");
		cap.setCapability("app", "C:\\\\Desktop\\\\All APK\\\\NewDevUi.apk");
		cap.setCapability("platformversion", "16");	
		cap.setCapability("appPackage", "com.mobi.dds_iuv");
		cap.setCapability("appActivity", "com.mobi.datadesignstudio.FirstScreenActivity");	
		cap.setCapability("autoGrantPermissions", true);
		cap.setCapability("noReset", true);
		cap.setCapability("appWaitActivity", "*");
		
//		URL url = new URL("http://127.0.0.1:4723/");
		//driver = new AppiumDriver(url, cap);
		URL url = URI.create("http://127.0.0.1:4723/").toURL();
	  driver= new AndroidDriver(url , cap);
	 		
		
		Thread.sleep(1000);
		System.out.println("Application started sucessfully and connected with appsavy DevUI");
		
	}
	@AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
	
}
