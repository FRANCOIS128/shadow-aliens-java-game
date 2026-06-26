package game.entities.projectile;

import game.data.GameDataUtils;

import java.util.Properties;

/**
 * A player missile that gradually turns toward a target each frame. The
 * battle screen feeds it the nearest enemy's position via
 * {@link #steerToward(double, double)}; the missile rotates its velocity a
 * fraction of the way toward that bearing (the turn rate) while keeping a
 * constant speed, giving a smooth pursuit curve rather than an instant snap.
 */
public class HomingProjectile extends PlayerProjectile {
    private final double speed;
    private final double turnRate;

    public HomingProjectile(Properties gameProps, double x, double y) {
        super(gameProps.getProperty("projectile.homing.image"),
                x,
                y,
                0.0,
                -GameDataUtils.parseDouble(gameProps, "projectile.homing.movementSpeed"),
                1);
        this.speed = GameDataUtils.parseDouble(gameProps, "projectile.homing.movementSpeed");
        this.turnRate = GameDataUtils.parseDouble(gameProps, "projectile.homing.turnRate");
    }

    /**
     * Nudges the missile's velocity toward the target point, blending the
     * current heading with the ideal heading by the turn rate and then
     * renormalising back to the constant missile speed.
     */
    public void steerToward(double targetX, double targetY) {
        double deltaX = targetX - getX();
        double deltaY = targetY - getY();
        double distance = Math.hypot(deltaX, deltaY);
        if (distance < 1e-6) {
            return;
        }
        double idealVelocityX = deltaX / distance * speed;
        double idealVelocityY = deltaY / distance * speed;
        double blendedX = getVelocityX() + (idealVelocityX - getVelocityX()) * turnRate;
        double blendedY = getVelocityY() + (idealVelocityY - getVelocityY()) * turnRate;
        double blendedLength = Math.hypot(blendedX, blendedY);
        if (blendedLength < 1e-6) {
            return;
        }
        setVelocity(blendedX / blendedLength * speed, blendedY / blendedLength * speed);
    }
}
