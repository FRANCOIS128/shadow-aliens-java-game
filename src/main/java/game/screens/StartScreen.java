package game.screens;

import bagel.Input;
import bagel.Keys;

import game.core.ScreenState;
import game.entities.Player;
import game.ui.TextScreenRenderer;

import java.util.Properties;

/**
 * The first screen the player sees. It shows the ship and start text, lets
 * the ship move left and right, and starts the battle when Space is pressed.
 */
public class StartScreen implements GameScreen {
    private final Player player;
    private final TextScreenRenderer renderer;

    public StartScreen(Properties gameProps, double screenWidth) {
        this.player = new Player(gameProps, screenWidth);
        this.renderer = new TextScreenRenderer(gameProps);
    }

    @Override
    public ScreenState update(Input input) {
        player.handleHorizontalInput(input);
        if (input.wasPressed(Keys.SPACE)) {
            return ScreenState.BATTLE;
        }
        return ScreenState.START;
    }

    @Override
    public void render() {
        player.render();
        renderer.renderStartScreen();
    }
}
