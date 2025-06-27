/**
 * The bidding game state
 */
public class BiddingState extends GameState {
    public BiddingState(Pinochle gameContext) {
        super(gameContext);
    }

    @Override
    public void handle() {
        gameContext.getBiddingManager().performBidding(gameContext.getHands());
        gameContext.setState(new TrumpSelectionState(gameContext));
    }

    @Override
    public String getStateName() {
        return "Bidding";
    }
}