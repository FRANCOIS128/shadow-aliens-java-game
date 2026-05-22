package game.screens;

import bagel.Input;
import bagel.Keys;

import game.core.ScreenState;
import game.ui.PauseInfoRenderer;

import java.util.Properties;

/**
 * Shows the paused view. The battle screen is still drawn underneath, but
 * it is not updated, so everything stays frozen exactly where it was.
 *
 * <p>Dev keys still work here, because the spec says they should work even
 * while the game is paused.
 */
public class PauseScreen implements GameScreen {
    private final BattleScreen battleScreen;
    private final PauseInfoRenderer renderer;

    public PauseScreen(BattleScreen battleScreen, Properties gameProps, double screenWidth) {
        this.battleScreen = battleScreen;
        this.renderer = new PauseInfoRenderer(gameProps);
    }

    @Override
    public ScreenState update(Input input) {
        if (input.wasPressed(Keys.ESCAPE)) {
            return ScreenState.BATTLE;
        }
        ScreenState devResult = battleScreen.handleDevKeysWhilePaused(input);
        if (devResult != null) {
            return devResult;
        }
        return ScreenState.PAUSE;
    }

    @Override
    public void render() {
        battleScreen.render();
        renderer.render(battleScreen.getDisplayedTimescaleText());
    }
}
