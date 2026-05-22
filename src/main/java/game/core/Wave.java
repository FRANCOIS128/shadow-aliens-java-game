package game.core;

import game.data.EnemySpawnInfo;
import game.data.PowerUpSpawnInfo;
import game.entities.enemy.EnemyShip;
import game.entities.powerup.PowerUp;

import java.util.List;
import java.util.Properties;

/**
 * Stores the spawn plan for one wave. When the wave timer reaches an
 * entry's arrival time, this class asks the relevant enum to build the
 * matching enemy or power-up. Enemies and power-ups have their own spawn
 * methods so callers (and the UML) stay symmetrical.
 */
public class Wave {
    private final List<EnemySpawnInfo> enemySpawnInfos;
    private final List<PowerUpSpawnInfo> powerUpSpawnInfos;
    private int nextEnemyIndex;
    private int nextPowerUpIndex;

    public Wave(List<EnemySpawnInfo> enemySpawnInfos,
                List<PowerUpSpawnInfo> powerUpSpawnInfos) {
        this.enemySpawnInfos = enemySpawnInfos;
        this.powerUpSpawnInfos = powerUpSpawnInfos;
        reset();
    }

    public final void reset() {
        nextEnemyIndex = 0;
        nextPowerUpIndex = 0;
    }

    /**
     * Adds every enemy whose arrival time matches this wave frame.
     */
    public void spawnEnemies(int frameCount,
                             List<EnemyShip> enemies,
                             Properties gameProps) {
        while (nextEnemyIndex < enemySpawnInfos.size()
                && enemySpawnInfos.get(nextEnemyIndex).getArrivalTime() == frameCount) {
            EnemySpawnInfo info = enemySpawnInfos.get(nextEnemyIndex);
            enemies.add(info.getType().create(gameProps, info));
            nextEnemyIndex++;
        }
    }

    /**
     * Adds every power-up whose arrival time matches this wave frame.
     */
    public void spawnPowerUps(int frameCount,
                              List<PowerUp> powerUps,
                              Properties gameProps) {
        while (nextPowerUpIndex < powerUpSpawnInfos.size()
                && powerUpSpawnInfos.get(nextPowerUpIndex).getArrivalTime() == frameCount) {
            PowerUpSpawnInfo info = powerUpSpawnInfos.get(nextPowerUpIndex);
            powerUps.add(info.getType().create(gameProps, info));
            nextPowerUpIndex++;
        }
    }

    /**
     * Checks whether this wave has already released everything it planned
     * to release.
     */
    public boolean hasSpawnedAllObjects() {
        return nextEnemyIndex >= enemySpawnInfos.size()
                && nextPowerUpIndex >= powerUpSpawnInfos.size();
    }
}
