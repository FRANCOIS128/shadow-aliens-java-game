package game.entities;

import bagel.Image;
import bagel.Input;
import bagel.Keys;

import game.core.Weapon;
import game.data.GameDataUtils;
import game.entities.powerup.PowerUp;
import game.entities.projectile.PlayerProjectile;

import java.util.List;
import java.util.Properties;

/**
 * The player's ship. Holds lives, movement speed, shoot cooldown, and the
 * short window of invincibility that follows a hit. The class also owns
 * the single "active power-up" slot — applying a new power-up replaces
 * any earlier one — so the rest of the game does not have to know which
 * effect is currently in force. Field names follow the UML.
 */
public class Player extends GameEntity {
    private final Image invincibilityImage;
    private final double baseSpeed;
    private double currentSpeed;
    private final double initialX;
    private final double minX;
    private final double maxX;
    private final int initialLives;
    private int remainingLives;
    private final int baseShootCooldown;
    private int currentShootCooldown;
    private int cooldownRemaining;
    private int invincibilityRemaining;
    private final int hitInvincibilityFrames;
    private boolean shieldActive;
    private boolean devInvincible;
    private PowerUp activePowerUp;
    private Weapon currentWeapon;

    public Player(Properties gameProps, double screenWidth) {
        super(gameProps.getProperty("player.image"),
                screenWidth / 2,
                GameDataUtils.parseDouble(gameProps, "player.posY"));
        this.invincibilityImage = new Image(gameProps.getProperty("invincibility.image"));
        this.baseSpeed = GameDataUtils.parseDouble(gameProps, "player.speed");
        this.baseShootCooldown = GameDataUtils.parseInt(gameProps, "player.shootCooldown");
        this.initialLives = GameDataUtils.parseInt(gameProps, "player.initialLives");
        this.hitInvincibilityFrames = GameDataUtils.parseInt(gameProps, "player.hitInvincibilityTime");
        this.initialX = screenWidth / 2;

        double halfWidth = getImageWidth() / 2;
        this.minX = halfWidth;
        this.maxX = screenWidth - halfWidth;

        reset();
    }

    /**
     * Puts the player back to a fresh starting state.
     */
    public final void reset() {
        setX(initialX);
        currentSpeed = baseSpeed;
        currentShootCooldown = baseShootCooldown;
        cooldownRemaining = 0;
        remainingLives = initialLives;
        invincibilityRemaining = 0;
        shieldActive = false;
        devInvincible = false;
        activePowerUp = null;
        currentWeapon = Weapon.CANNON;
    }

    /**
     * Moves the ship left, stopping at the window edge.
     */
    public void moveLeft() {
        setX(Math.max(minX, getX() - currentSpeed));
    }

    /**
     * Moves the ship right, stopping at the window edge.
     */
    public void moveRight() {
        setX(Math.min(maxX, getX() + currentSpeed));
    }

    /**
     * Reads the A and D keys and moves the ship accordingly. Holding both
     * keys leaves the ship where it is. Used by every screen so the
     * input rule is not copy-pasted around.
     */
    public void handleHorizontalInput(Input input) {
        boolean left = input.isDown(Keys.A);
        boolean right = input.isDown(Keys.D);
        if (left && !right) {
            moveLeft();
        } else if (right && !left) {
            moveRight();
        }
    }

    /**
     * Ticks down the shoot cooldown and hit-recovery timer, and counts
     * down the currently active power-up if there is one.
     */
    @Override
    public void update() {
        if (cooldownRemaining > 0) {
            cooldownRemaining--;
        }
        if (invincibilityRemaining > 0) {
            invincibilityRemaining--;
        }
        if (activePowerUp != null) {
            activePowerUp.tickActive();
            if (activePowerUp.isExpired()) {
                activePowerUp.expire(this);
                activePowerUp = null;
            }
        }
    }

    @Override
    public void render() {
        super.render();
        if (isInvincible()) {
            invincibilityImage.draw(getX(), getY());
        }
    }

    /**
     * Fires the current weapon, producing one or more bullets at the
     * centre of the ship.
     */
    public List<PlayerProjectile> shoot(Properties gameProps) {
        return currentWeapon.fire(gameProps, getX(), getY());
    }

    public boolean canShoot() {
        return cooldownRemaining == 0;
    }

    /**
     * Starts the cooldown for the current weapon, derived from the base
     * cooldown (which the cooldown power-up may have shortened).
     */
    public void startShootCooldown() {
        cooldownRemaining = currentWeapon.cooldownFrames(currentShootCooldown);
    }

    /**
     * Switches the active weapon. The new weapon takes effect on the next
     * shot.
     */
    public void selectWeapon(Weapon weapon) {
        this.currentWeapon = weapon;
    }

    public Weapon getWeapon() {
        return currentWeapon;
    }

    /**
     * Tries to damage the player. If the ship is invincible nothing
     * happens. Otherwise a life is removed and the short hit-recovery
     * window begins.
     *
     * @return {@code true} if the player actually lost a life
     */
    public boolean takeHit() {
        if (isInvincible()) {
            return false;
        }
        loseLife();
        invincibilityRemaining = hitInvincibilityFrames;
        return true;
    }

    /**
     * Removes one life if the player still has any.
     */
    public void loseLife() {
        if (remainingLives > 0) {
            remainingLives--;
        }
    }

    /**
     * Adds one life, capped at the starting life count.
     */
    public void addLife() {
        if (remainingLives < initialLives) {
            remainingLives++;
        }
    }

    /**
     * Picks up a power-up and applies its effect. If a different power-up
     * is already active, it is expired first so only one effect runs at
     * a time. Power-ups with no active duration finish in this same call.
     */
    public void activatePowerUp(PowerUp powerUp) {
        if (activePowerUp != null) {
            activePowerUp.expire(this);
            activePowerUp = null;
        }
        powerUp.applyTo(this);
        if (powerUp.isExpired()) {
            powerUp.expire(this);
        } else {
            activePowerUp = powerUp;
        }
    }

    public boolean isInvincible() {
        return shieldActive || devInvincible || invincibilityRemaining > 0;
    }

    public boolean isAlive() {
        return remainingLives > 0;
    }

    public int getRemainingLives() {
        return remainingLives;
    }

    public void setShieldActive(boolean shieldActive) {
        this.shieldActive = shieldActive;
    }

    /**
     * Lets the battle screen mirror its dev-mode flag onto the ship so
     * that {@link #isInvincible()} can give a single answer regardless of
     * which source granted the invincibility.
     */
    public void setDevInvincible(boolean devInvincible) {
        this.devInvincible = devInvincible;
    }

    /**
     * Adjusts the player's movement speed. {@code 1.0} restores the base
     * speed defined in the data file.
     */
    public void setSpeedMultiplier(double multiplier) {
        this.currentSpeed = baseSpeed * multiplier;
    }

    /**
     * Shortens (or restores) the shoot cooldown by the given divider, with
     * a hard floor of one frame so the player cannot shoot every frame
     * forever.
     */
    public void setShootCooldownDivider(int divider) {
        if (divider <= 0) {
            divider = 1;
        }
        int reduced = Math.max(1, baseShootCooldown / divider);
        this.currentShootCooldown = reduced;
        if (cooldownRemaining > reduced) {
            cooldownRemaining = reduced;
        }
    }
}
