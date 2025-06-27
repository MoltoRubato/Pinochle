/**
 * The end game state
 */
public class GameOverState extends GameState {
    public GameOverState(Pinochle gameContext) {
        super(gameContext);
    }

    @Override
    public void handle() {
        gameContext.performGameOver();
        // Game ends
    }

    @Override
    public String getStateName() {
        return "Game Over";
    }
}