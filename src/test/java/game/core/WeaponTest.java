package game.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class WeaponTest {

    @Test
    void cannonKeepsBaseCooldown() {
        assertEquals(60, Weapon.CANNON.cooldownFrames(60));
    }

    @Test
    void laserFiresThreeTimesFaster() {
        assertEquals(20, Weapon.LASER.cooldownFrames(60));
    }

    @Test
    void laserCooldownNeverDropsBelowOneFrame() {
        assertEquals(1, Weapon.LASER.cooldownFrames(1));
    }

    @Test
    void homingFiresSlower() {
        assertEquals(120, Weapon.HOMING.cooldownFrames(60));
    }

    @Test
    void selectionMapsOneBasedIndexToWeapon() {
        assertSame(Weapon.CANNON, Weapon.fromSelection(1));
        assertSame(Weapon.SPREAD, Weapon.fromSelection(2));
        assertSame(Weapon.LASER, Weapon.fromSelection(3));
        assertSame(Weapon.HOMING, Weapon.fromSelection(4));
    }

    @Test
    void selectionOutOfRangeReturnsNull() {
        assertNull(Weapon.fromSelection(0));
        assertNull(Weapon.fromSelection(5));
    }

    @Test
    void everyWeaponHasADisplayName() {
        for (Weapon weapon : Weapon.values()) {
            assertEquals(weapon.displayName(), weapon.displayName().trim());
        }
    }
}
