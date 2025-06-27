/**
 * The melding game state
 */
public class MeldingState extends GameState {
    public MeldingState(Pinochle gameContext) {
        super(gameContext);
    }

    @Override
    public void handle() {
        gameContext.performMelding();
        gameContext.setState(new TrickTakingState(gameContext));
    }

    @Override
    public String getStateName() {
        return "Melding";
    }
}