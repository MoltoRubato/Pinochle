import ch.aplu.jcardgame.*;
import java.util.*;

public class RandomTrickTakingStrategy implements TrickTakingStrategy {
    private final Random random;

    public RandomTrickTakingStrategy(Random random) {
        this.random = random;
    }

    @Override
    public Card selectCard(Hand playerHand, List<Card> playingAreaCards, String trumpSuit,
                           Set<Card> playedCards, int playerIndex, int bidWinnerIndex, int currentBid) {
        List<Card> validCards = TrickHelper.getValidCards(
                playerHand.getCardList(), playingAreaCards, trumpSuit);
        return validCards.get(random.nextInt(validCards.size()));
    }

    @Override
    public void recordPlayedCard(Card card) {
        // Random strategy doesn't track cards
    }

    @Override
    public void reset() {
        // Don't need to reset
    }
}