package game.ui;

import game.core.EndState;

import java.util.Properties;

/**
 * Draws the win or lose message plus the press-space-to-restart line.
 */
public class EndScreenRenderer extends OverlayRenderer {
    private final TitleSpec winTitle;
    private final TitleSpec loseTitle;
    private final InstructionsListSpec instructions;

    public EndScreenRenderer(Properties gameProps) {
        super(gameProps);
        this.winTitle = TitleSpec.from(gameProps, "end.win");
        this.loseTitle = TitleSpec.from(gameProps, "end.lose");
        this.instructions = InstructionsListSpec.from(gameProps, "end.instructionsList");
    }

    /**
     * Draws either the win or lose title, then the restart instruction.
     * The {@code score} argument matches the UML signature; the spec does
     * not require a score on the end screen so it is intentionally not
     * shown.
     */
    public void render(EndState endState, int score) {
        drawTitle(endState == EndState.WIN ? winTitle : loseTitle);
        drawInstructions(instructions);
    }
}
