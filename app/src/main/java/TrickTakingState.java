/**
 * The trick taking game state
 */
public class TrickTakingState extends GameState {
    public TrickTakingState(Pinochle gameContext) {
        super(gameContext);
    }

    @Override
    public void handle() {
        gameContext.performTrickTaking();
        gameContext.setState(new GameOverState(gameContext));
    }

    @Override
    public String getStateName() {
        return "Trick Taking";
    }
}