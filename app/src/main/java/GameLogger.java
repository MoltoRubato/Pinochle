import ch.aplu.jcardgame.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 *  Class responsible for all logging
 */

public class GameLogger {
    private final StringBuilder logResult = new StringBuilder();

    public void addCardPlayedToLog(int player, Card card) {
        logResult.append("P" + player + "-");
        Rank cardRank = (Rank) card.getRank();
        Suit cardSuit = (Suit) card.getSuit();
        logResult.append(cardRank.getCardLog() + cardSuit.getSuitShortHand());
        logResult.append(",");
    }

    public void addBidInfoToLog(int bidWinPlayerIndex, int currentBid) {
        logResult.append("Bid:" + bidWinPlayerIndex + "-" + currentBid + "\n");
    }

    public void addTrumpInfoToLog(String trumpSuit, int[] scores) {
        logResult.append("Trump: " + trumpSuit + "\n");
        logResult.append("Melding Scores: " + scores[0] + "-" + scores[1] + "\n");
    }

    public void addRoundInfoToLog(int roundNumber) {
        logResult.append("\n");
        logResult.append("Round" + roundNumber + ":");
    }

    public void addPlayerCardsToLog(Hand[] hands, int nbPlayers) {
        logResult.append("Initial Cards:");
        for (int i = 0; i < nbPlayers; i++) {
            logResult.append("P" + i + "-");
            logResult.append(convertHandToString(hands[i]));
        }
    }

    public void addEndOfGameToLog(Hand[] trickWinningHands, int[] scores, List<Integer> winners, int nbPlayers) {
        logResult.append("\n");
        logResult.append("Trick Winning: ");
        for (int i = 0; i < nbPlayers; i++) {
            logResult.append("P" + i + ":");
            logResult.append(convertHandToString(trickWinningHands[i]));
        }
        logResult.append("\n");
        logResult.append("Final Score: ");
        for (int i = 0; i < scores.length; i++) {
            logResult.append(scores[i] + ",");
        }
        logResult.append("\n");
        logResult.append("Winners: " + String.join(", ", winners.stream().map(String::valueOf).collect(Collectors.toList())));
    }

    private String convertHandToString(Hand hand) {
        StringBuilder sb = new StringBuilder();
        sb.append(hand.getCardList().stream().map(card -> {
            Rank rank = (Rank) card.getRank();
            Suit suit = (Suit) card.getSuit();
            return rank.getCardLog() + suit.getSuitShortHand();
        }).collect(Collectors.joining(",")));
        sb.append("-");
        return sb.toString();
    }

    public String getLogResult() {
        return logResult.toString();
    }

    public void clearLog() {
        logResult.setLength(0);
    }
}