package game.data;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameDataUtilsTest {

    private Properties propsWith(String key, String value) {
        Properties props = new Properties();
        props.setProperty(key, value);
        return props;
    }

    @Test
    void parseIntTrimsSurroundingWhitespace() {
        assertEquals(42, GameDataUtils.parseInt(propsWith("k", "  42  "), "k"));
    }

    @Test
    void parseDoubleTrimsSurroundingWhitespace() {
        assertEquals(3.5, GameDataUtils.parseDouble(propsWith("k", " 3.5 "), "k"));
    }

    @Test
    void parsePairReadsTwoTrimmedNumbers() {
        assertArrayEquals(new double[] {10.0, 20.0}, GameDataUtils.parsePair(" 10 , 20 "));
    }

    @Test
    void parseTripleReadsThreeTrimmedNumbers() {
        assertArrayEquals(new double[] {0.0, 0.5, 1.0}, GameDataUtils.parseTriple("0, 0.5 ,1"));
    }

    @Test
    void parseIntRejectsNonNumericValue() {
        assertThrows(NumberFormatException.class,
                () -> GameDataUtils.parseInt(propsWith("k", "abc"), "k"));
    }
}
