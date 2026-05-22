package game.entities;

import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Common base for everything the game shows on screen. Holds the image and
 * centre position, and exposes a polymorphic {@link #update()} hook that
 * the battle loop drives once per simulation step. {@link #shouldBeRemoved}
 * lets every subclass say when it is finished — projectiles when they
 * leave the screen, explosions when they time out, and so on — so the
 * battle screen can prune entity lists with one generic loop instead of
 * one bespoke loop per type.
 */
public abstract class GameEntity {
    private final Image image;
    private double x;
    private double y;

    protected GameEntity(String imagePath, double x, double y) {
        this.image = new Image(imagePath);
        this.x = x;
        this.y = y;
    }

    /**
     * Advances this entity by one simulation step.
     */
    public abstract void update();

    /**
     * Tells the battle screen whether this entity is finished and should
     * be removed from its list. The default is {@code false}; subclasses
     * (moving entities, explosions) override to encode their own rule.
     */
    public boolean shouldBeRemoved(double screenHeight) {
        return false;
    }

    /**
     * Draws this entity at its current position.
     */
    public void render() {
        image.draw(x, y);
    }

    /**
     * Gives the rectangle used for collision checks.
     */
    public Rectangle getBoundingBox() {
        return image.getBoundingBoxAt(new Point(x, y));
    }

    public final double getX() {
        return x;
    }

    public final double getY() {
        return y;
    }

    protected final void setX(double x) {
        this.x = x;
    }

    protected final void setY(double y) {
        this.y = y;
    }

    protected final double getImageHeight() {
        return image.getHeight();
    }

    protected final double getImageWidth() {
        return image.getWidth();
    }
}
