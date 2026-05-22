package game.ui;

import game.data.GameDataUtils;

import java.util.Properties;

/**
 * Tiny value object for a comma-delimited multi-line list (instructions,
 * controls). Bundles the lines, the starting y coordinate, and the
 * vertical gap between rows. Loading through {@link #from} means the
 * three overlay renderers no longer repeat the same {@code split(",")}
 * + parse pair every time.
 *
 * @param lines the lines to draw, already split on commas
 * @param startPosY y coordinate of the first line
 * @param rowGap vertical pixel spacing between subsequent lines
 */
public record InstructionsListSpec(String[] lines, double startPosY, double rowGap) {
    /**
     * Loads the {@code <prefix>.text}, {@code <prefix>.startPosY}, and
     * {@code <prefix>.rowGap} keys from the data file.
     */
    public static InstructionsListSpec from(Properties gameProps, String prefix) {
        String[] lines = gameProps.getProperty(prefix + ".text").split(",");
        return new InstructionsListSpec(
                lines,
                GameDataUtils.parseDouble(gameProps, prefix + ".startPosY"),
                GameDataUtils.parseDouble(gameProps, prefix + ".rowGap"));
    }
}
