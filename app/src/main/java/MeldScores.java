import ch.aplu.jcardgame.Card;

import java.util.*;

public class MeldScores {
    private final String trumpSuit;
    private List<Meld> melds = new ArrayList<>();
    public static boolean useAdditionalMelds = false;

    private final MeldFactory factory;

    public MeldScores(String trumpSuit) {
        this.trumpSuit = trumpSuit;
        if (useAdditionalMelds) {
            this.factory = new ExtendedMeldFactory();
        } else {
            this.factory = new OriginalMeldFactory();
        }

        this.melds = factory.createMelds(trumpSuit);
    }


    // Gets the total melding score for a hand based on the trump suit
    static int calculateMeldingScore(ArrayList<Card> cards, String TrumpSuit) {
        MeldScores meldScores = new MeldScores(TrumpSuit);
        List<Meld> meldList = meldScores.getMelds();

        List<Card> remainingCards = new ArrayList<>(cards);

        int totalScore = 0;

        for (Meld meld : meldList) {
            if (meld.getName().equals("Common Marriage")) {
                for (Suit suit : Suit.values()) {
                    String shorthand = suit.getSuitShortHand();

                    if (!shorthand.equals(TrumpSuit)) {
                        List<String> cardsToCheck = Arrays.asList(
                                Rank.KING.getRankCardValue() + shorthand,
                                Rank.QUEEN.getRankCardValue() + shorthand
                        );

                        if (checkCardInList(remainingCards, cardsToCheck)) {
                            totalScore += meld.getScore();
                            remainingCards = removeCardFromList(remainingCards, cardsToCheck);
                        }
                    }
                }
            } else {
                List<String> cardsToCheck = meld.getCards();
                if (checkCardInList(remainingCards, cardsToCheck)) {
                    totalScore += meld.getScore();
                    remainingCards = removeCardFromList(remainingCards, cardsToCheck);
                }
            }
        }
        return totalScore;
    }

    static private boolean checkCardInList(List<Card> cardList, List<String> cardsToCheck) {//
        ArrayList<String> cardsToRemove = new ArrayList<>(cardsToCheck);
        for (Card card : cardList) {
            String cardName = getCardName(card);
            cardsToRemove.remove(cardName);
        }
        return cardsToRemove.isEmpty();
    }

    static private List<Card> removeCardFromList(List<Card> cardList, List<String> cardsToRemove) {//
        List<Card> newCardList = new ArrayList<>();
        List<String> newCardsToRemove = new ArrayList<>(cardsToRemove);
        for (Card card : cardList) {
            String cardName = getCardName(card);
            if (newCardsToRemove.contains(cardName)) {
                newCardsToRemove.remove(cardName);
            } else {
                newCardList.add(card);
            }
        }
        return newCardList;
    }

    static private String getCardName(Card card) {
        Rank rank = (Rank) card.getRank();
        Suit suit = (Suit) card.getSuit();
        return rank.getRankCardValue() + suit.getSuitShortHand();
    }


    public List<Meld> getMelds() {
        return melds;
    }

}