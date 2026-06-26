package game.entities.projectile;

import game.data.GameDataUtils;

import java.util.Properties;

/**
 * Bullet fired by an enemy. A plain shooting enemy fires it straight down,
 * but the velocity constructor lets a boss fan bullets out at angles for a
 * bullet-pattern attack.
 */
public class EnemyProjectile extends Projectile {
    private final double velocityX;
    private final double velocityY;

    public EnemyProjectile(Properties gameProps, double x, double y) {
        this(gameProps.getProperty("enemyProjectile.image"),
                x,
                y,
                0.0,
                GameDataUtils.parseDouble(gameProps, "enemyProjectile.movementSpeed"));
    }

    public EnemyProjectile(String imagePath,
                           double x,
                           double y,
                           double velocityX,
                           double velocityY) {
        super(imagePath, x, y, velocityY);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    @Override
    public void update() {
        setX(getX() + velocityX);
        setY(getY() + velocityY);
    }

    @Override
    public boolean shouldBeRemoved(double screenHeight) {
        return isOffScreen(screenHeight) || isOffTopOfScreen();
    }
}
