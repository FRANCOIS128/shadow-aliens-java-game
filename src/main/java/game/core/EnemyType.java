package game.core;

import game.data.EnemySpawnInfo;
import game.data.GameDataUtils;
import game.entities.enemy.EnemyShip;
import game.entities.enemy.RegularEnemy;
import game.entities.enemy.ShootingEnemy;
import game.entities.enemy.StrafingEnemy;

import java.util.Locale;
import java.util.Properties;

/**
 * The three kinds of enemies the data file can ask for. Each constant
 * overrides {@link #create(Properties, EnemySpawnInfo)} so the
 * {@link Wave} can build any enemy without a {@code switch} on the type.
 * This is the "enum-as-factory" pattern, and it is the main reason
 * adding a fourth enemy type is just two lines of new code here plus
 * one new subclass.
 */
public enum EnemyType {
    REGULAR {
        @Override
        public EnemyShip create(Properties gameProps, EnemySpawnInfo info) {
            return new RegularEnemy(gameProps, info);
        }
    },
    STRAFING {
        @Override
        public EnemyShip create(Properties gameProps, EnemySpawnInfo info) {
            double screenWidth = GameDataUtils.parseInt(gameProps, "window.width");
            return new StrafingEnemy(gameProps, info, screenWidth);
        }
    },
    SHOOTING {
        @Override
        public EnemyShip create(Properties gameProps, EnemySpawnInfo info) {
            return new ShootingEnemy(gameProps, info);
        }
    };

    /**
     * Builds the enemy ship matching this type. The window width needed
     * by the strafing enemy is read from {@code gameProps} so this method
     * keeps the same signature for every type, matching the UML.
     */
    public abstract EnemyShip create(Properties gameProps, EnemySpawnInfo info);

    /**
     * Gives the lowercase name used in property keys.
     */
    public String getKey() {
        return name().toLowerCase(Locale.ROOT);
    }

    /**
     * Turns the text from the data file into an enemy type.
     *
     * @param value text from the properties file
     * @return the matching enemy type
     */
    public static EnemyType fromKey(String value) {
        return EnemyType.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
