package game.entities.enemy;

import game.data.EnemySpawnInfo;

import java.util.Properties;

/**
 * The simple Project 1 style enemy that just moves straight down.
 */
public class RegularEnemy extends EnemyShip {
    public RegularEnemy(Properties gameProps, EnemySpawnInfo spawnInfo) {
        super(gameProps, spawnInfo);
    }
}
