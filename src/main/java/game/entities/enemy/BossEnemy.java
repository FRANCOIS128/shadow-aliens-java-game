package game.entities.enemy;

import game.data.EnemySpawnInfo;
import game.data.GameDataUtils;
import game.entities.projectile.EnemyProjectile;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * A multi-hit boss. It descends to a hover line near the top of the screen,
 * then strafes left and right while firing a downward fan of bullets on a
 * fixed cadence. Unlike the basic enemies it survives many hits, so the
 * collision system damages it rather than destroying it on the first shot.
 */
public class BossEnemy extends EnemyShip {
    private final double hoverY;
    private final double strafeSpeed;
    private final double minX;
    private final double maxX;
    private final int firingRate;
    private final int arrivalFrame;
    private final int bulletCount;
    private final double bulletSpeed;
    private final double fanSpreadX;
    private final String bulletImage;
    private int strafeDirection;
    private boolean hasReachedHover;

    public BossEnemy(Properties gameProps, EnemySpawnInfo spawnInfo, double screenWidth) {
        super(gameProps, spawnInfo, GameDataUtils.parseInt(gameProps, "enemy.boss.health"));
        this.hoverY = GameDataUtils.parseDouble(gameProps, "enemy.boss.hoverY");
        this.strafeSpeed = GameDataUtils.parseDouble(gameProps, "enemy.boss.strafeSpeed");
        this.firingRate = GameDataUtils.parseInt(gameProps, "enemy.boss.firingRate");
        this.bulletCount = GameDataUtils.parseInt(gameProps, "enemy.boss.bulletCount");
        this.bulletSpeed = GameDataUtils.parseDouble(gameProps, "enemy.boss.bulletSpeed");
        this.fanSpreadX = GameDataUtils.parseDouble(gameProps, "enemy.boss.fanSpreadX");
        this.bulletImage = gameProps.getProperty("enemy.boss.projectile.image");
        double halfWidth = getImageWidth() / 2;
        this.minX = halfWidth;
        this.maxX = screenWidth - halfWidth;
        this.arrivalFrame = spawnInfo.getArrivalTime();
        this.strafeDirection = 1;
        this.hasReachedHover = false;
    }

    @Override
    public void update() {
        if (!hasReachedHover && getY() < hoverY) {
            super.update();
            if (getY() >= hoverY) {
                hasReachedHover = true;
            }
            return;
        }
        hasReachedHover = true;
        setX(getX() + strafeDirection * strafeSpeed);
        bounceIfAtEdge();
    }

    /**
     * The boss never falls off the bottom; it is only removed once its
     * health is gone.
     */
    @Override
    public boolean shouldBeRemoved(double screenHeight) {
        return isDestroyed();
    }

    @Override
    public int ramDamage() {
        return 2;
    }

    @Override
    public boolean canShoot(int frameCount) {
        if (!hasReachedHover) {
            return false;
        }
        int elapsed = frameCount - arrivalFrame;
        return elapsed > 0 && elapsed % firingRate == 0;
    }

    @Override
    public List<EnemyProjectile> shoot(Properties gameProps) {
        List<EnemyProjectile> fan = new ArrayList<>(bulletCount);
        double startOffset = -fanSpreadX * (bulletCount - 1) / 2.0;
        for (int i = 0; i < bulletCount; i++) {
            double velocityX = startOffset + i * fanSpreadX;
            fan.add(new EnemyProjectile(bulletImage, getX(), getY(), velocityX, bulletSpeed));
        }
        return fan;
    }

    private void bounceIfAtEdge() {
        if (getX() <= minX) {
            setX(minX);
            strafeDirection = 1;
        } else if (getX() >= maxX) {
            setX(maxX);
            strafeDirection = -1;
        }
    }
}
