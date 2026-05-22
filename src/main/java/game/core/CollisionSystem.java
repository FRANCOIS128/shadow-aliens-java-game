package game.core;

import game.entities.Explosion;
import game.entities.Player;
import game.entities.enemy.EnemyShip;
import game.entities.powerup.PowerUp;
import game.entities.projectile.EnemyProjectile;
import game.entities.projectile.PlayerProjectile;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Centralises every collision check the battle screen has to run each
 * frame. The battle screen passes in the live entity lists, score, and
 * explosion buffer, and these methods remove anything that gets hit, add
 * the matching explosion, and update the score.
 *
 * <p>The class holds a reference to {@code gameProps} so the public method
 * signatures match the UML exactly (no per-call {@code Properties}
 * argument). Storing it once also means new explosions can be built
 * without threading the data file through the battle screen on every
 * call.
 */
public final class CollisionSystem {
    private final Properties gameProps;

    public CollisionSystem(Properties gameProps) {
        this.gameProps = gameProps;
    }

    /**
     * Handles the player ship running into enemy ships. The enemy is
     * removed and replaced with a large explosion. If the player is not
     * invincible, a life is lost and the score is reduced.
     */
    public void handlePlayerEnemyCollisions(List<EnemyShip> enemies,
                                            Player player,
                                            List<Explosion> explosions,
                                            ScoreManager score) {
        Iterator<EnemyShip> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            EnemyShip enemy = iterator.next();
            if (!player.getBoundingBox().intersects(enemy.getBoundingBox())) {
                continue;
            }
            explosions.add(new Explosion(gameProps, enemy.getX(), enemy.getY(), ExplosionSize.LARGE));
            if (player.takeHit()) {
                score.subtractPlayerHitScore();
            }
            iterator.remove();
        }
    }

    /**
     * Handles enemy projectiles hitting the player ship. The projectile
     * is destroyed with a small explosion. If the player is not
     * invincible, a life is lost and the score is reduced.
     */
    public void handlePlayerEnemyProjectileCollisions(List<EnemyProjectile> enemyProjectiles,
                                                      Player player,
                                                      List<Explosion> explosions,
                                                      ScoreManager score) {
        Iterator<EnemyProjectile> iterator = enemyProjectiles.iterator();
        while (iterator.hasNext()) {
            EnemyProjectile projectile = iterator.next();
            if (!player.getBoundingBox().intersects(projectile.getBoundingBox())) {
                continue;
            }
            explosions.add(new Explosion(gameProps, projectile.getX(), projectile.getY(), ExplosionSize.SMALL));
            if (player.takeHit()) {
                score.subtractPlayerHitScore();
            }
            iterator.remove();
        }
    }

    /**
     * Handles the player picking up power-ups. The power-up is removed
     * from the world, applied to the player, and the pickup score is
     * added.
     */
    public void handlePlayerPowerUpCollisions(List<PowerUp> powerUps,
                                              Player player,
                                              ScoreManager score) {
        Iterator<PowerUp> iterator = powerUps.iterator();
        while (iterator.hasNext()) {
            PowerUp powerUp = iterator.next();
            if (!player.getBoundingBox().intersects(powerUp.getBoundingBox())) {
                continue;
            }
            player.activatePowerUp(powerUp);
            score.addPowerUpScore();
            iterator.remove();
        }
    }

    /**
     * Handles player projectiles hitting enemy ships. Both the projectile
     * and the enemy are removed, a large explosion is shown at the
     * enemy's last position, and the kill score for that enemy type is
     * added.
     */
    public void handlePlayerProjectileEnemyCollisions(List<PlayerProjectile> playerProjectiles,
                                                      List<EnemyShip> enemies,
                                                      List<Explosion> explosions,
                                                      ScoreManager score) {
        Iterator<PlayerProjectile> projectileIterator = playerProjectiles.iterator();
        while (projectileIterator.hasNext()) {
            PlayerProjectile projectile = projectileIterator.next();
            boolean hit = false;
            Iterator<EnemyShip> enemyIterator = enemies.iterator();
            while (enemyIterator.hasNext()) {
                EnemyShip enemy = enemyIterator.next();
                if (!projectile.getBoundingBox().intersects(enemy.getBoundingBox())) {
                    continue;
                }
                explosions.add(new Explosion(gameProps, enemy.getX(), enemy.getY(), ExplosionSize.LARGE));
                score.addDestroyedEnemyScore(enemy.getType());
                enemyIterator.remove();
                hit = true;
                break;
            }
            if (hit) {
                projectileIterator.remove();
            }
        }
    }

    /**
     * Handles player projectiles cancelling enemy projectiles in mid-air.
     * Both projectiles are removed, a small explosion is shown at the
     * enemy projectile's position, and the deflection score is added.
     */
    public void handleProjectileProjectileCollisions(List<PlayerProjectile> playerProjectiles,
                                                     List<EnemyProjectile> enemyProjectiles,
                                                     List<Explosion> explosions,
                                                     ScoreManager score) {
        Iterator<PlayerProjectile> playerIterator = playerProjectiles.iterator();
        while (playerIterator.hasNext()) {
            PlayerProjectile playerProjectile = playerIterator.next();
            boolean hit = false;
            Iterator<EnemyProjectile> enemyIterator = enemyProjectiles.iterator();
            while (enemyIterator.hasNext()) {
                EnemyProjectile enemyProjectile = enemyIterator.next();
                if (!playerProjectile.getBoundingBox().intersects(enemyProjectile.getBoundingBox())) {
                    continue;
                }
                explosions.add(new Explosion(gameProps, enemyProjectile.getX(), enemyProjectile.getY(),
                        ExplosionSize.SMALL));
                score.addHitProjectileScore();
                enemyIterator.remove();
                hit = true;
                break;
            }
            if (hit) {
                playerIterator.remove();
            }
        }
    }
}
