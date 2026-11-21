package Utils;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.AppiumBy;

public class ScrollUtils {
	
	private AndroidDriver driver;

    public ScrollUtils(AndroidDriver driver) {
        this.driver = driver;
    }

    public void scrollToText(String text) {
    	driver.findElement(
                AppiumBy.androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true))" +
                    ".scrollIntoView(new UiSelector().text(\"" + text + "\"))"
                )
            );
    }

}
