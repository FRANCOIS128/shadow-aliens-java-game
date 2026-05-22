package game.data;

import game.core.PowerUpType;

/**
 * Data for one power-up entry in the wave section of the properties file.
 * Fields and constructor match the UML. The spec actually defines
 * movement speed per type (in {@code powerup.<type>.movementSpeed}), so
 * the per-instance speed is loaded from that property and cached here for
 * UML fidelity.
 */
public class PowerUpSpawnInfo {
    private final int waveNumber;
    private final int arrivalTime;
    private final double posX;
    private final double movementSpeed;
    private final PowerUpType type;

    public PowerUpSpawnInfo(int waveNumber,
                            int arrivalTime,
                            double posX,
                            double movementSpeed,
                            PowerUpType type) {
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

    public PowerUpType getType() {
        return type;
    }
}
