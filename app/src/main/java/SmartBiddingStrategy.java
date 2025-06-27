import ch.aplu.jcardgame.*;
import java.util.*;

/**
 * Smart bidding strategy for Task 2
 */
public class SmartBiddingStrategy implements BiddingStrategy {
    private final int bidIncrement;
    private final int maxSingleBid;
    private final Random random;

    public SmartBiddingStrategy(Random random, int bidIncrement, int maxSingleBid) {
        this.random = random;
        this.bidIncrement = bidIncrement;
        this.maxSingleBid = maxSingleBid;
    }

    @Override
    public int calculateBid(Hand hand, int currentBid, boolean isFirstBid) {
        String majoritySuit = determineLikelyTrumpSuit(hand);

        if (isFirstBid) {
            // First bid is based on melding score
            return MeldScores.calculateMeldingScore(hand.getCardList(), majoritySuit);
        } else {
            int bidIncrease;
            if (countCardsInSuit(hand, majoritySuit) >= 6) {
                bidIncrease = 20; // if 6+ cards in same suit

            } else {
                bidIncrease = 10; // Standard increase otherwise
            }

            // Calculate maximum bid we're willing to make
            int maxAllowedBid = Math.max(
                    calculateSuitValue(hand, majoritySuit),
                    calculateBestHighCardSuitValue(hand)
            );

            // Only bid if new total would be less than the maximum allowed
            int potentialBid = currentBid + bidIncrease;
            if (potentialBid <= maxAllowedBid) {
                return bidIncrease;
            } else {
                return 0; // Pass
            }
        }
    }

    /**
     * Determine majority suit
     */
    String determineLikelyTrumpSuit(Hand hand) {
        Map<String, Integer> suitCounts = new HashMap<>();
        for (Suit suit : Suit.values()) {
            String shorthand = suit.getSuitShortHand();
            suitCounts.put(shorthand, countCardsInSuit(hand, shorthand));
        }

        // Find suit with most cards
        String majoritySuit = null;
        int maxCount = -1;
        List<String> tiedSuits = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : suitCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                majoritySuit = entry.getKey();
                tiedSuits.clear();
                tiedSuits.add(majoritySuit);
            } else if (entry.getValue() == maxCount) {
                tiedSuits.add(entry.getKey());
            }
        }

        // If there's a tie, randomly select one
        if (tiedSuits.size() > 1) {
            return tiedSuits.get(random.nextInt(tiedSuits.size()));
        }

        return majoritySuit;
    }

    /**
     * Count how many cards there are in a suit
     */
    private int countCardsInSuit(Hand hand, String suitShorthand) {
        int count = 0;
        for (Card card : hand.getCardList()) {
            Suit cardSuit = (Suit) card.getSuit();
            if (cardSuit.getSuitShortHand().equals(suitShorthand)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Calculate the total value of cards in a suit
     */
    private int calculateSuitValue(Hand hand, String suitShorthand) {
        int value = 0;
        for (Card card : hand.getCardList()) {
            Suit cardSuit = (Suit) card.getSuit();
            if (cardSuit.getSuitShortHand().equals(suitShorthand)) {
                Rank cardRank = (Rank) card.getRank();
                value += cardRank.getScoreValue();
            }
        }
        return value + MeldScores.calculateMeldingScore(hand.getCardList(), suitShorthand);
    }

    /**
     * Find the suit with the most (Aces, 10s, Kings) cards and calculate the total value
     */
    private int calculateBestHighCardSuitValue(Hand hand) {
        Map<String, Integer> suitHighCardCounts = new HashMap<>();
        Map<String, Integer> suitValues = new HashMap<>();

        for (Suit suit : Suit.values()) {
            String shorthand = suit.getSuitShortHand();
            suitHighCardCounts.put(shorthand, 0);
            suitValues.put(shorthand, 0);
        }

        // Count high cards and add values
        for (Card card : hand.getCardList()) {
            Suit cardSuit = (Suit) card.getSuit();
            Rank cardRank = (Rank) card.getRank();
            String shorthand = cardSuit.getSuitShortHand();

            // Update suit value
            suitValues.put(shorthand, suitValues.get(shorthand) + cardRank.getScoreValue());

            // Count as high card if Ace, 10, or King
            if (cardRank == Rank.ACE || cardRank == Rank.TEN || cardRank == Rank.KING) {
                suitHighCardCounts.put(shorthand, suitHighCardCounts.get(shorthand) + 1);
            }
        }

        // Find suit with most high cards
        String bestSuit = null;
        int maxHighCards = -1;

        for (Map.Entry<String, Integer> entry : suitHighCardCounts.entrySet()) {
            if (entry.getValue() > maxHighCards) {
                maxHighCards = entry.getValue();
                bestSuit = entry.getKey();
            }
        }

        // Card value + meld Value of that "best suit"
        return bestSuit != null ? suitValues.get(bestSuit) + MeldScores.calculateMeldingScore(hand.getCardList(), bestSuit) : 0;
    }
}