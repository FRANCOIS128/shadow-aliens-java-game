package game.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HighScoreManagerTest {

    @TempDir
    Path tempDir;

    private HighScoreManager managerAt(Path file, int maxEntries) {
        return new HighScoreManager(file.toString(), maxEntries);
    }

    @Test
    void startsEmptyWhenNoFileExists() {
        HighScoreManager manager = managerAt(tempDir.resolve("scores.txt"), 5);
        assertEquals(0, manager.getHighScore());
        assertTrue(manager.getHighScores().isEmpty());
    }

    @Test
    void recordsAndSortsScoresDescending() {
        HighScoreManager manager = managerAt(tempDir.resolve("scores.txt"), 5);
        manager.submitScore(300);
        manager.submitScore(900);
        manager.submitScore(600);
        assertEquals(List.of(900, 600, 300), manager.getHighScores());
        assertEquals(900, manager.getHighScore());
    }

    @Test
    void onlyKeepsTopEntries() {
        HighScoreManager manager = managerAt(tempDir.resolve("scores.txt"), 3);
        manager.submitScore(100);
        manager.submitScore(200);
        manager.submitScore(300);
        manager.submitScore(400);
        assertEquals(List.of(400, 300, 200), manager.getHighScores());
    }

    @Test
    void reportsNewHighScoreOnlyWhenBeaten() {
        HighScoreManager manager = managerAt(tempDir.resolve("scores.txt"), 5);
        assertTrue(manager.submitScore(500));
        assertFalse(manager.submitScore(400));
        assertTrue(manager.submitScore(800));
    }

    @Test
    void persistsAcrossInstances() {
        Path file = tempDir.resolve("scores.txt");
        managerAt(file, 5).submitScore(1234);
        HighScoreManager reloaded = managerAt(file, 5);
        assertEquals(1234, reloaded.getHighScore());
    }

    @Test
    void ignoresCorruptLinesWhenLoading() throws IOException {
        Path file = tempDir.resolve("scores.txt");
        Files.write(file, List.of("700", "not-a-number", "", "300"));
        HighScoreManager manager = managerAt(file, 5);
        assertEquals(List.of(700, 300), manager.getHighScores());
    }

    @Test
    void returnedListIsAnUnmodifiableSnapshot() {
        HighScoreManager manager = managerAt(tempDir.resolve("scores.txt"), 5);
        manager.submitScore(100);
        List<Integer> snapshot = manager.getHighScores();
        org.junit.jupiter.api.Assertions.assertThrows(
                UnsupportedOperationException.class, () -> snapshot.add(999));
    }
}
