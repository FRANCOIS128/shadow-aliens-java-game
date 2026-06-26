package game.core;

import game.entities.enemy.EnemyShip;
import game.entities.powerup.PowerUp;
import game.entities.projectile.EnemyProjectile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WaveManagerTest {

    private WaveManager waveManager;

    @BeforeEach
    void setUp() {
        // Two waves whose enemies arrive far in the future, so we can drive
        // wave-progression logic without ever triggering an actual spawn
        // (which would need the graphics context to load an image).
        Properties props = new Properties();
        props.setProperty("wave.1.enemy.0.arrivalTime", "100000");
        props.setProperty("wave.1.enemy.0.posX", "100");
        props.setProperty("wave.1.enemy.0.movementSpeed", "2");
        props.setProperty("wave.1.enemy.0.type", "regular");
        props.setProperty("wave.2.enemy.0.arrivalTime", "100000");
        props.setProperty("wave.2.enemy.0.posX", "100");
        props.setProperty("wave.2.enemy.0.movementSpeed", "2");
        props.setProperty("wave.2.enemy.0.type", "regular");
        waveManager = new WaveManager(props);
    }

    @Test
    void startsOnWaveOne() {
        assertEquals(1, waveManager.getCurrentWaveNumber());
        assertFalse(waveManager.areAllWavesComplete());
    }

    @Test
    void moveToNextWaveAdvancesUntilWavesRunOut() {
        assertTrue(waveManager.moveToNextWave());
        assertEquals(2, waveManager.getCurrentWaveNumber());
        assertFalse(waveManager.moveToNextWave());
        assertTrue(waveManager.areAllWavesComplete());
    }

    @Test
    void waveIsIncompleteWhilePendingSpawnsRemain() {
        List<EnemyShip> enemies = new ArrayList<>();
        List<EnemyProjectile> projectiles = new ArrayList<>();
        List<PowerUp> powerUps = new ArrayList<>();
        assertFalse(waveManager.isCurrentWaveComplete(enemies, projectiles, powerUps));
    }

    @Test
    void allWavesCompleteReportAsComplete() {
        waveManager.moveToNextWave();
        waveManager.moveToNextWave();
        List<EnemyShip> enemies = new ArrayList<>();
        List<EnemyProjectile> projectiles = new ArrayList<>();
        List<PowerUp> powerUps = new ArrayList<>();
        assertTrue(waveManager.isCurrentWaveComplete(enemies, projectiles, powerUps));
    }

    @Test
    void resetReturnsToFirstWave() {
        waveManager.moveToNextWave();
        waveManager.reset();
        assertEquals(1, waveManager.getCurrentWaveNumber());
        assertFalse(waveManager.areAllWavesComplete());
    }
}
