package game;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Loads the data file. Kept as a separate class so the main entry point
 * stays focused on bootstrapping the BAGEL window. If the file cannot be
 * read the program prints the spec-mandated error and exits with -1.
 */
public final class IOUtils {
    private IOUtils() {
    }

    /**
     * Reads the chosen properties file.
     *
     * @param configFile path to the properties file
     * @return the loaded properties
     */
    public static Properties readPropertiesFile(String configFile) {
        Properties appProps = new Properties();
        try (FileInputStream stream = new FileInputStream(configFile)) {
            appProps.load(stream);
        } catch (IOException ex) {
            System.err.println("Error with " + configFile);
            System.exit(-1);
        }
        return appProps;
    }
}
