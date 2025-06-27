import java.util.Random;

/**
 * Factory for bidding strategies
 */
public class BiddingStrategyFactory {

    public static final String SMART_BIDDING = "smart";
    public static final String RANDOM_BIDDING = "random";

    public static BiddingStrategy createStrategy(String strategyName, int bidIncrement, int maxSingleBid) {

        Random random = new Random();

        if (strategyName.equalsIgnoreCase("smart")) {
            return new SmartBiddingStrategy(random, bidIncrement, maxSingleBid);
        }

        return new RandomBiddingStrategy(random, bidIncrement, maxSingleBid);
    }
}