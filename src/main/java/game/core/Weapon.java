package game.core;

import game.data.GameDataUtils;
import game.entities.projectile.HomingProjectile;
import game.entities.projectile.PlayerProjectile;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * The selectable player weapons. Each constant knows how to build the set
 * of projectiles fired in one shot and how its fire rate relates to the
 * base shoot cooldown, so the player and battle screen never switch on the
 * weapon kind themselves. Adding a weapon is one new constant plus, if it
 * needs new behaviour, one new {@link PlayerProjectile} subclass.
 */
public enum Weapon {
    /** Single straight shot — the default balanced weapon. */
    CANNON("CANNON") {
        @Override
        public List<PlayerProjectile> fire(Properties gameProps, double x, double y) {
            return List.of(new PlayerProjectile(gameProps, x, y));
        }

        @Override
        public int cooldownFrames(int baseCooldown) {
            return baseCooldown;
        }
    },

    /** Three pellets fanned out — high coverage, normal fire rate. */
    SPREAD("SPREAD") {
        @Override
        public List<PlayerProjectile> fire(Properties gameProps, double x, double y) {
            String image = gameProps.getProperty("projectile.image");
            double speed = GameDataUtils.parseDouble(gameProps, "projectile.movementSpeed");
            double spreadX = GameDataUtils.parseDouble(gameProps, "weapon.spread.spreadX");
            List<PlayerProjectile> shots = new ArrayList<>(3);
            shots.add(new PlayerProjectile(image, x, y, 0.0, -speed, 1));
            shots.add(new PlayerProjectile(image, x, y, -spreadX, -speed, 1));
            shots.add(new PlayerProjectile(image, x, y, spreadX, -speed, 1));
            return shots;
        }

        @Override
        public int cooldownFrames(int baseCooldown) {
            return baseCooldown;
        }
    },

    /** Fast single bolt with a short cooldown — high rate of fire. */
    LASER("LASER") {
        @Override
        public List<PlayerProjectile> fire(Properties gameProps, double x, double y) {
            String image = gameProps.getProperty("projectile.laser.image");
            double speed = GameDataUtils.parseDouble(gameProps, "projectile.laser.movementSpeed");
            return List.of(new PlayerProjectile(image, x, y, 0.0, -speed, 1));
        }

        @Override
        public int cooldownFrames(int baseCooldown) {
            return Math.max(1, baseCooldown / 3);
        }
    },

    /** Guided missile that tracks the nearest enemy — slow fire rate. */
    HOMING("HOMING") {
        @Override
        public List<PlayerProjectile> fire(Properties gameProps, double x, double y) {
            return List.of(new HomingProjectile(gameProps, x, y));
        }

        @Override
        public int cooldownFrames(int baseCooldown) {
            return baseCooldown * 2;
        }
    };

    private final String displayName;

    Weapon(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Builds the projectiles produced by one trigger pull at the ship's
     * muzzle position.
     */
    public abstract List<PlayerProjectile> fire(Properties gameProps, double x, double y);

    /**
     * Converts the base shoot cooldown into this weapon's cooldown, which is
     * how each weapon gets its own rate of fire.
     */
    public abstract int cooldownFrames(int baseCooldown);

    public String displayName() {
        return displayName;
    }

    /**
     * Maps a 1-based selection (e.g. the 1–4 number keys) to a weapon,
     * returning {@code null} when the index is out of range so the caller
     * can simply ignore unrelated key presses.
     */
    public static Weapon fromSelection(int oneBasedIndex) {
        Weapon[] values = values();
        if (oneBasedIndex < 1 || oneBasedIndex > values.length) {
            return null;
        }
        return values[oneBasedIndex - 1];
    }
}
