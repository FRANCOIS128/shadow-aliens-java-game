package game.data;

import game.core.EnemyType;

/**
 * Data for one enemy entry in the wave section of the properties file.
 * Fields mirror the UML so the wave loader has somewhere clear to drop the
 * parsed values for each enemy.
 */
public class EnemySpawnInfo {
    private final int waveNumber;
    private final int arrivalTime;
    private final double posX;
    private final double movementSpeed;
    private final EnemyType type;

    public EnemySpawnInfo(int waveNumber,
                          int arrivalTime,
                          double posX,
                          double movementSpeed,
                          EnemyType type) {
        this.waveNumber = waveNumber;
        this.arrivalTime = arrivalTime;
        this.posX = posX;
        this.movementSpeed = movementSpeed;
        this.type = type;
    }

    public int getWaveNumber() {
        return waveNumber;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public double getPosX() {
        return posX;
    }

    public double getMovementSpeed() {
        return movementSpeed;
    }

    public EnemyType getType() {
        return type;
    }
}
