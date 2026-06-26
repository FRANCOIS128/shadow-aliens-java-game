package game.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PowerUpTypeTest {

    @Test
    void keyIsLowercaseName() {
        assertEquals("shield", PowerUpType.SHIELD.getKey());
        assertEquals("cooldown", PowerUpType.COOLDOWN.getKey());
        assertEquals("engine", PowerUpType.ENGINE.getKey());
        assertEquals("life", PowerUpType.LIFE.getKey());
    }

    @Test
    void fromKeyIsCaseInsensitiveAndTrims() {
        assertEquals(PowerUpType.SHIELD, PowerUpType.fromKey(" shield "));
        assertEquals(PowerUpType.ENGINE, PowerUpType.fromKey("ENGINE"));
    }

    @Test
    void fromKeyRejectsUnknownValue() {
        assertThrows(IllegalArgumentException.class, () -> PowerUpType.fromKey("nuke"));
    }
}
