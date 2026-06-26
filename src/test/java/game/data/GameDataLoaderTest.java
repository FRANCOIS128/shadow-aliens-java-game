package game.data;

import game.core.EnemyType;
import game.core.PowerUpType;
import game.core.Wave;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameDataLoaderTest {

    private Properties sampleProps() {
        Properties props = new Properties();
        props.setProperty("wave.text", "Wave");

        props.setProperty("wave.1.enemy.0.arrivalTime", "200");
        props.setProperty("wave.1.enemy.0.posX", "100");
        props.setProperty("wave.1.enemy.0.movementSpeed", "2");
        props.setProperty("wave.1.enemy.0.type", "regular");
        props.setProperty("wave.1.enemy.1.arrivalTime", "60");
        props.setProperty("wave.1.enemy.1.posX", "300");
        props.setProperty("wave.1.enemy.1.movementSpeed", "3");
        props.setProperty("wave.1.enemy.1.type", "shooting");

        props.setProperty("wave.1.powerup.0.arrivalTime", "120");
        props.setProperty("wave.1.powerup.0.posX", "250");
        props.setProperty("wave.1.powerup.0.type", "shield");
        props.setProperty("powerup.shield.movementSpeed", "2");

        props.setProperty("wave.2.enemy.0.arrivalTime", "60");
        props.setProperty("wave.2.enemy.0.posX", "120");
        props.setProperty("wave.2.enemy.0.movementSpeed", "4");
        props.setProperty("wave.2.enemy.0.type", "strafing");
        return props;
    }

    @Test
    void loadsAllWavesInNumericOrder() {
        List<Wave> waves = GameDataLoader.loadWaves(sampleProps());
        assertEquals(2, waves.size());
    }

    @Test
    void enemySpawnInfosAreSortedByArrivalTime() {
        List<EnemySpawnInfo> infos = GameDataLoader.loadEnemySpawnInfos(sampleProps(), 1);
        assertEquals(2, infos.size());
        assertEquals(60, infos.get(0).getArrivalTime());
        assertEquals(EnemyType.SHOOTING, infos.get(0).getType());
        assertEquals(200, infos.get(1).getArrivalTime());
        assertEquals(EnemyType.REGULAR, infos.get(1).getType());
    }

    @Test
    void powerUpSpeedIsReadFromPerTypeProperty() {
        List<PowerUpSpawnInfo> infos = GameDataLoader.loadPowerUpSpawnInfos(sampleProps(), 1);
        assertEquals(1, infos.size());
        PowerUpSpawnInfo shield = infos.get(0);
        assertEquals(PowerUpType.SHIELD, shield.getType());
        assertEquals(2.0, shield.getMovementSpeed());
        assertEquals(250.0, shield.getPosX());
    }

    @Test
    void nonNumericWaveKeysAreIgnored() {
        List<Wave> waves = GameDataLoader.loadWaves(sampleProps());
        assertEquals(2, waves.size());
    }
}
