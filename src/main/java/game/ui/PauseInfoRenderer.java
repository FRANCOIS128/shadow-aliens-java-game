package game.ui;

import bagel.Font;

import game.data.GameDataUtils;

import java.util.Properties;

/**
 * Draws the pause text over the frozen battle screen.
 */
public class PauseInfoRenderer extends OverlayRenderer {
    private final TitleSpec pausedTitle;
    private final InstructionsListSpec controlsList;
    private final Font timescaleFont;
    private final String timescaleText;
    private final double timescaleY;

    public PauseInfoRenderer(Properties gameProps) {
        super(gameProps);
        this.pausedTitle = TitleSpec.from(gameProps, "pausedTitle");
        this.controlsList = InstructionsListSpec.from(gameProps, "controlsList");
        this.timescaleFont = font();
        this.timescaleText = gameProps.getProperty("timescale.text");
        double[] timescalePos = GameDataUtils.parsePair(gameProps.getProperty("timescale.pos"));
        this.timescaleY = timescalePos[1];
    }

    /**
     * Draws the paused title, the controls list, and the current
     * timescale value.
     */
    public void render(String displayedTimescaleText) {
        drawTitle(pausedTitle);
        drawInstructions(controlsList);
        drawAtRight(timescaleFont, timescaleText + " " + displayedTimescaleText, 8, timescaleY);
    }
}
