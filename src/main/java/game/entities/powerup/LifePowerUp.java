package game.entities.powerup;

import game.data.PowerUpSpawnInfo;
import game.entities.Player;

import java.util.Properties;

/**
 * Adds one life straight away, up to the starting life limit.
 */
public class LifePowerUp extends PowerUp {
    public LifePowerUp(Properties gameProps, PowerUpSpawnInfo spawnInfo) {
        super(gameProps, spawnInfo);
    }

    @Override
    public void applyTo(Player player) {
        markActive();
        player.addLife();
    }

    @Override
    public void expire(Player player) {
        // Life happens instantly, so there is nothing to undo here.
    }
}
