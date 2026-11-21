package listeners;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.OutputType;
import driver.DriverManager;
import java.io.File;
import java.nio.file.Files;

public class TestListener implements ITestListener {

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) {
        extent = ExtentManager.getInstance();
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
        test.set(extentTest);
        System.out.println("Extent: test started -> " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().log(Status.PASS, "Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.get().log(Status.FAIL, "Test failed: " + result.getThrowable());

        // Screenshot capture on failure — bahut useful hota hai debugging ke liye
        try {
            AndroidDriver driver = DriverManager.getDriver();
            File srcFile = driver.getScreenshotAs(OutputType.FILE);
            String screenshotPath = System.getProperty("user.dir")
                + "/test-output/screenshots/" + result.getMethod().getMethodName() + ".png";
            File destFile = new File(screenshotPath);
            destFile.getParentFile().mkdirs(); // folder exist na ho toh bana do
            Files.copy(srcFile.toPath(), destFile.toPath());

            test.get().addScreenCaptureFromPath(screenshotPath);
            System.out.println("Screenshot saved: " + screenshotPath);
        } catch (Exception e) {
            System.out.println("Screenshot capture failed: " + e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.get().log(Status.SKIP, "Test skipped");
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush(); // report file ko disk pe finally likhta hai — ye call na ho toh report incomplete/empty rahegi
    }
}