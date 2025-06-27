import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExtendedMeldFactory extends MeldFactory{
    private final int TEN_TO_ACE_CARDS = 150;
    private final int ACE_RUN_EXTRA_KING = 190;
    private final int ACE_RUN_EXTRA_QUEEN = 190;
    private final int ROYAL_MARRIAGE = 40;
    private final int DIX = 10;
    private final int ACE_RUN_AND_ROYAL_MARRIAGE = 230;
    private final int DOUBLE_RUN = 1500;
    private final int COMMON_MARRIAGE = 20;
    private final int PINOCHLE = 40;
    private final int DOUBLE_PINOCHLE = 300;
    private final int ACES_AROUND = 100;
    private final int JACKS_ABOUND = 400;

    public List<Meld> getMelds(String trumpSuit) {
        List<Meld> meldsList = new ArrayList<>();

        meldsList.add(new Meld("Ten to Ace Cards", getTenToAceCards(trumpSuit), TEN_TO_ACE_CARDS));
        meldsList.add(new Meld("Ace Run Extra King", getAceRunExtraKing(trumpSuit), ACE_RUN_EXTRA_KING));
        meldsList.add(new Meld("Ace Run Extra Queen", getAceRunExtraQueen(trumpSuit), ACE_RUN_EXTRA_QUEEN));
        meldsList.add(new Meld("Royal Marriage", getRoyalMarriage(trumpSuit), ROYAL_MARRIAGE));

        meldsList.add(new Meld("Dix", getDix(trumpSuit), DIX));
        meldsList.add(new Meld("Ace Run And Royal Marriage", getAceRunAndRoyalMarriage(trumpSuit), ACE_RUN_AND_ROYAL_MARRIAGE));
        meldsList.add(new Meld("Double Run", getDoubleRun(trumpSuit), DOUBLE_RUN));
        meldsList.add(new Meld("Common Marriage", getCommonMarriage(trumpSuit), COMMON_MARRIAGE));
        meldsList.add(new Meld("Pinochle", getPinochle(), PINOCHLE));
        meldsList.add(new Meld("Double Pinochle", getDoublePinochle(), DOUBLE_PINOCHLE));
        meldsList.add(new Meld("Aces Around", getAcesAround(), ACES_AROUND));
        meldsList.add(new Meld("Jacks Abound", getJacksAbound(), JACKS_ABOUND));

        return meldsList;
    }

    private List<String> getDuplicate(List<String> list) {
        List<String> duplicate = new ArrayList<>(list);
        list.addAll(duplicate);
        return list;
    }

    private List<String> getTenToAceCards(String trumpSuit) {
        return Arrays.asList(
                Rank.ACE.getRankCardValue() + trumpSuit,
                Rank.JACK.getRankCardValue() + trumpSuit,
                Rank.QUEEN.getRankCardValue() + trumpSuit,
                Rank.KING.getRankCardValue() + trumpSuit,
                Rank.TEN.getRankCardValue() + trumpSuit);
    }

    //Task 1: Gets all Suit For A Rank
    private List<String> getAllSuitForRank(Rank rank) {
        List<String> allSuit = new ArrayList<>();

        for (Suit suit: Suit.values() ) {
            String shorthand = suit.getSuitShortHand();
            String card = rank.getRankCardValue() + shorthand;

            if (!allSuit.contains(card)){
                allSuit.add(card);
            }
        }
        return allSuit;
    }

    //Task 1: Get Pinohcle
    private List<String> getPinochle() {
        return Arrays.asList(Rank.JACK.getRankCardValue() + "D",
                Rank.QUEEN.getRankCardValue() + "S");
    }

    //Task 1: Adds Royal Marriage
    private List<String> getRoyalMarriage(String trumpSuit) {
        return Arrays.asList(
                Rank.QUEEN.getRankCardValue() + trumpSuit,
                Rank.KING.getRankCardValue() + trumpSuit);
    }

    private List<String> getAceRunExtraKing(String trumpSuit) {
        List<String> cards = new ArrayList<>(getTenToAceCards(trumpSuit));
        cards.add(Rank.KING.getRankCardValue() + trumpSuit);

        return cards;
    }

    private List<String> getAceRunExtraQueen(String trumpSuit) {
        List<String> cards = new ArrayList<>(getTenToAceCards(trumpSuit));
        cards.add(Rank.QUEEN.getRankCardValue() + trumpSuit);

        return cards;
    }

    //Task 1: Start (Extra Melds)

    private List<String> getDix(String trumpSuit) {
        List<String> cards = new ArrayList<>();
        cards.add(Rank.NINE.getRankCardValue() + trumpSuit);

        return cards;
    }

    private List<String> getAceRunAndRoyalMarriage(String trumpSuit) {
        List<String> cards = new ArrayList<>(getTenToAceCards(trumpSuit));
        List<String> royalMarriage = new ArrayList<>(getRoyalMarriage(trumpSuit));
        cards.addAll(royalMarriage);

        return cards;
    }

    private List<String> getDoubleRun(String trumpSuit) {
        List<String> cards = new ArrayList<>(getTenToAceCards(trumpSuit));
        cards = getDuplicate(cards);

        return cards;
    }

    private List<String> getCommonMarriage(String trumpSuit) {

        List<String> cards = new ArrayList<>();

        for (Suit suit: Suit.values()) {
            String shorthand = suit.getSuitShortHand();

            if (!shorthand.equals(trumpSuit)) {
                cards.add(Rank.KING.getRankCardValue() + shorthand);
                cards.add(Rank.QUEEN.getRankCardValue() + shorthand);
            }
        }
        return cards;
    }


    private List<String> getDoublePinochle() {
        List<String> cards = new ArrayList<>(getPinochle());
        cards = getDuplicate(cards);

        return cards;
    }

    private List<String> getAcesAround() {

        List<String> cards = new ArrayList<>(getAllSuitForRank(Rank.ACE));

        return cards;
    }

    private List<String> getJacksAbound() {
        List<String> cards = new ArrayList<>(getAllSuitForRank(Rank.JACK));
        cards = getDuplicate(cards);

        return cards;
    }
}