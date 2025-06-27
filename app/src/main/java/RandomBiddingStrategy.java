import ch.aplu.jcardgame.Hand;
import java.util.Random;

/**
 * Original random bidding strategy
 */
public class RandomBiddingStrategy implements BiddingStrategy {
    private final Random random;
    private final int bidIncrement;
    private final int maxSingleBid;

    public RandomBiddingStrategy(Random random, int bidIncrement, int maxSingleBid) {
        this.random = random;
        this.bidIncrement = bidIncrement;
        this.maxSingleBid = maxSingleBid;
    }

    @Override
    public int calculateBid(Hand hand, int currentBid, boolean isFirstBid) {
        // Return a random bid between 0 and maximum allowed single bid
        int randomBidBase = random.nextInt(3); // 0, 1, or 2
        int bidValue = randomBidBase * bidIncrement;

        // Maximum bid limit
        bidValue = Math.min(bidValue, maxSingleBid);

        System.out.println(bidValue);

        return bidValue;
    }
}