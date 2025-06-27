import java.util.Random;

public class TrickTakingStrategyFactory {
    public static final String SMART_TRICK_TAKING = "smart";
    public static final String RANDOM_TRICK_TAKING = "random";

    public static TrickTakingStrategy createStrategy(String strategyType, Random random) {
        switch (strategyType) {
            case SMART_TRICK_TAKING:
                return new SmartTrickTakingStrategy();
            case RANDOM_TRICK_TAKING:
            default:
                return new RandomTrickTakingStrategy(random);
        }
    }
}