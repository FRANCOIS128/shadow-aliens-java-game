package game.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Persists the player's best scores to a small text file (one integer per
 * line) and keeps the in-memory leaderboard in descending order, capped at
 * a maximum number of entries.
 *
 * <p>All file access is best-effort: a missing, unreadable, or partially
 * corrupt file never crashes the game. Unparsable lines are skipped and any
 * I/O failure simply leaves the leaderboard untouched, which keeps the
 * gameplay path free of checked-exception handling.
 */
public class HighScoreManager {
    private static final String DEFAULT_FILE = "highscores.txt";
    private static final int DEFAULT_MAX_ENTRIES = 5;

    private final Path file;
    private final int maxEntries;
    private final List<Integer> scores;

    public HighScoreManager(String filePath, int maxEntries) {
        this.file = Path.of(filePath);
        this.maxEntries = Math.max(1, maxEntries);
        this.scores = new ArrayList<>();
        load();
    }

    /**
     * Builds a manager from the data file, falling back to sensible
     * defaults when the keys are absent.
     */
    public static HighScoreManager from(Properties gameProps) {
        String filePath = gameProps.getProperty("highscore.file", DEFAULT_FILE);
        int max = gameProps.containsKey("highscore.maxEntries")
                ? GameDataUtils.parseInt(gameProps, "highscore.maxEntries")
                : DEFAULT_MAX_ENTRIES;
        return new HighScoreManager(filePath, max);
    }

    /**
     * Reloads the leaderboard from disk, replacing the in-memory list.
     */
    public final void load() {
        scores.clear();
        if (!Files.exists(file)) {
            return;
        }
        try {
            for (String line : Files.readAllLines(file)) {
                addParsedScore(line);
            }
        } catch (IOException e) {
            scores.clear();
        }
        trimToTop();
    }

    /**
     * Adds a single line's score, skipping blank or corrupt lines rather
     * than failing the whole load.
     */
    private void addParsedScore(String line) {
        String trimmed = line.trim();
        if (trimmed.isEmpty()) {
            return;
        }
        try {
            scores.add(Integer.parseInt(trimmed));
        } catch (NumberFormatException ignored) {
            // Corrupt entry: ignore it and keep the rest of the leaderboard.
        }
    }

    /**
     * Checks whether the given score would become the new top score,
     * without recording it.
     */
    public boolean isNewHighScore(int score) {
        return score > getHighScore();
    }

    /**
     * Gives the current best score, or {@code 0} when no scores exist.
     */
    public int getHighScore() {
        return scores.isEmpty() ? 0 : scores.get(0);
    }

    /**
     * Gives an unmodifiable, descending snapshot of the leaderboard.
     */
    public List<Integer> getHighScores() {
        return Collections.unmodifiableList(new ArrayList<>(scores));
    }

    /**
     * Records a finished run's score, keeps only the top entries, and
     * persists the leaderboard.
     *
     * @return {@code true} if this score is the new best
     */
    public boolean submitScore(int score) {
        boolean isNew = isNewHighScore(score);
        scores.add(score);
        trimToTop();
        save();
        return isNew;
    }

    private void trimToTop() {
        scores.sort(Collections.reverseOrder());
        if (scores.size() > maxEntries) {
            scores.subList(maxEntries, scores.size()).clear();
        }
    }

    private void save() {
        try {
            List<String> lines = new ArrayList<>(scores.size());
            for (int score : scores) {
                lines.add(Integer.toString(score));
            }
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(file, lines);
        } catch (IOException e) {
            // Best-effort persistence: a failed save must not break the game.
        }
    }
}
