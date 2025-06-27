/**
 * The trump selection game state
 */
public class TrumpSelectionState extends GameState {
    public TrumpSelectionState(Pinochle gameContext) {
        super(gameContext);
    }

    @Override
    public void handle() {
        gameContext.performTrumpSelection();
        if (gameContext.config.isCutthroatMode()) {
            gameContext.setState(new CutthroatState(gameContext));
        } else {
            gameContext.setState(new MeldingState(gameContext));
        }
    }

    @Override
    public String getStateName() {
        return "Trump Selection";
    }
}
