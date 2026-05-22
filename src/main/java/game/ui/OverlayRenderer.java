package game.ui;

import bagel.DrawOptions;
import bagel.Font;

import game.data.GameDataUtils;

import java.util.Properties;

/**
 * Shared base for the four text overlays (start, pause, end, HUD). All
 * of them draw centred lines using the same font path and text colour,
 * so the common setup and helpers live here. {@link #drawTitle(TitleSpec)}
 * and {@link #drawInstructions(InstructionsListSpec)} let subclasses say
 * "draw this title" and "draw this list" in one line each, which is
 * what cuts the repetition that used to live in the three text overlays.
 */
public abstract class OverlayRenderer {
    private final double screenWidth;
    private final String fontPath;
    private final int defaultTextSize;
    private final DrawOptions textOptions;

    protected OverlayRenderer(Properties gameProps) {
        this.screenWidth = GameDataUtils.parseInt(gameProps, "window.width");
        this.fontPath = gameProps.getProperty("text.font");
        this.defaultTextSize = GameDataUtils.parseInt(gameProps, "text.size");
        this.textOptions = new DrawOptions()
                .setBlendColour(GameDataUtils.parseColour(gameProps.getProperty("text.colour")));
    }

    /**
     * Builds a font using the default text size.
     */
    protected final Font font() {
        return new Font(fontPath, defaultTextSize);
    }

    /**
     * Builds a font at a specific size.
     */
    protected final Font font(int size) {
        return new Font(fontPath, size);
    }

    /**
     * Draws one line centred horizontally at the given y coordinate.
     */
    protected void drawCentred(Font font, String text, double y) {
        double x = (screenWidth - font.getWidth(text)) / 2;
        font.drawString(text, x, y, textOptions);
    }

    /**
     * Draws several lines stacked vertically, each centred horizontally.
     * This is the overloaded counterpart of
     * {@link #drawCentred(Font, String, double)} for multi-line overlays.
     */
    protected void drawCentred(Font font, String[] lines, double startY, double rowGap) {
        for (int i = 0; i < lines.length; i++) {
            drawCentred(font, lines[i].trim(), startY + i * rowGap);
        }
    }

    /**
     * Draws plain text at an absolute position (left-aligned).
     */
    protected void drawAt(Font font, String text, double x, double y) {
        font.drawString(text, x, y, textOptions);
    }

    /**
     * Renders a title spec at its configured size and position.
     */
    protected final void drawTitle(TitleSpec title) {
        drawCentred(font(title.size()), title.text(), title.posY());
    }

    /**
     * Renders a multi-line list spec at the default text size.
     */
    protected final void drawInstructions(InstructionsListSpec list) {
        drawCentred(font(), list.lines(), list.startPosY(), list.rowGap());
    }
}
