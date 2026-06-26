package game.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnemyTypeTest {

    @Test
    void keyIsLowercaseName() {
        assertEquals("regular", EnemyType.REGULAR.getKey());
        assertEquals("strafing", EnemyType.STRAFING.getKey());
        assertEquals("shooting", EnemyType.SHOOTING.getKey());
        assertEquals("boss", EnemyType.BOSS.getKey());
    }

    @Test
    void fromKeyIsCaseInsensitiveAndTrims() {
        assertEquals(EnemyType.REGULAR, EnemyType.fromKey("  regular "));
        assertEquals(EnemyType.SHOOTING, EnemyType.fromKey("SHOOTING"));
        assertEquals(EnemyType.BOSS, EnemyType.fromKey("boss"));
    }

    @Test
    void fromKeyRejectsUnknownValue() {
        assertThrows(IllegalArgumentException.class, () -> EnemyType.fromKey("mothership"));
    }
}
