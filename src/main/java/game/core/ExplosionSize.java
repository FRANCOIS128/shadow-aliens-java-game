package game.core;

import java.util.Locale;

/**
 * The two explosion sizes used by the game.
 */
public enum ExplosionSize {
    SMALL,
    LARGE;

    /**
     * Gives the lowercase name used in property keys.
     */
    public String getKey() {
        return name().toLowerCase(Locale.ROOT);
    }
}
