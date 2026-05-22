package game.data;

import game.core.EnemyType;
import game.core.PowerUpType;
import game.core.Wave;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

/**
 * Reads the wave data from the data file and turns it into the value
 * objects the game can work with. The loader is the only place that
 * understands the {@code wave.#.enemy.#.*} key shape, which keeps the
 * rest of the code decoupled from the data format.
 */
public final class GameDataLoader {
    private GameDataLoader() {
    }

    /**
     * Loads every wave found in the data file, in numeric order.
     */
    public static List<Wave> loadWaves(Properties gameProps) {
        List<Integer> waveNumbers = discoverWaveNumbers(gameProps);
        List<Wave> waves = new ArrayList<>(waveNumbers.size());
        for (int waveNumber : waveNumbers) {
            List<EnemySpawnInfo> enemyInfos = loadEnemySpawnInfos(gameProps, waveNumber);
            List<PowerUpSpawnInfo> powerUpInfos = loadPowerUpSpawnInfos(gameProps, waveNumber);
            waves.add(new Wave(enemyInfos, powerUpInfos));
        }
        return waves;
    }

    /**
     * Loads the enemy entries for one wave, sorted by arrival time so the
     * wave object only needs a single index pointer.
     */
    public static List<EnemySpawnInfo> loadEnemySpawnInfos(Properties gameProps, int waveNumber) {
        List<EnemySpawnInfo> infos = new ArrayList<>();
        int enemyIndex = 0;
        while (true) {
            String prefix = "wave." + waveNumber + ".enemy." + enemyIndex + ".";
            if (!gameProps.containsKey(prefix + "arrivalTime")) {
                break;
            }
            int arrivalTime = GameDataUtils.parseInt(gameProps, prefix + "arrivalTime");
            double posX = GameDataUtils.parseDouble(gameProps, prefix + "posX");
            double movementSpeed = GameDataUtils.parseDouble(gameProps, prefix + "movementSpeed");
            EnemyType type = EnemyType.fromKey(gameProps.getProperty(prefix + "type"));
            infos.add(new EnemySpawnInfo(waveNumber, arrivalTime, posX, movementSpeed, type));
            enemyIndex++;
        }
        infos.sort(Comparator.comparingInt(EnemySpawnInfo::getArrivalTime));
        return infos;
    }

    /**
     * Loads the power-up entries for one wave. Per-instance movement
     * speed is read from the per-type {@code powerup.<type>.movementSpeed}
     * property, which is what the spec defines.
     */
    public static List<PowerUpSpawnInfo> loadPowerUpSpawnInfos(Properties gameProps, int waveNumber) {
        List<PowerUpSpawnInfo> infos = new ArrayList<>();
        int powerUpIndex = 0;
        while (true) {
            String prefix = "wave." + waveNumber + ".powerup." + powerUpIndex + ".";
            if (!gameProps.containsKey(prefix + "arrivalTime")) {
                break;
            }
            int arrivalTime = GameDataUtils.parseInt(gameProps, prefix + "arrivalTime");
            double posX = GameDataUtils.parseDouble(gameProps, prefix + "posX");
            PowerUpType type = PowerUpType.fromKey(gameProps.getProperty(prefix + "type"));
            double movementSpeed = GameDataUtils.parseDouble(gameProps,
                    "powerup." + type.getKey() + ".movementSpeed");
            infos.add(new PowerUpSpawnInfo(waveNumber, arrivalTime, posX, movementSpeed, type));
            powerUpIndex++;
        }
        infos.sort(Comparator.comparingInt(PowerUpSpawnInfo::getArrivalTime));
        return infos;
    }

    private static List<Integer> discoverWaveNumbers(Properties gameProps) {
        TreeSet<Integer> waveNumbers = new TreeSet<>();
        for (Object key : gameProps.keySet()) {
            String keyName = key.toString();
            if (!keyName.startsWith("wave.")) {
                continue;
            }
            String[] parts = keyName.split("\\.");
            if (parts.length < 2) {
                continue;
            }
            try {
                waveNumbers.add(Integer.parseInt(parts[1]));
            } catch (NumberFormatException ignored) {
                // Keys like "wave.text" are labels, not wave numbers.
            }
        }
        return new ArrayList<>(waveNumbers);
    }
}
