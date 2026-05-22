package game.core;

/**
 * Keeps track of the dev-mode speed setting. The current timescale is an
 * integer level: zero is normal speed, positive values speed the game up
 * (1x faster per step), and negative values slow the game down (half,
 * third, quarter, and so on). For slow motion the battle screen simply
 * runs the simulation on a subset of render frames. There is no fixed
 * upper or lower limit on the timescale level.
 */
public class TimeScaleController {
    private static final int INITIAL_TIMESCALE = 0;

    private int timescale;
    private int renderFrameCount;

    public TimeScaleController() {
        reset();
    }

    /**
     * Puts the timescale back to its starting value.
     */
    public final void reset() {
        timescale = INITIAL_TIMESCALE;
        renderFrameCount = 0;
    }

    /**
     * Makes the game one step faster.
     */
    public void increase() {
        timescale++;
        renderFrameCount = 0;
    }

    /**
     * Makes the game one step slower.
     */
    public void decrease() {
        timescale--;
        renderFrameCount = 0;
    }

    /**
     * Gives the speed multiplier as a real number, so 2 means twice as
     * fast and {@code 1.0/3.0} means a third as fast.
     */
    public double getMultiplier() {
        if (timescale >= 0) {
            return timescale + 1.0;
        }
        return 1.0 / (-timescale + 1);
    }

    /**
     * Tells the battle screen how many simulation steps to run for the
     * next drawn frame. In slow motion this is sometimes zero, which is
     * how slow-mo is implemented (just skip some simulation frames).
     */
    public int nextSimulationStepsForRenderFrame() {
        renderFrameCount++;
        if (timescale >= 0) {
            return timescale + 1;
        }
        int divisor = -timescale + 1;
        return renderFrameCount % divisor == 0 ? 1 : 0;
    }

    /**
     * Formats the timescale shown on the pause screen.
     */
    public String getDisplayedTimescaleText() {
        if (timescale >= 0) {
            return Integer.toString(timescale + 1);
        }
        return "1/" + (-timescale + 1);
    }
}
