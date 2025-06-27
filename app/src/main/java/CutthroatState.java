/**
 * The cutthroat game state
 */
public class CutthroatState extends GameState {

    public CutthroatState(Pinochle gameContext) {
        super(gameContext);
    }

    @Override
    public void handle() {
        gameContext.performCutThroatMode();
        // Transition to melding state after cutthroat mode
        gameContext.setState(new MeldingState(gameContext));
    }

    @Override
    public String getStateName() {
        return "Cutthroat Mode";
    }
}