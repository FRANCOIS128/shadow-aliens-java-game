package game.core;

import game.data.EnemySpawnInfo;
import game.data.PowerUpSpawnInfo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WaveTest {

    @Test
    void emptyWaveHasSpawnedEverythingImmediately() {
        Wave wave = new Wave(new ArrayList<>(), new ArrayList<>());
        assertTrue(wave.hasSpawnedAllObjects());
    }

    @Test
    void waveWithPendingEnemyHasNotSpawnedEverything() {
        List<EnemySpawnInfo> enemies = new ArrayList<>();
        enemies.add(new EnemySpawnInfo(1, 60, 100, 2, EnemyType.REGULAR));
        Wave wave = new Wave(enemies, new ArrayList<>());
        assertFalse(wave.hasSpawnedAllObjects());
    }

    @Test
    void waveWithPendingPowerUpHasNotSpawnedEverything() {
        List<PowerUpSpawnInfo> powerUps = new ArrayList<>();
        powerUps.add(new PowerUpSpawnInfo(1, 60, 100, 2, PowerUpType.SHIELD));
        Wave wave = new Wave(new ArrayList<>(), powerUps);
        assertFalse(wave.hasSpawnedAllObjects());
    }
}
