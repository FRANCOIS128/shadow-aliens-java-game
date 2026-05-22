package game.screens;

import bagel.Input;
import bagel.Keys;

import game.core.EndState;
import game.core.ScreenState;
import game.entities.Player;
import game.ui.EndScreenRenderer;

import java.util.Properties;

/**
 * The screen shown after the run is finished. The player can still move
 * the ship left and right, and Space starts a new battle. The win or lose
 * outcome is supplied at construction time, so the screen is fully
 * configured once and does not have to be mutated later.
 */
public class EndScreen implements GameScreen {
    private final Player player;
    private final EndState endState;
    private final EndScreenRenderer renderer;

    public EndScreen(Player player,
                     EndState endState,
                     Properties gameProps,
                     double screenWidth) {
        this.player = player;
        this.endState = endState;
        this.renderer = new EndScreenRenderer(gameProps);
        player.reset();
    }

    @Override
    public ScreenState update(Input input) {
        player.handleHorizontalInput(input);
        if (input.wasPressed(Keys.SPACE)) {
            return ScreenState.BATTLE;
        }
        return endState == EndState.WIN ? ScreenState.END_WIN : ScreenState.END_LOSE;
    }

    @Override
    public void render() {
        player.render();
        renderer.render(endState, 0);
    }
}
