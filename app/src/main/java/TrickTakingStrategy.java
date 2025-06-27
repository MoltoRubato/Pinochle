import ch.aplu.jcardgame.*;
import java.util.*;


public interface TrickTakingStrategy {
    Card selectCard(Hand playerHand, List<Card> playingAreaCards, String trumpSuit,
                    Set<Card> playedCards, int playerIndex, int bidWinnerIndex, int currentBid);
    void recordPlayedCard(Card card);
    void reset();
}

