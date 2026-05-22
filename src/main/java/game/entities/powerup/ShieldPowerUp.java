package game.entities.powerup;

import game.data.PowerUpSpawnInfo;
import game.entities.Player;

import java.util.Properties;

/**
 * Makes the player invincible for a short time.
 */
public class ShieldPowerUp extends PowerUp {
    public ShieldPowerUp(Properties gameProps, PowerUpSpawnInfo spawnInfo) {
        super(gameProps, spawnInfo);
    }

    @Override
    public void applyTo(Player player) {
        markActive();
        player.setShieldActive(true);
    }

    @Override
    public void expire(Player player) {
        player.setShieldActive(false);
    }
}
