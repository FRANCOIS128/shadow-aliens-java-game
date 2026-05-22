package game.entities.projectile;

import game.data.GameDataUtils;

import java.util.Properties;

/**
 * Bullet fired by the player. It travels upward and is pruned when it
 * leaves the top of the screen.
 */
public class PlayerProjectile extends Projectile {
    public PlayerProjectile(Properties gameProps, double x, double y) {
        super(gameProps.getProperty("projectile.image"),
                x,
                y,
                -GameDataUtils.parseDouble(gameProps, "projectile.movementSpeed"));
    }

    @Override
    public boolean shouldBeRemoved(double screenHeight) {
        return isOffTopOfScreen();
    }
}
