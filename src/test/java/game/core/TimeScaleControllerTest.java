package game.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeScaleControllerTest {

    private TimeScaleController controller;

    @BeforeEach
    void setUp() {
        controller = new TimeScaleController();
    }

    @Test
    void startsAtNormalSpeed() {
        assertEquals(1.0, controller.getMultiplier());
        assertEquals("1", controller.getDisplayedTimescaleText());
        assertEquals(1, controller.nextSimulationStepsForRenderFrame());
    }

    @Test
    void increasingSpeedsUpSimulation() {
        controller.increase();
        controller.increase();
        assertEquals(3.0, controller.getMultiplier());
        assertEquals("3", controller.getDisplayedTimescaleText());
        assertEquals(3, controller.nextSimulationStepsForRenderFrame());
    }

    @Test
    void decreasingHalvesSpeed() {
        controller.decrease();
        assertEquals(0.5, controller.getMultiplier());
        assertEquals("1/2", controller.getDisplayedTimescaleText());
    }

    @Test
    void slowMotionRunsSimulationOnSubsetOfFrames() {
        controller.decrease();
        assertEquals(0, controller.nextSimulationStepsForRenderFrame());
        assertEquals(1, controller.nextSimulationStepsForRenderFrame());
        assertEquals(0, controller.nextSimulationStepsForRenderFrame());
        assertEquals(1, controller.nextSimulationStepsForRenderFrame());
    }

    @Test
    void resetReturnsToNormalSpeed() {
        controller.increase();
        controller.increase();
        controller.reset();
        assertEquals(1.0, controller.getMultiplier());
        assertEquals("1", controller.getDisplayedTimescaleText());
    }
}
