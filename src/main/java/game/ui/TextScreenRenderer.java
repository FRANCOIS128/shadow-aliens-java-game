package game.ui;

import java.util.Properties;

/**
 * Draws the title and instructions on the start screen.
 */
public class TextScreenRenderer extends OverlayRenderer {
    private final TitleSpec title;
    private final InstructionsListSpec instructions;

    public TextScreenRenderer(Properties gameProps) {
        super(gameProps);
        this.title = TitleSpec.from(gameProps, "start.title");
        this.instructions = InstructionsListSpec.from(gameProps, "start.instructionsList");
    }

    /**
     * Draws the start title and the press-space-to-start instructions.
     */
    public void renderStartScreen() {
        drawTitle(title);
        drawInstructions(instructions);
    }
}
