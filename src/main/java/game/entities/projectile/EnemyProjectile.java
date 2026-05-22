package game.entities.projectile;

import game.data.GameDataUtils;

import java.util.Properties;

/**
 * Bullet fired by a shooting enemy. It travels downward and is pruned
 * when it reaches the bottom of the screen (default {@code MovingEntity}
 * behaviour).
 */
public class EnemyProjectile extends Projectile {
    public EnemyProjectile(Properties gameProps, double x, double y) {
        super(gameProps.getProperty("enemyProjectile.image"),
                x,
                y,
                GameDataUtils.parseDouble(gameProps, "enemyProjectile.movementSpeed"));
    }
}
