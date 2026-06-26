package game.entities.projectile;

import game.data.GameDataUtils;

import java.util.Properties;

/**
 * Bullet fired by the player. It travels along a 2D velocity so the same
 * class can back a straight cannon shot, an angled spread pellet, or a fast
 * laser bolt. Subclasses (e.g. the homing missile) reuse the velocity model
 * but adjust their direction over time.
 */
public class PlayerProjectile extends Projectile {
    private double velocityX;
    private double velocityY;
    private final int damage;

    public PlayerProjectile(String imagePath,
                            double x,
                            double y,
                            double velocityX,
                            double velocityY,
                            int damage) {
        super(imagePath, x, y, velocityY);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.damage = damage;
    }

    /**
     * Convenience constructor for the default straight-up cannon shot,
     * kept so existing callers and the data file's {@code projectile.*}
     * keys continue to work.
     */
    public PlayerProjectile(Properties gameProps, double x, double y) {
        this(gameProps.getProperty("projectile.image"),
                x,
                y,
                0.0,
                -GameDataUtils.parseDouble(gameProps, "projectile.movementSpeed"),
                1);
    }

    /**
     * Gives how many hit points this bullet removes from an enemy.
     */
    public int getDamage() {
        return damage;
    }

    @Override
    public void update() {
        setX(getX() + velocityX);
        setY(getY() + velocityY);
    }

    @Override
    public boolean shouldBeRemoved(double screenHeight) {
        return isOffTopOfScreen() || isOffScreen(screenHeight);
    }

    protected void setVelocity(double velocityX, double velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    protected double getVelocityX() {
        return velocityX;
    }

    protected double getVelocityY() {
        return velocityY;
    }
}
