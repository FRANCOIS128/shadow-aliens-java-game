package game.entities.powerup;

import game.data.PowerUpSpawnInfo;
import game.entities.Player;

import java.util.Properties;

/**
 * Lets the player shoot more often for a short time.
 */
public class CooldownPowerUp extends PowerUp {
    private static final int COOLDOWN_REDUCTION_FACTOR = 3;

    public CooldownPowerUp(Properties gameProps, PowerUpSpawnInfo spawnInfo) {
        super(gameProps, spawnInfo);
    }

    @Override
    public void applyTo(Player player) {
        markActive();
        player.setShootCooldownDivider(COOLDOWN_REDUCTION_FACTOR);
    }

    @Override
    public void expire(Player player) {
        player.setShootCooldownDivider(1);
    }
}
