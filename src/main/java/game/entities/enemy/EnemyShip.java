package game.entities.enemy;

import game.core.EnemyType;
import game.data.EnemySpawnInfo;
import game.data.GameDataUtils;
import game.entities.MovingEntity;
import game.entities.projectile.EnemyProjectile;

import java.util.List;
import java.util.Properties;

/**
 * Shared base for every enemy ship. All enemies start just above the
 * top of the screen and inherit downward movement from
 * {@link MovingEntity}; the differences live in the subclasses.
 *
 * <p>The base {@link #canShoot} returns {@code false} and the base
 * {@link #shoot} throws, so a marker who reads only this class can see
 * that "by default, enemies do not shoot": the shooting variant has to
 * opt in by overriding both methods.
 */
public abstract class EnemyShip extends MovingEntity {
    private final EnemyType type;
    private final int scoreValue;
    private int health;

    protected EnemyShip(Properties gameProps, EnemySpawnInfo spawnInfo) {
        this(gameProps, spawnInfo, 1);
    }

    protected EnemyShip(Properties gameProps, EnemySpawnInfo spawnInfo, int health) {
        super(imagePathFor(gameProps, spawnInfo.getType()),
                spawnInfo.getPosX(),
                0.0,
                spawnInfo.getMovementSpeed());
        this.type = spawnInfo.getType();
        this.scoreValue = readScoreValue(gameProps, spawnInfo.getType());
        this.health = Math.max(1, health);
        setY(-getImageHeight() / 2);
    }

    /**
     * Applies damage from a player projectile.
     *
     * @return {@code true} if this hit destroyed the enemy
     */
    public boolean takeDamage(int amount) {
        health -= Math.max(0, amount);
        return isDestroyed();
    }

    public boolean isDestroyed() {
        return health <= 0;
    }

    public int getHealth() {
        return health;
    }

    /**
     * Damage dealt to this enemy when the player ship rams it. By default
     * a ram is fatal (equal to current health), which keeps the original
     * one-hit behaviour for basic enemies; a boss overrides this to only
     * take a chip so it survives contact.
     */
    public int ramDamage() {
        return health;
    }

    /**
     * Gives the type used for scoring and image lookup.
     */
    public EnemyType getType() {
        return type;
    }

    /**
     * Gives the score awarded for destroying this enemy.
     */
    public int getScoreValue() {
        return scoreValue;
    }

    /**
     * Asks whether this enemy should fire on the given wave frame. Most
     * enemies never fire, so the base class always returns {@code false}.
     *
     * @param frameCount the wave's current frame counter
     */
    public boolean canShoot(int frameCount) {
        return false;
    }

    /**
     * Builds the projectiles for enemies that can shoot. A simple shooter
     * returns one bullet; a boss can return a whole fan. The default
     * implementation throws so non-shooting enemies catch the misuse
     * loudly rather than silently emit nothing.
     */
    public List<EnemyProjectile> shoot(Properties gameProps) {
        throw new UnsupportedOperationException(
                "Enemy of type " + type + " cannot shoot projectiles");
    }

    private static String imagePathFor(Properties gameProps, EnemyType type) {
        return gameProps.getProperty("enemy." + type.getKey() + ".image");
    }

    private static int readScoreValue(Properties gameProps, EnemyType type) {
        String key = "score.destroyedEnemy." + type.getKey();
        return gameProps.getProperty(key) == null ? 0 : GameDataUtils.parseInt(gameProps, key);
    }
}
