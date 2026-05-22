package game.ui;

import bagel.Font;
import bagel.Image;

import game.data.GameDataUtils;

import java.util.Properties;

/**
 * Draws the small HUD at the top of the battle view: lives row, current
 * wave number and current score. Extends {@link OverlayRenderer} so the
 * font, colour, and absolute-position helpers stay shared with the other
 * overlays.
 */
public class HeadInfoDisplayRenderer extends OverlayRenderer {
    private final Image playerLifeImage;
    private final double playerLivesStartX;
    private final double playerLivesStartY;
    private final double playerLivesGap;
    private final Font hudFont;
    private final String waveText;
    private final double waveX;
    private final double waveY;
    private final String scoreText;
    private final double scoreX;
    private final double scoreY;

    public HeadInfoDisplayRenderer(Properties gameProps) {
        super(gameProps);
        this.playerLifeImage = new Image(gameProps.getProperty("playerLives.image"));
        double[] livesStart = GameDataUtils.parsePair(gameProps.getProperty("playerLives.startPosition"));
        this.playerLivesStartX = livesStart[0];
        this.playerLivesStartY = livesStart[1];
        this.playerLivesGap = GameDataUtils.parseDouble(gameProps, "playerLives.gap");

        this.hudFont = font();

        this.waveText = gameProps.getProperty("wave.text");
        double[] wavePos = GameDataUtils.parsePair(gameProps.getProperty("wave.pos"));
        this.waveX = wavePos[0];
        this.waveY = wavePos[1];

        this.scoreText = gameProps.getProperty("score.text");
        double[] scorePos = GameDataUtils.parsePair(gameProps.getProperty("score.pos"));
        this.scoreX = scorePos[0];
        this.scoreY = scorePos[1];
    }

    /**
     * Draws the lives row, current wave number, and current score.
     * Argument order matches the UML: score first, then lives, then wave.
     */
    public void render(int score, int remainingLives, int waveNumber) {
        renderLives(remainingLives);
        drawAt(hudFont, waveText + " " + waveNumber, waveX, waveY);
        drawAt(hudFont, scoreText + " " + score, scoreX, scoreY);
    }

    private void renderLives(int remainingLives) {
        for (int i = 0; i < remainingLives; i++) {
            playerLifeImage.draw(playerLivesStartX + i * playerLivesGap, playerLivesStartY);
        }
    }
}
