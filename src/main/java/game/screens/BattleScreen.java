package game.screens;

import bagel.Input;
import bagel.Keys;

import game.core.CollisionSystem;
import game.core.ScoreManager;
import game.core.ScreenState;
import game.core.TimeScaleController;
import game.core.WaveManager;
import game.core.Weapon;
import game.entities.Explosion;
import game.entities.GameEntity;
import game.entities.Player;
import game.entities.enemy.EnemyShip;
import game.entities.powerup.PowerUp;
import game.entities.projectile.EnemyProjectile;
import game.entities.projectile.HomingProjectile;
import game.entities.projectile.PlayerProjectile;
import game.ui.HeadInfoDisplayRenderer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * The main gameplay screen. It owns the player, the live entity lists,
 * and the subsystems that handle scoring, waves, dev-mode timing, and
 * collisions, and delegates each frame's work to them. Polymorphism on
 * {@link GameEntity#update()} and {@link GameEntity#shouldBeRemoved}
 * means most entity types share a single update-and-prune loop
 * ({@link #updateAndPrune(List)}), so adding a new kind of moving entity
 * does not require a new bespoke loop here.
 *
 * <p>The dev-mode timescale is applied by running
 * {@link #simulateOneFrame(Input)} a variable number of times per drawn
 * frame, so every moving object — including the player ship — speeds up
 * or slows down together.
 */
public class BattleScreen implements GameScreen {
    private final Properties gameProps;
    private final double screenHeight;
    private final Player player;
    private final List<PlayerProjectile> playerProjectiles;
    private final List<EnemyProjectile> enemyProjectiles;
    private final List<EnemyShip> enemies;
    private final List<PowerUp> powerUps;
    private final List<Explosion> explosions;
    private final TimeScaleController timeScaleController;
    private final WaveManager waveManager;
    private final ScoreManager scoreManager;
    private final CollisionSystem collisionSystem;
    private final HeadInfoDisplayRenderer hudRenderer;

    private boolean devInvincible;
    private boolean wantToShoot;

    public BattleScreen(Properties gameProps, double screenWidth, double screenHeight) {
        this.gameProps = gameProps;
        this.screenHeight = screenHeight;
        this.player = new Player(gameProps, screenWidth);
        this.playerProjectiles = new ArrayList<>();
        this.enemyProjectiles = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.powerUps = new ArrayList<>();
        this.explosions = new ArrayList<>();
        this.timeScaleController = new TimeScaleController();
        this.waveManager = new WaveManager(gameProps);
        this.scoreManager = new ScoreManager(gameProps);
        this.collisionSystem = new CollisionSystem(gameProps);
        this.hudRenderer = new HeadInfoDisplayRenderer(gameProps);
        reset();
    }

    /**
     * Puts the battle back into a brand-new game state.
     */
    public final void reset() {
        wantToShoot = false;
        devInvincible = false;
        playerProjectiles.clear();
        enemyProjectiles.clear();
        enemies.clear();
        powerUps.clear();
        explosions.clear();
        player.reset();
        timeScaleController.reset();
        waveManager.reset();
        scoreManager.reset();
    }

    @Override
    public ScreenState update(Input input) {
        ScreenState devResult = handleDebugKeys(input);
        if (devResult != null) {
            return devResult;
        }
        if (input.wasPressed(Keys.ESCAPE)) {
            return ScreenState.PAUSE;
        }
        handleWeaponSelection(input);
        if (input.wasPressed(Keys.SPACE) && player.canShoot()) {
            wantToShoot = true;
        }
        int simulationSteps = timeScaleController.nextSimulationStepsForRenderFrame();
        for (int step = 0; step < simulationSteps; step++) {
            ScreenState stepResult = simulateOneFrame(input);
            if (stepResult != ScreenState.BATTLE) {
                return stepResult;
            }
        }
        return ScreenState.BATTLE;
    }

    /**
     * Handles only the dev keys while the pause screen is open, without
     * advancing the simulation.
     */
    public ScreenState handleDevKeysWhilePaused(Input input) {
        return handleDebugKeys(input);
    }

    @Override
    public void render() {
        explosions.forEach(Explosion::render);
        powerUps.forEach(PowerUp::render);
        player.render();
        enemies.forEach(EnemyShip::render);
        enemyProjectiles.forEach(EnemyProjectile::render);
        playerProjectiles.forEach(PlayerProjectile::render);
        hudRenderer.render(scoreManager.getScore(), player.getRemainingLives(),
                waveManager.getCurrentWaveNumber(), player.getWeapon().displayName());
    }

    public String getDisplayedTimescaleText() {
        return timeScaleController.getDisplayedTimescaleText();
    }

    /**
     * Gives the current score, used to seed the end screen and the
     * persistent high-score leaderboard once the run finishes.
     */
    public int getScore() {
        return scoreManager.getScore();
    }

    private ScreenState simulateOneFrame(Input input) {
        handlePlayerInput(input);
        player.update();
        handleShoot();
        waveManager.update(enemies, powerUps, gameProps);
        updateEntities();
        return checkWaveStatus();
    }

    private ScreenState handleDebugKeys(Input input) {
        if (input.wasPressed(Keys.R)) {
            return ScreenState.START;
        }
        if (input.wasPressed(Keys.I)) {
            devInvincible = !devInvincible;
            player.setDevInvincible(devInvincible);
        }
        if (input.wasPressed(Keys.G)) {
            timeScaleController.increase();
        }
        if (input.wasPressed(Keys.F)) {
            timeScaleController.decrease();
        }
        if (input.wasPressed(Keys.N)) {
            skipToNextWave();
            if (waveManager.areAllWavesComplete()) {
                return ScreenState.END_WIN;
            }
        }
        return null;
    }

    private void handlePlayerInput(Input input) {
        player.handleHorizontalInput(input);
    }

    /**
     * Lets the player switch weapons with the 1–4 number keys.
     */
    private void handleWeaponSelection(Input input) {
        Keys[] weaponKeys = {Keys.NUM_1, Keys.NUM_2, Keys.NUM_3, Keys.NUM_4};
        for (int i = 0; i < weaponKeys.length; i++) {
            if (input.wasPressed(weaponKeys[i])) {
                Weapon weapon = Weapon.fromSelection(i + 1);
                if (weapon != null) {
                    player.selectWeapon(weapon);
                }
            }
        }
    }

    private void handleShoot() {
        if (!wantToShoot) {
            return;
        }
        if (player.canShoot()) {
            playerProjectiles.addAll(player.shoot(gameProps));
            player.startShootCooldown();
        }
        wantToShoot = false;
    }

    /**
     * Moves every entity one frame, fires any enemy bullets that are due,
     * removes anything that has finished, and resolves collisions. Almost
     * all of the per-list bookkeeping lives in {@link #updateAndPrune};
     * only enemies need a custom loop because of their shooting hook.
     */
    private void updateEntities() {
        updateEnemiesWithShooting();
        steerHomingProjectiles();
        updateAndPrune(playerProjectiles);
        updateAndPrune(enemyProjectiles);
        updateAndPrune(powerUps);
        updateAndPrune(explosions);
        handleCollisions();
    }

    /**
     * Points every homing missile at the closest live enemy before the
     * generic movement step runs.
     */
    private void steerHomingProjectiles() {
        for (PlayerProjectile projectile : playerProjectiles) {
            if (projectile instanceof HomingProjectile homing) {
                EnemyShip target = nearestEnemyTo(homing.getX(), homing.getY());
                if (target != null) {
                    homing.steerToward(target.getX(), target.getY());
                }
            }
        }
    }

    private EnemyShip nearestEnemyTo(double x, double y) {
        EnemyShip nearest = null;
        double bestDistance = Double.MAX_VALUE;
        for (EnemyShip enemy : enemies) {
            double distance = Math.hypot(enemy.getX() - x, enemy.getY() - y);
            if (distance < bestDistance) {
                bestDistance = distance;
                nearest = enemy;
            }
        }
        return nearest;
    }

    /**
     * Generic update-and-prune loop. Polymorphism on
     * {@link GameEntity#update()} and {@link GameEntity#shouldBeRemoved}
     * lets the same body handle projectiles, power-ups, and explosions.
     */
    private <T extends GameEntity> void updateAndPrune(List<T> entities) {
        Iterator<T> iterator = entities.iterator();
        while (iterator.hasNext()) {
            T entity = iterator.next();
            entity.update();
            if (entity.shouldBeRemoved(screenHeight)) {
                iterator.remove();
            }
        }
    }

    /**
     * Same as {@link #updateAndPrune}, but also lets each enemy fire a
     * projectile if it is due to. Kept separate so the generic loop stays
     * generic.
     */
    private void updateEnemiesWithShooting() {
        int waveFrame = waveManager.getCurrentFrame();
        Iterator<EnemyShip> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            EnemyShip enemy = iterator.next();
            enemy.update();
            if (enemy.canShoot(waveFrame)) {
                enemyProjectiles.addAll(enemy.shoot(gameProps));
            }
            if (enemy.shouldBeRemoved(screenHeight)) {
                iterator.remove();
            }
        }
    }

    private void handleCollisions() {
        collisionSystem.handlePlayerProjectileEnemyCollisions(
                playerProjectiles, enemies, explosions, scoreManager);
        collisionSystem.handleProjectileProjectileCollisions(
                playerProjectiles, enemyProjectiles, explosions, scoreManager);
        collisionSystem.handlePlayerEnemyCollisions(
                enemies, player, explosions, scoreManager);
        collisionSystem.handlePlayerEnemyProjectileCollisions(
                enemyProjectiles, player, explosions, scoreManager);
        collisionSystem.handlePlayerPowerUpCollisions(
                powerUps, player, scoreManager);
    }

    /**
     * Decides whether the run should keep going, end in a loss, or end in
     * a win because the player has finished every wave.
     */
    private ScreenState checkWaveStatus() {
        if (!player.isAlive()) {
            return ScreenState.END_LOSE;
        }
        if (waveManager.isCurrentWaveComplete(enemies, enemyProjectiles, powerUps)) {
            scoreManager.addWaveCompletedScore();
            boolean hasMoreWaves = waveManager.moveToNextWave();
            if (!hasMoreWaves) {
                return ScreenState.END_WIN;
            }
        }
        return ScreenState.BATTLE;
    }

    private void skipToNextWave() {
        playerProjectiles.clear();
        enemyProjectiles.clear();
        enemies.clear();
        powerUps.clear();
        scoreManager.addWaveCompletedScore();
        waveManager.moveToNextWave();
    }
}
