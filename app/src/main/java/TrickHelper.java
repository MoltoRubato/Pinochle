import ch.aplu.jcardgame.*;
import java.util.*;
import java.util.stream.Collectors;

public class TrickHelper {

    // VALIDATION UTILITIES =======
    public static boolean checkValidTrick(Card playingCard, List<Card> playerCards,
                                          List<Card> existingCards, String trumpSuit) {
        if (existingCards.isEmpty()) {
            return true;
        }

        Card existingCard = existingCards.get(0);
        Suit playingSuit = (Suit) playingCard.getSuit();
        Suit existingSuit = (Suit) existingCard.getSuit();
        Rank playingRank = (Rank) playingCard.getRank();
        Rank existingRank = (Rank) existingCard.getRank();

        // Same Suit, Higher Rank, then valid
        if (isSameSuit(playingCard, existingCard) &&
                playingRank.getRankCardValue() > existingRank.getRankCardValue()) {
            return true;
        }

        // If the chosen is not the same suit, higher rank and there is one, then not valid
        Card higherCard = getHigherCardFromList(existingCard, playerCards);
        if (higherCard != null) {
            return false;
        }

        boolean isExistingTrump = existingSuit.getSuitShortHand().equals(trumpSuit);
        boolean isPlayingTrump = playingSuit.getSuitShortHand().equals(trumpSuit);
        // If the current is trump, then there is already no trump card with higher rank.
        // Otherwise, the above if should return false.
        if (isExistingTrump) {
            return true;
        }

        // If the current is not trump card, then playing trump card is valid
        if (isPlayingTrump) {
            return true;
        }

        // If the current is not trump card, and we have a trump card,
        // but not having a same suit, higher rank card, then we have to play trump card
        Card trumpCard = getTrumpCard(playerCards, trumpSuit);
        if (trumpCard != null) {
            return false;
        }

        // If we dont have a trump card, any card is valid
        return true;
    }

    public static List<Card> getValidCards(List<Card> playerCards, List<Card> playingAreaCards, String trumpSuit) {
        if (playingAreaCards.isEmpty()) {
            return new ArrayList<>(playerCards);
        }

        Card leadCard = playingAreaCards.get(0);
        String leadSuit = ((Suit) leadCard.getSuit()).getSuitShortHand();
        int leadRank = ((Rank) leadCard.getRank()).getRankCardValue();

        // Must play higher same suit if possible
        List<Card> higherSameSuit = playerCards.stream()
                .filter(card -> {
                    String cardSuit = ((Suit) card.getSuit()).getSuitShortHand();
                    int cardRank = ((Rank) card.getRank()).getRankCardValue();
                    return cardSuit.equals(leadSuit) && cardRank > leadRank;
                })
                .collect(Collectors.toList());

        if (!higherSameSuit.isEmpty()) return higherSameSuit;

        // Must follow suit if possible
        List<Card> sameSuit = playerCards.stream()
                .filter(card -> ((Suit) card.getSuit()).getSuitShortHand().equals(leadSuit))
                .collect(Collectors.toList());

        if (!sameSuit.isEmpty()) return sameSuit;

        // Must trump if possible (and lead isn't trump)
        if (!leadSuit.equals(trumpSuit)) {
            List<Card> trumpCards = playerCards.stream()
                    .filter(card -> ((Suit) card.getSuit()).getSuitShortHand().equals(trumpSuit))
                    .collect(Collectors.toList());

            if (!trumpCards.isEmpty()) return trumpCards;
        }

        return new ArrayList<>(playerCards);
    }


    // PARSING UTILITIES =======
    public static Rank getRankFromString(String cardName) {
        if (cardName == null || cardName.length() < 2) {
            return Rank.ACE; // default fallback
        }

        String rankString = cardName.substring(0, cardName.length() - 1);
        try {
            Integer rankValue = Integer.parseInt(rankString);

            for (Rank rank : Rank.values()) {
                if (rank.getShortHandValue() == rankValue) {
                    return rank;
                }
            }
        } catch (NumberFormatException e) {
            // Handle non-numeric ranks if needed
        }

        return Rank.ACE; // default fallback
    }

    public static Suit getSuitFromString(String cardName) {
        if (cardName == null || cardName.isEmpty()) {
            return Suit.CLUBS; // default fallback
        }

        String suitString = cardName.substring(cardName.length() - 1);

        for (Suit suit : Suit.values()) {
            if (suit.getSuitShortHand().equals(suitString)) {
                return suit;
            }
        }

        return Suit.CLUBS; // default fallback
    }

    public static Card getCardFromString(List<Card> cards, String cardName) {
        if (cardName == null || cardName.isEmpty() || cards == null) {
            return null;
        }

        Rank targetRank = getRankFromString(cardName);
        Suit targetSuit = getSuitFromString(cardName);

        return cards.stream()
                .filter(card -> {
                    Suit cardSuit = (Suit) card.getSuit();
                    Rank cardRank = (Rank) card.getRank();
                    return cardSuit.getSuitShortHand().equals(targetSuit.getSuitShortHand()) &&
                            cardRank.getRankCardValue() == targetRank.getRankCardValue();
                })
                .findFirst()
                .orElse(null);
    }


    // PRIVATE UTILITIES =======
    private static boolean isSameSuit(Card card1, Card card2) {
        Suit card1Suit = (Suit) card1.getSuit();
        Suit card2Suit = (Suit) card2.getSuit();
        return card1Suit.getSuitShortHand().equals(card2Suit.getSuitShortHand());
    }

    private static Card getHigherCardFromList(Card existingCard, List<Card> cards) {
        return cards.stream()
                .filter(playerCard -> isSameSuit(existingCard, playerCard) &&
                        isHigherRank(playerCard, existingCard))
                .findAny()
                .orElse(null);
    }

    private static boolean isHigherRank(Card card1, Card card2) {
        Rank card1Rank = (Rank) card1.getRank();
        Rank card2Rank = (Rank) card2.getRank();
        return card1Rank.getRankCardValue() > card2Rank.getRankCardValue();
    }

    private static Card getTrumpCard(List<Card> cards, String trumpSuit) {
        return cards.stream()
                .filter(playerCard -> {
                    Suit playerCardSuit = (Suit) playerCard.getSuit();
                    return playerCardSuit.getSuitShortHand().equals(trumpSuit);
                }).findAny().orElse(null);
    }
}
