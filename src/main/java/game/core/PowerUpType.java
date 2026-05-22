package game.core;

import game.data.PowerUpSpawnInfo;
import game.entities.powerup.CooldownPowerUp;
import game.entities.powerup.EnginePowerUp;
import game.entities.powerup.LifePowerUp;
import game.entities.powerup.PowerUp;
import game.entities.powerup.ShieldPowerUp;

import java.util.Locale;
import java.util.Properties;

/**
 * The four power-ups that can appear during a wave. Each value knows how to
 * build the matching power-up, so callers do not have to switch on the
 * type themselves.
 */
public enum PowerUpType {
    SHIELD {
        @Override
        public PowerUp create(Properties gameProps, PowerUpSpawnInfo info) {
            return new ShieldPowerUp(gameProps, info);
        }
    },
    LIFE {
        @Override
        public PowerUp create(Properties gameProps, PowerUpSpawnInfo info) {
            return new LifePowerUp(gameProps, info);
        }
    },
    COOLDOWN {
        @Override
        public PowerUp create(Properties gameProps, PowerUpSpawnInfo info) {
            return new CooldownPowerUp(gameProps, info);
        }
    },
    ENGINE {
        @Override
        public PowerUp create(Properties gameProps, PowerUpSpawnInfo info) {
            return new EnginePowerUp(gameProps, info);
        }
    };

    /**
     * Builds the power-up matching this type.
     */
    public abstract PowerUp create(Properties gameProps, PowerUpSpawnInfo info);

    /**
     * Gives the lowercase name used in property keys.
     */
    public String getKey() {
        return name().toLowerCase(Locale.ROOT);
    }

    /**
     * Turns the text from the data file into a power-up type.
     *
     * @param value text from the properties file
     * @return the matching power-up type
     */
    public static PowerUpType fromKey(String value) {
        return PowerUpType.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
