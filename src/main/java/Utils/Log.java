package Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class Log {
	 private static final Logger logger =
	            LogManager.getLogger(Log.class);

	    public static void info(String message) {
	        logger.info(message);
	    }

	    public static void pass(String message) {
	        logger.info("[PASS] " + message);
	    }

	    public static void fail(String message) {
	        logger.error("[FAIL] " + message);
	    }

}
