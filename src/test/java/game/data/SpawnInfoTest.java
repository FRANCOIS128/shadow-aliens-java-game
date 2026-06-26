package game.data;

import game.core.EnemyType;
import game.core.PowerUpType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpawnInfoTest {

    @Test
    void enemySpawnInfoExposesAllFields() {
        EnemySpawnInfo info = new EnemySpawnInfo(2, 60, 120.5, 3.0, EnemyType.STRAFING);
        assertEquals(2, info.getWaveNumber());
        assertEquals(60, info.getArrivalTime());
        assertEquals(120.5, info.getPosX());
        assertEquals(3.0, info.getMovementSpeed());
        assertEquals(EnemyType.STRAFING, info.getType());
    }

    @Test
    void powerUpSpawnInfoExposesAllFields() {
        PowerUpSpawnInfo info = new PowerUpSpawnInfo(3, 90, 200.0, 5.0, PowerUpType.COOLDOWN);
        assertEquals(3, info.getWaveNumber());
        assertEquals(90, info.getArrivalTime());
        assertEquals(200.0, info.getPosX());
        assertEquals(5.0, info.getMovementSpeed());
        assertEquals(PowerUpType.COOLDOWN, info.getType());
    }
}
