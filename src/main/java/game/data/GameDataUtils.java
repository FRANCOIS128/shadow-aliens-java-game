package game.data;

import bagel.util.Colour;

import java.util.Properties;

/**
 * Small parsing helpers for values stored in the data file. Centralising
 * the {@code Integer.parseInt(...trim())} and pair/triple parsing logic
 * here means every reader uses the same trimming and number rules, and
 * keeps the constructors of entities and renderers free of repetitive
 * parsing code.
 */
public final class GameDataUtils {
    private GameDataUtils() {
    }

    /**
     * Reads an integer property.
     */
    public static int parseInt(Properties gameProps, String key) {
        return Integer.parseInt(gameProps.getProperty(key).trim());
    }

    /**
     * Reads a double property.
     */
    public static double parseDouble(Properties gameProps, String key) {
        return Double.parseDouble(gameProps.getProperty(key).trim());
    }

    /**
     * Reads a two-number value like {@code "10,20"}.
     */
    public static double[] parsePair(String pairText) {
        String[] parts = pairText.split(",");
        return new double[] {
                Double.parseDouble(parts[0].trim()),
                Double.parseDouble(parts[1].trim())
        };
    }

    /**
     * Reads a three-number value like {@code "0,0,0"}.
     */
    public static double[] parseTriple(String tripleText) {
        String[] parts = tripleText.split(",");
        return new double[] {
                Double.parseDouble(parts[0].trim()),
                Double.parseDouble(parts[1].trim()),
                Double.parseDouble(parts[2].trim())
        };
    }

    /**
     * Turns an RGB text value into a BAGEL colour.
     */
    public static Colour parseColour(String colourText) {
        double[] colourParts = parseTriple(colourText);
        return new Colour(colourParts[0], colourParts[1], colourParts[2]);
    }
}
