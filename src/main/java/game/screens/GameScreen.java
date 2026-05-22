package game.screens;

import bagel.Input;

import game.core.ScreenState;

/**
 * Common shape for every screen in the game. Each screen updates itself,
 * renders itself, and tells the screen manager where to go next.
 */
public interface GameScreen {
    /**
     * Handles this frame's input.
     *
     * @param input keyboard and mouse input for this frame
     * @return the screen that should be active next
     */
    ScreenState update(Input input);

    /**
     * Draws this screen.
     */
    void render();
}
