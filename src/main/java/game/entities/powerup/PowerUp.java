package game.entities.powerup;

import game.core.PowerUpType;
import game.data.GameDataUtils;
import game.data.PowerUpSpawnInfo;
import game.entities.MovingEntity;
import game.entities.Player;

import java.util.Properties;

/**
 * Shared base for collectable power-ups. Before collection a power-up
 * falls down the screen like any other moving entity. After collection,
 * the subclass-specific {@link #applyTo}/{@link #expire} pair decides
 * what happens — this is where polymorphism replaces what would
 * otherwise be a {@code switch} statement on power-up type.
 */
public abstract class PowerUp extends MovingEntity {
    private final PowerUpType type;
    private final int duration;
    private int remainingDuration;
    private boolean active;

    protected PowerUp(Properties gameProps, PowerUpSpawnInfo spawnInfo) {
        super(gameProps.getProperty("powerup." + spawnInfo.getType().getKey() + ".image"),
                spawnInfo.getPosX(),
                0.0,
                spawnInfo.getMovementSpeed());
        this.type = spawnInfo.getType();
        this.duration = readDurationOrZero(gameProps, spawnInfo.getType());
        this.remainingDuration = duration;
        this.active = false;
        setY(-getImageHeight() / 2);
    }

    public PowerUpType getType() {
        return type;
    }

    /**
     * Checks whether this power-up has been picked up and is still
     * affecting the player.
     */
    public boolean isActive() {
        return active && remainingDuration > 0;
    }

    /**
     * Checks whether this active power-up has finished.
     */
    public boolean isExpired() {
        return active && remainingDuration <= 0;
    }

    /**
     * Counts down the timer after the player has picked this up.
     */
    public void tickActive() {
        if (active && remainingDuration > 0) {
            remainingDuration--;
        }
    }

    /**
     * Applies this power-up to the player.
     */
    public abstract void applyTo(Player player);

    /**
     * Cleans up the effect when the power-up finishes or is replaced.
     */
    public abstract void expire(Player player);

    protected void markActive() {
        active = true;
    }

    private static int readDurationOrZero(Properties gameProps, PowerUpType type) {
        String key = "powerup." + type.getKey() + ".duration";
        return gameProps.getProperty(key) == null ? 0 : GameDataUtils.parseInt(gameProps, key);
    }
}
