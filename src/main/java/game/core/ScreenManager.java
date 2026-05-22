package game.core;

import bagel.Input;

import game.entities.Player;
import game.screens.BattleScreen;
import game.screens.EndScreen;
import game.screens.GameScreen;
import game.screens.PauseScreen;
import game.screens.StartScreen;

import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;

/**
 * Handles the screen switching for the game. Each frame it asks the
 * current screen what should happen next, then moves to the next screen
 * if needed.
 *
 * <p>The end screen is built lazily, with the actual win/lose outcome
 * fixed at construction time. When the player restarts, the screens are
 * rebuilt so the next run is clean and does not carry over old enemies,
 * score, or power-ups.
 */
public class ScreenManager {
    private final Properties gameProps;
    private final double screenWidth;
    private final double screenHeight;
    private final Map<ScreenState, GameScreen> screens;
    private ScreenState currentState;
    private BattleScreen battleScreen;

    public ScreenManager(Properties gameProps, double screenWidth, double screenHeight) {
        this.gameProps = gameProps;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.screens = new EnumMap<>(ScreenState.class);
        this.currentState = ScreenState.START;
        buildScreens();
    }

    public void update(Input input) {
        GameScreen current = screens.get(currentState);
        ScreenState next = current.update(input);
        if (next != currentState) {
            transitionTo(next);
        }
    }

    public void render() {
        screens.get(currentState).render();
    }

    private void buildScreens() {
        battleScreen = new BattleScreen(gameProps, screenWidth, screenHeight);
        PauseScreen pauseScreen = new PauseScreen(battleScreen, gameProps, screenWidth);
        screens.put(ScreenState.START, new StartScreen(gameProps, screenWidth));
        screens.put(ScreenState.BATTLE, battleScreen);
        screens.put(ScreenState.PAUSE, pauseScreen);
    }

    private void transitionTo(ScreenState state) {
        switch (state) {
            case START:
                restartGame();
                return;
            case BATTLE:
                if (currentState != ScreenState.PAUSE) {
                    battleScreen.reset();
                }
                break;
            case END_WIN:
                screens.put(ScreenState.END_WIN, createEndScreen(EndState.WIN));
                break;
            case END_LOSE:
                screens.put(ScreenState.END_LOSE, createEndScreen(EndState.LOSE));
                break;
            case PAUSE:
            default:
                break;
        }
        currentState = state;
    }

    private EndScreen createEndScreen(EndState endState) {
        Player endPlayer = new Player(gameProps, screenWidth);
        return new EndScreen(endPlayer, endState, gameProps, screenWidth);
    }

    private void restartGame() {
        screens.clear();
        buildScreens();
        currentState = ScreenState.START;
    }
}
