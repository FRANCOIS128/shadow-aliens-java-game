package game.entities.powerup;

import game.data.PowerUpSpawnInfo;
import game.entities.Player;

import java.util.Properties;

/**
 * Makes the player move faster for a short time.
 */
public class EnginePowerUp extends PowerUp {
    private static final double SPEED_BOOST_MULTIPLIER = 2.0;

    public EnginePowerUp(Properties gameProps, PowerUpSpawnInfo spawnInfo) {
        super(gameProps, spawnInfo);
    }

    @Override
    public void applyTo(Player player) {
        markActive();
        player.setSpeedMultiplier(SPEED_BOOST_MULTIPLIER);
    }

    @Override
    public void expire(Player player) {
        player.setSpeedMultiplier(1.0);
    }
}
