package game.entities.enemy;

import game.data.EnemySpawnInfo;

import java.util.Properties;

/**
 * Enemy that moves down while sliding sideways, bouncing off the left and
 * right edges. The bouncing logic is in {@link #bounceIfAtEdge()} so the
 * {@link #update()} method stays small and matches the UML.
 */
public class StrafingEnemy extends EnemyShip {
    private final double minX;
    private final double maxX;
    private int xDirection;

    public StrafingEnemy(Properties gameProps, EnemySpawnInfo spawnInfo, double screenWidth) {
        super(gameProps, spawnInfo);
        double halfWidth = getImageWidth() / 2;
        this.minX = halfWidth;
        this.maxX = screenWidth - halfWidth;
        this.xDirection = chooseInitialDirection(spawnInfo.getPosX(), screenWidth);
    }

    @Override
    public void update() {
        super.update();
        double horizontalSpeed = Math.abs(getMovementSpeed());
        setX(getX() + xDirection * horizontalSpeed);
        bounceIfAtEdge();
    }

    /**
     * Clamps the ship back inside the window and flips its horizontal
     * direction whenever it touches an edge.
     */
    private void bounceIfAtEdge() {
        if (getX() <= minX) {
            setX(minX);
            xDirection = 1;
        } else if (getX() >= maxX) {
            setX(maxX);
            xDirection = -1;
        }
    }

    private static int chooseInitialDirection(double spawnX, double screenWidth) {
        double distanceToLeft = spawnX;
        double distanceToRight = screenWidth - spawnX;
        return distanceToLeft <= distanceToRight ? -1 : 1;
    }
}
