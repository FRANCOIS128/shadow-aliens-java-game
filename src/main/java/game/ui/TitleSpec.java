package game.ui;

import game.data.GameDataUtils;

import java.util.Properties;

/**
 * Tiny value object that bundles the three values needed to draw a
 * single title line — its text, the font size, and the y position. The
 * three text overlays (start, pause, end) all use the same shape, so
 * loading them through the same {@link #from} factory removes the
 * field-by-field copy-paste that lived in each renderer's constructor
 * before this class existed.
 *
 * @param text the literal text to draw
 * @param size the font size used for this title
 * @param posY the y coordinate where the title is centred
 */
public record TitleSpec(String text, int size, double posY) {
    /**
     * Loads the {@code <prefix>.text}, {@code <prefix>.size}, and
     * {@code <prefix>.posY} keys from the data file.
     */
    public static TitleSpec from(Properties gameProps, String prefix) {
        return new TitleSpec(
                gameProps.getProperty(prefix + ".text"),
                GameDataUtils.parseInt(gameProps, prefix + ".size"),
                GameDataUtils.parseDouble(gameProps, prefix + ".posY"));
    }
}
