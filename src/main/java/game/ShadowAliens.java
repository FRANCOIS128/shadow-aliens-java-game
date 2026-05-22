package game;

import bagel.AbstractGame;
import bagel.Input;
import bagel.Window;

import game.core.ScreenManager;
import game.data.GameDataUtils;

import java.util.Properties;

/**
 * Top-level entry point. The class is deliberately thin: it loads the
 * data file, opens the window, and hands every frame to the
 * {@link ScreenManager}, which decides which screen runs.
 */
public class ShadowAliens extends AbstractGame {
    private static final String DEFAULT_GAME_DATA_FILE = "gameData.properties";
    private final ScreenManager screenManager;

    public ShadowAliens(Properties gameProps) {
        super(GameDataUtils.parseInt(gameProps, "window.width"),
                GameDataUtils.parseInt(gameProps, "window.height"),
                "Shadow Aliens");
        double screenWidth = GameDataUtils.parseInt(gameProps, "window.width");
        double screenHeight = GameDataUtils.parseInt(gameProps, "window.height");
        double[] backgroundColour = GameDataUtils.parseTriple(gameProps.getProperty("background.colour"));
        Window.setClearColour(backgroundColour[0], backgroundColour[1], backgroundColour[2]);
        screenManager = new ScreenManager(gameProps, screenWidth, screenHeight);
    }

    @Override
    protected void update(Input input) {
        screenManager.update(input);
        screenManager.render();
    }

    public static void main(String[] args) {
        String gameDataFile = System.getProperty("gameData");
        if (gameDataFile == null || gameDataFile.isBlank()) {
            gameDataFile = DEFAULT_GAME_DATA_FILE;
        }
        Properties gameProps = IOUtils.readPropertiesFile(gameDataFile);
        ShadowAliens game = new ShadowAliens(gameProps);
        game.run();
    }
}
