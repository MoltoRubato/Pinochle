import ch.aplu.jcardgame.Hand;

/**
 * Interface for bidding strategies
 */
public interface BiddingStrategy {
    /**
     * returns a bid value
     */
    int calculateBid(Hand hand, int currentBid, boolean isFirstBid);
}