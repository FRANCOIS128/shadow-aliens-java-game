package game.entities;

import game.core.ExplosionSize;
import game.data.GameDataUtils;

import java.util.Properties;

/**
 * A short on-screen effect shown wherever something is destroyed. The
 * explosion does not move and is removed from the world as soon as its
 * timer reaches zero.
 */
public class Explosion extends GameEntity {
    private final ExplosionSize size;
    private final int displayDurationFrames;
    private int remainingDisplayFrames;

    public Explosion(Properties gameProps, double x, double y, ExplosionSize size) {
        super(gameProps.getProperty("explosion." + size.getKey() + ".image"), x, y);
        this.size = size;
        this.displayDurationFrames = GameDataUtils.parseInt(gameProps,
                "explosion." + size.getKey() + ".duration");
        this.remainingDisplayFrames = displayDurationFrames;
    }

    public ExplosionSize getSize() {
        return size;
    }

    @Override
    public void update() {
        if (remainingDisplayFrames > 0) {
            remainingDisplayFrames--;
        }
    }

    public boolean hasFinished() {
        return remainingDisplayFrames <= 0;
    }

    @Override
    public boolean shouldBeRemoved(double screenHeight) {
        return hasFinished();
    }
}
