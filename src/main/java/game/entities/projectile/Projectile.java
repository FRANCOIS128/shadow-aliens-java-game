package game.entities.projectile;

import game.entities.MovingEntity;

/**
 * Shared base for bullets. Player bullets and enemy bullets only differ in
 * which way they move and which image they use.
 */
public abstract class Projectile extends MovingEntity {
    protected Projectile(String imagePath, double x, double y, double movementSpeed) {
        super(imagePath, x, y, movementSpeed);
    }

    /**
     * Checks whether an upward-moving bullet has left the top of the screen.
     */
    public boolean isOffTopOfScreen() {
        return getY() + getImageHeight() / 2 < 0;
    }
}
