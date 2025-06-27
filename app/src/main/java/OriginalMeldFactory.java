import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OriginalMeldFactory extends MeldFactory{

    private final int TEN_TO_ACE_CARDS = 150;
    private final int ACE_RUN_EXTRA_KING = 190;
    private final int ACE_RUN_EXTRA_QUEEN = 190;
    private final int ROYAL_MARRIAGE = 40;

    @Override
    public List<Meld> getMelds(String trumpSuit) {
        List<Meld> meldsList = new ArrayList<>();

        meldsList.add(new Meld("Ten to Ace Cards", getTenToAceCards(trumpSuit), TEN_TO_ACE_CARDS));
        meldsList.add(new Meld("Ace Run Extra King", getAceRunExtraKing(trumpSuit), ACE_RUN_EXTRA_KING));
        meldsList.add(new Meld("Ace Run Extra Queen", getAceRunExtraQueen(trumpSuit), ACE_RUN_EXTRA_QUEEN));
        meldsList.add(new Meld("Royal Marriage", getRoyalMarriage(trumpSuit), ROYAL_MARRIAGE));

        return meldsList;
    }

    private List<String> getTenToAceCards(String trumpSuit) {
        return Arrays.asList(
                Rank.ACE.getRankCardValue() + trumpSuit,
                Rank.JACK.getRankCardValue() + trumpSuit,
                Rank.QUEEN.getRankCardValue() + trumpSuit,
                Rank.KING.getRankCardValue() + trumpSuit,
                Rank.TEN.getRankCardValue() + trumpSuit);
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




}