package Utils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static Properties properties;

    // Static initializer block - runs once when the class is first loaded (Java concept)
    static {
        try {
            properties = new Properties();
            InputStream input = ConfigReader.class.getClassLoader()
                    .getResourceAsStream("config.properties");
            properties.load(input);
            System.out.println("config.properties loaded successfully");
        } catch (IOException e) {
            System.out.println("Failed to load config.properties: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}