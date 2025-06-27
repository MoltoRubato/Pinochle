import ch.aplu.jcardgame.*;
import java.util.*;
import java.util.stream.Collectors;

public class SmartTrickTakingStrategy implements TrickTakingStrategy {
    private final Set<Card> playedCards = new HashSet<>();
    private final Map<String, Integer> suitCardsRemaining = new HashMap<>();

    public SmartTrickTakingStrategy() {
        initializeCardTracking();
    }

    private void initializeCardTracking() {
        for (Suit suit : Suit.values()) {
            suitCardsRemaining.put(suit.getSuitShortHand(), 12); // 12 cards per suit
        }
    }

    @Override
    public Card selectCard(Hand playerHand, List<Card> playingAreaCards, String trumpSuit,
                           Set<Card> playedCards, int playerIndex, int bidWinnerIndex, int currentBid) {
        List<Card> validCards = TrickHelper.getValidCards(
                playerHand.getCardList(), playingAreaCards, trumpSuit);

        return playingAreaCards.isEmpty()
                ? selectLeadingCard(validCards, trumpSuit, playerIndex, bidWinnerIndex)
                : selectFollowingCard(validCards, trumpSuit);
    }

    private Card selectLeadingCard(List<Card> validCards, String trumpSuit,
                                   int playerIndex, int bidWinnerIndex) {
        // Bid winner, lead with high values
        if (playerIndex == bidWinnerIndex) {
            Card highValueCard = getCardByValue(validCards, trumpSuit, true);
            if (highValueCard != null && getCardTotalValue(highValueCard, trumpSuit) >= 10) {
                return highValueCard;
            }
        }

        // play Guaranteed winners (except for trumps)
        Card guaranteedWinner = findGuaranteedWinner(validCards);
        if (guaranteedWinner != null) {
            return guaranteedWinner;
        }

        // Lead with trumps if we have a lot
        Card trumpLead = selectStrategicTrumpLead(validCards, trumpSuit);
        if (trumpLead != null){
            return trumpLead;
        }

        // If the opponent has a void suit, play that suit
        Card voidExploit = findVoidSuitExploit(validCards, trumpSuit);
        if (voidExploit != null) {
            return voidExploit;
        }

        // Default: lowest card, save the big cards for defending
        return getCardByRank(validCards, false);
    }

    private Card selectStrategicTrumpLead(List<Card> validCards, String trumpSuit) {
        List<Card> trumpCards = getCardsBySuit(validCards, trumpSuit);
        if (trumpCards.size() < 2) return null;

        int opponentTrump = suitCardsRemaining.get(trumpSuit) - trumpCards.size();

        if (opponentTrump <= 2) {
            return getCardByRank(trumpCards, true); // Lead with highest trump
        }

        if (trumpCards.size() >= 4 && opponentTrump >= 3) {
            trumpCards.sort((a, b) -> Integer.compare(getRank(b), getRank(a)));
            return trumpCards.get(1); // Lead with 2nd highest trump just in case
        }

        return null;
    }

    private Card findGuaranteedWinner(List<Card> validCards) {
        return validCards.stream()
                .filter(this::isGuaranteedWinner)
                .findFirst()
                .orElse(null);
    }

    private boolean isGuaranteedWinner(Card card) {
        String suit = ((Suit) card.getSuit()).getSuitShortHand();
        int rank = getRank(card);

        // Check if all higher rank cards in this suit have been played
        for (int higherRank = rank + 1; higherRank <= Rank.ACE.getRankCardValue(); higherRank++) {
            int finalHigherRank = higherRank;
            long playedCount = playedCards.stream()
                    .filter(c -> isSuit(c, suit) && getRank(c) == finalHigherRank)
                    .count();

            if (playedCount < 2) return false;
        }
        return true;
    }

    private Card findVoidSuitExploit(List<Card> validCards, String trumpSuit) {
        for (String suit : Arrays.asList("S", "H", "D", "C")) {
            if (suit.equals(trumpSuit)) continue;

            List<Card> ourCards = getCardsBySuit(validCards, suit);
            int opponentMax = suitCardsRemaining.get(suit) - ourCards.size();

            if (opponentMax <= 1 && !ourCards.isEmpty()) {
                return getCardByRank(ourCards, false); // Lead lowest from that suit
            }
        }
        return null;
    }

    private Card selectFollowingCard(List<Card> validCards, String trumpSuit) {
        List<Card> nonTrumpCards = validCards.stream()
                .filter(card -> !isSuit(card, trumpSuit))
                .collect(Collectors.toList());

        return nonTrumpCards.isEmpty()
                ? getCardByRank(validCards, false)
                : getCardByValue(nonTrumpCards, trumpSuit, false);
    }

    private int getCardTotalValue(Card card, String trumpSuit) {
        Rank rank = (Rank) card.getRank();
        Suit suit = (Suit) card.getSuit();
        return (rank == Rank.NINE && suit.getSuitShortHand().equals(trumpSuit))
                ? Rank.NINE_TRUMP : rank.getScoreValue();
    }

    private Card getCardByValue(List<Card> cards, String trumpSuit, boolean highest) {
        Comparator<Card> comparator = Comparator
                .comparingInt(this::getRank)  // Primary: rank value
                .thenComparing(card -> isSuit(card, trumpSuit) ? 1 : 0);  // Secondary: trump suit is stronger

        if (!highest) {
            comparator = comparator.reversed();
        }

        return cards.stream()
                .max(comparator)
                .orElse(null);
    }

    private Card getCardByRank(List<Card> cards, boolean highest) {
        Comparator<Card> comparator = Comparator.comparing(this::getRank);
        return cards.stream()
                .max(highest ? comparator : comparator.reversed())
                .orElse(null);
    }

    private List<Card> getCardsBySuit(List<Card> cards, String suit) {
        return cards.stream()
                .filter(card -> isSuit(card, suit))
                .collect(Collectors.toList());
    }

    private boolean isSuit(Card card, String suit) {
        return ((Suit) card.getSuit()).getSuitShortHand().equals(suit);
    }

    private int getRank(Card card) {
        return ((Rank) card.getRank()).getRankCardValue();
    }

    @Override
    public void recordPlayedCard(Card card) {
        playedCards.add(card);
        String suitKey = ((Suit) card.getSuit()).getSuitShortHand();
        suitCardsRemaining.put(suitKey, suitCardsRemaining.get(suitKey) - 1);
    }

    @Override
    public void reset() {
        playedCards.clear();
        initializeCardTracking();
    }
}