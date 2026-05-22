package game.entities;

/**
 * Base for entities that drift on their own each frame. Positive speed
 * moves down; negative speed moves up. By default a moving entity is
 * pruned the moment it falls below the screen, which is what enemies,
 * power-ups, and enemy projectiles all need.
 */
public abstract class MovingEntity extends GameEntity {
    private final double movementSpeed;

    protected MovingEntity(String imagePath, double x, double y, double movementSpeed) {
        super(imagePath, x, y);
        this.movementSpeed = movementSpeed;
    }

    @Override
    public void update() {
        setY(getY() + movementSpeed);
    }

    /**
     * Checks whether this object has dropped below the screen.
     */
    public boolean isOffScreen(double screenHeight) {
        return getY() - getImageHeight() / 2 > screenHeight;
    }

    @Override
    public boolean shouldBeRemoved(double screenHeight) {
        return isOffScreen(screenHeight);
    }

    protected double getMovementSpeed() {
        return movementSpeed;
    }
}
