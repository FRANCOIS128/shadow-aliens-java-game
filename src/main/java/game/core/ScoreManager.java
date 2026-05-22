package game.core;

import game.data.GameDataUtils;

import java.util.Properties;

/**
 * Owns the player's score. The collision system tells this class what
 * happened (enemy destroyed, bullet deflected, player hit, wave cleared)
 * and the matching score change is applied here. The score is clamped at
 * zero because the spec disallows negative scores.
 */
public class ScoreManager {
    private final Properties gameProps;
    private final int scoreHit;
    private final int scoreGotPowerUp;
    private final int scoreHitProjectile;
    private final int scoreWaveCompleted;
    private int score;

    public ScoreManager(Properties gameProps) {
        this.gameProps = gameProps;
        this.scoreHit = readIntOrZero("score.gotHit");
        this.scoreGotPowerUp = readIntOrZero("score.gotPowerup");
        this.scoreHitProjectile = readIntOrZero("score.hitProjectile");
        this.scoreWaveCompleted = readIntOrZero("score.waveCompleted");
        reset();
    }

    public final void reset() {
        score = 0;
    }

    public int getScore() {
        return score;
    }

    /**
     * Adds the score for destroying this kind of enemy.
     */
    public void addDestroyedEnemyScore(EnemyType type) {
        score += readIntOrZero("score.destroyedEnemy." + type.getKey());
    }

    public void addHitProjectileScore() {
        score += scoreHitProjectile;
    }

    public void addPowerUpScore() {
        score += scoreGotPowerUp;
    }

    public void addWaveCompletedScore() {
        score += scoreWaveCompleted;
    }

    /**
     * Applies the hit penalty, clamped so the score never goes below zero.
     */
    public void subtractPlayerHitScore() {
        score = Math.max(0, score - scoreHit);
    }

    private int readIntOrZero(String key) {
        return gameProps.getProperty(key) == null ? 0 : GameDataUtils.parseInt(gameProps, key);
    }
}
