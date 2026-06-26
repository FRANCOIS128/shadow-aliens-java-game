package game.ui;

import game.core.EndState;
import game.data.GameDataUtils;

import java.util.List;
import java.util.Properties;

/**
 * Draws the win or lose message, the player's final score, an optional
 * "new high score" banner, the persistent leaderboard, and the
 * press-space-to-restart line.
 */
public class EndScreenRenderer extends OverlayRenderer {
    private final TitleSpec winTitle;
    private final TitleSpec loseTitle;
    private final InstructionsListSpec instructions;
    private final String scoreText;
    private final double scorePosY;
    private final String newHighScoreText;
    private final double newHighScorePosY;
    private final String highScoresTitle;
    private final double highScoresPosY;
    private final double highScoresRowGap;

    public EndScreenRenderer(Properties gameProps) {
        super(gameProps);
        this.winTitle = TitleSpec.from(gameProps, "end.win");
        this.loseTitle = TitleSpec.from(gameProps, "end.lose");
        this.instructions = InstructionsListSpec.from(gameProps, "end.instructionsList");
        this.scoreText = gameProps.getProperty("end.score.text", "YOUR SCORE");
        this.scorePosY = GameDataUtils.parseDouble(gameProps, "end.score.posY");
        this.newHighScoreText = gameProps.getProperty("end.newHighScore.text", "NEW HIGH SCORE!");
        this.newHighScorePosY = GameDataUtils.parseDouble(gameProps, "end.newHighScore.posY");
        this.highScoresTitle = gameProps.getProperty("end.highScores.title", "TOP SCORES");
        this.highScoresPosY = GameDataUtils.parseDouble(gameProps, "end.highScores.posY");
        this.highScoresRowGap = GameDataUtils.parseDouble(gameProps, "end.highScores.rowGap");
    }

    /**
     * Draws the win/lose title, the final score, an optional new-high-score
     * banner, the leaderboard, and the restart instruction.
     */
    public void render(EndState endState,
                       int score,
                       boolean isNewHighScore,
                       List<Integer> highScores) {
        drawTitle(endState == EndState.WIN ? winTitle : loseTitle);
        drawCentred(font(), scoreText + " " + score, scorePosY);
        if (isNewHighScore) {
            drawCentred(font(), newHighScoreText, newHighScorePosY);
        }
        drawCentred(font(), highScoresTitle, highScoresPosY);
        for (int i = 0; i < highScores.size(); i++) {
            String line = (i + 1) + ".  " + highScores.get(i);
            drawCentred(font(), line, highScoresPosY + (i + 1) * highScoresRowGap);
        }
        drawInstructions(instructions);
    }
}
