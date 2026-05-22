package game.entities.enemy;

import game.data.EnemySpawnInfo;
import game.data.GameDataUtils;
import game.entities.projectile.EnemyProjectile;

import java.util.Properties;

/**
 * Enemy that moves like a regular enemy but fires a bullet on a fixed
 * cadence. Firing is anchored to the wave frame on which the enemy
 * arrived so a shot lands at {@code arrivalFrame + firingRate}, then
 * again every {@code firingRate} frames thereafter.
 */
public class ShootingEnemy extends EnemyShip {
    private final int firingRate;
    private final int arrivalFrame;

    public ShootingEnemy(Properties gameProps, EnemySpawnInfo spawnInfo) {
        super(gameProps, spawnInfo);
        this.firingRate = GameDataUtils.parseInt(gameProps, "enemy.shooting.firingRate");
        this.arrivalFrame = spawnInfo.getArrivalTime();
    }

    @Override
    public boolean canShoot(int frameCount) {
        int elapsed = frameCount - arrivalFrame;
        return elapsed > 0 && elapsed % firingRate == 0;
    }

    @Override
    public EnemyProjectile shoot(Properties gameProps) {
        return new EnemyProjectile(gameProps, getX(), getY());
    }
}
