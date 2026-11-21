package Utils;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
public class ExtentManager {
	
	private static ExtentReports extent;

    public static ExtentReports getExtentReport() {

        if (extent == null) {
            ExtentSparkReporter spark =
                    new ExtentSparkReporter("test-output/ExtentReport.html");

            spark.config().setReportName("Mobile Automation Report");
            spark.config().setDocumentTitle("Appium Test Execution");

            extent = new ExtentReports();
            extent.attachReporter(spark);

            extent.setSystemInfo("OS", "Windows");
            extent.setSystemInfo("Tester", "Surya");
            extent.setSystemInfo("Framework", "Appium + TestNG");
        }
        return extent;
    }

}
