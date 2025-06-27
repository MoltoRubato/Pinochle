import ch.aplu.jcardgame.*;
import java.util.List;

public class TrickWinnerCalculator {

    public static int checkWinner(List<Card> playingAreaCards, String trumpSuit,
                                  int currentPlayerIndex) {
        assert (playingAreaCards.size() == 2);
        int previousPlayerIndex = Math.abs(currentPlayerIndex - 1) % 2;
        Card firstCard = playingAreaCards.get(0);
        Card secondCard = playingAreaCards.get(1);

        Suit firstSuit = (Suit) firstCard.getSuit();
        Suit secondSuit = (Suit) secondCard.getSuit();
        Rank firstRank = (Rank) firstCard.getRank();
        Rank secondRank = (Rank) secondCard.getRank();

        if (firstSuit.getSuitShortHand().equals(secondSuit.getSuitShortHand())) {
            return secondRank.getRankCardValue() > firstRank.getRankCardValue()
                    ? currentPlayerIndex : previousPlayerIndex;
        }

        boolean firstIsTrump = firstSuit.getSuitShortHand().equals(trumpSuit);
        boolean secondIsTrump = secondSuit.getSuitShortHand().equals(trumpSuit);

        if (firstIsTrump && !secondIsTrump) {
            return previousPlayerIndex;
        }
        if (!firstIsTrump && secondIsTrump) {
            return currentPlayerIndex;
        }

        return previousPlayerIndex;
    }
}