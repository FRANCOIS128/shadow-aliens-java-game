package game.core;

import game.data.GameDataLoader;
import game.entities.enemy.EnemyShip;
import game.entities.powerup.PowerUp;
import game.entities.projectile.EnemyProjectile;

import java.util.List;
import java.util.Properties;

/**
 * Looks after the current wave. Each simulation step it advances the
 * wave's frame counter and asks the current {@link Wave} to spawn any
 * enemies or power-ups that are due. The same object knows whether the
 * wave is finished and how to move to the next one.
 */
public class WaveManager {
    private final List<Wave> waves;
    private int currentWaveIndex;
    private int currentFrame;

    public WaveManager(Properties gameProps) {
        this.waves = GameDataLoader.loadWaves(gameProps);
        reset();
    }

    /**
     * Sends the wave system back to the beginning.
     */
    public final void reset() {
        currentWaveIndex = 0;
        currentFrame = 0;
        for (Wave wave : waves) {
            wave.reset();
        }
    }

    /**
     * Advances the wave frame by one and lets the current wave spawn any
     * enemies or power-ups that are due. This is the once-per-simulation
     * call the battle screen uses.
     */
    public void update(List<EnemyShip> enemies,
                       List<PowerUp> powerUps,
                       Properties gameProps) {
        currentFrame++;
        if (currentWaveIndex >= waves.size()) {
            return;
        }
        Wave wave = waves.get(currentWaveIndex);
        wave.spawnEnemies(currentFrame, enemies, gameProps);
        wave.spawnPowerUps(currentFrame, powerUps, gameProps);
    }

    /**
     * Gives the current wave's frame counter, used by shooting enemies to
     * decide when to fire.
     */
    public int getCurrentFrame() {
        return currentFrame;
    }

    /**
     * Checks whether the current wave has nothing left to spawn and
     * nothing left on screen.
     */
    public boolean isCurrentWaveComplete(List<EnemyShip> enemies,
                                          List<EnemyProjectile> enemyProjectiles,
                                          List<PowerUp> powerUps) {
        if (currentWaveIndex >= waves.size()) {
            return true;
        }
        Wave current = waves.get(currentWaveIndex);
        return current.hasSpawnedAllObjects()
                && enemies.isEmpty()
                && enemyProjectiles.isEmpty()
                && powerUps.isEmpty();
    }

    /**
     * Starts the next wave if there is one.
     *
     * @return {@code true} when another wave was started
     */
    public boolean moveToNextWave() {
        if (currentWaveIndex >= waves.size()) {
            return false;
        }
        currentWaveIndex++;
        currentFrame = 0;
        return currentWaveIndex < waves.size();
    }

    public boolean areAllWavesComplete() {
        return currentWaveIndex >= waves.size();
    }

    /**
     * Gives the wave number shown in the HUD. Numbering is 1-based and
     * derived from the wave's position in the list, so the first wave is
     * always shown as Wave 1, the second as Wave 2, and so on.
     */
    public int getCurrentWaveNumber() {
        if (waves.isEmpty()) {
            return 1;
        }
        int index = Math.min(currentWaveIndex, waves.size() - 1);
        return index + 1;
    }
}
