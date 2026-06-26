package game.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoreManagerTest {

    private ScoreManager scoreManager;

    @BeforeEach
    void setUp() {
        Properties props = new Properties();
        props.setProperty("score.destroyedEnemy.regular", "100");
        props.setProperty("score.destroyedEnemy.strafing", "200");
        props.setProperty("score.destroyedEnemy.shooting", "300");
        props.setProperty("score.gotHit", "1000");
        props.setProperty("score.gotPowerup", "500");
        props.setProperty("score.hitProjectile", "50");
        props.setProperty("score.waveCompleted", "1000");
        scoreManager = new ScoreManager(props);
    }

    @Test
    void startsAtZero() {
        assertEquals(0, scoreManager.getScore());
    }

    @Test
    void destroyedEnemyScoreDependsOnType() {
        scoreManager.addDestroyedEnemyScore(EnemyType.REGULAR);
        assertEquals(100, scoreManager.getScore());
        scoreManager.addDestroyedEnemyScore(EnemyType.SHOOTING);
        assertEquals(400, scoreManager.getScore());
    }

    @Test
    void powerUpProjectileAndWaveScoresAccumulate() {
        scoreManager.addPowerUpScore();
        scoreManager.addHitProjectileScore();
        scoreManager.addWaveCompletedScore();
        assertEquals(500 + 50 + 1000, scoreManager.getScore());
    }

    @Test
    void playerHitPenaltyIsClampedAtZero() {
        scoreManager.addHitProjectileScore();
        scoreManager.subtractPlayerHitScore();
        assertEquals(0, scoreManager.getScore());
    }

    @Test
    void resetClearsAccumulatedScore() {
        scoreManager.addWaveCompletedScore();
        scoreManager.reset();
        assertEquals(0, scoreManager.getScore());
    }
}
