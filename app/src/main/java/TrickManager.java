import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.GameGrid;

import java.util.*;

/**
 * Class responsible for Trick taking logic
 */
public class TrickManager {
    private Hand playingArea;
    private Hand[] trickWinningHands;
    public static final int trickWidth = 40;
    public static final int handWidth = 400;
    private final CardGame game;
    private final Set<Card> playedCards;
    private final TrickTakingStrategy[] playerStrategies;
    private String trumpSuit;

    public TrickManager(CardGame game, TrickTakingStrategy[] playerStrategies) {
        this.game = game;
        this.playerStrategies = playerStrategies;
        this.playedCards = new HashSet<>();
    }

    public Card selectCardForPlayer(int playerIndex, Hand[] playerHands,
                                    int bidWinnerIndex, int currentBid, boolean isAuto,
                                    List<List<String>> playerAutoMovements, int[] autoIndexHands) {

        if (isAuto) {
            return selectAutoCard(playerIndex, playerHands, playerAutoMovements, autoIndexHands);
        }

        if (playerStrategies[playerIndex] != null) {
            return selectStrategyCard(playerIndex, playerHands, bidWinnerIndex, currentBid);
        }

        // Fallback to random selection
        return selectRandomCard(playerHands[playerIndex]);
    }

    private Card selectAutoCard(int playerIndex, Hand[] playerHands,
                                List<List<String>> playerAutoMovements, int[] autoIndexHands) {
        List<String> movements = playerAutoMovements.get(playerIndex);
        int autoIndex = autoIndexHands[playerIndex];

        if (movements.size() > autoIndex && !movements.get(autoIndex).isEmpty()) {
            String movement = movements.get(autoIndex);
            autoIndexHands[playerIndex]++;
            return applyAutoMovement(playerHands[playerIndex], movement);
        }

        // Use strategy or random if no auto movement
        if (playerStrategies[playerIndex] != null) {
            return selectStrategyCard(playerIndex, playerHands, -1, 0);
        }
        return selectRandomCard(playerHands[playerIndex]);
    }

    private Card selectStrategyCard(int playerIndex, Hand[] playerHands,
                                    int bidWinnerIndex, int currentBid) {
        Card selectedCard = playerStrategies[playerIndex].selectCard(
                playerHands[playerIndex],
                playingArea.getCardList(),
                trumpSuit,
                playedCards,
                playerIndex,
                bidWinnerIndex,
                currentBid
        );

        // Record the played card
        playerStrategies[playerIndex].recordPlayedCard(selectedCard);
        playedCards.add(selectedCard);

        return selectedCard;
    }

    private Card selectRandomCard(Hand playerHand) {
        List<Card> validCards = TrickHelper.getValidCards(
                playerHand.getCardList(), playingArea.getCardList(), trumpSuit);

        // Check if there are any valid cards
        if (validCards.isEmpty()) {
            // Just in case: if no valid cards found, return any card from hand, just to avoid exception
            if (!playerHand.isEmpty()) {
                System.err.println("Warning: No valid cards found, selecting first available card");
                return playerHand.getCardList().get(0);
            } else {
                System.err.println("Error: Player hand is empty, cannot select card");
                return null;
            }
        }

        Random random = new Random();
        return validCards.get(random.nextInt(validCards.size()));
    }

    private Card applyAutoMovement(Hand hand, String movement) {
        if (hand.isEmpty()) return null;

        String[] cardStrings = movement.split("-");
        String cardDealtString = cardStrings[0];

        if (movement.isEmpty()) {
            return null;
        }

        Card dealt = TrickHelper.getCardFromString(hand.getCardList(), cardDealtString);
        if (dealt == null) {
            System.err.println("cannot draw card: " + cardDealtString + " - hand: " + hand.getCardList());
        }

        return dealt;
    }

    public int determineTrickWinner(int currentPlayerIndex) {
        return TrickWinnerCalculator.checkWinner(
                playingArea.getCardList(), trumpSuit, currentPlayerIndex);
    }

    public void transferCardsToWinner(int winnerIndex, int delayTime) {
        // Transfer cards to winner's trick pile
        for (Card card : playingArea.getCardList()) {
            trickWinningHands[winnerIndex].insert(card, true);
        }
        playingArea.removeAll(true);

        // Update display
        game.delay(delayTime);
        updateTrickHandDisplays();
        game.delay(delayTime);
    }

    private void updateTrickHandDisplays() {
        for (int i = 0; i < trickWinningHands.length; i++) {
            RowLayout layout = new RowLayout(PinochleUIManager.trickHandLocations[i], handWidth);
            layout.setRotationAngle(90);
            trickWinningHands[i].setView(game, layout);
            trickWinningHands[i].draw();
        }
    }

    public void updatePlayingAreaDisplay() {
        playingArea.setView(game, new RowLayout(PinochleUIManager.playingLocation,
                (playingArea.getNumberOfCards() + 2) * trickWidth));
        playingArea.draw();
    }

    public void resetForNewGame() {
        playedCards.clear();
        for (TrickTakingStrategy strategy : playerStrategies) {
            if (strategy != null) {
                strategy.reset();
            }
        }
    }

    public void setTrumpSuit(String trumpSuit) {
        this.trumpSuit = trumpSuit;
    }


    public boolean isValidMove(Card card, List<Card> playerCards) {
        return TrickHelper.checkValidTrick(card, playerCards,
                playingArea.getCardList(), trumpSuit);
    }

    public TrickTakingStrategy[] getPlayerStrategies() {
        return playerStrategies;
    }

    public void recordPlayedCard(Card card) {
        playedCards.add(card);
    }

    public Set<Card> getPlayedCards() {
        return playedCards;
    }

    public String getTrumpSuit() {
        return trumpSuit;
    }

    public void setPlayingArea(Hand playingArea) {
        this.playingArea = playingArea;
    }

    public void setTrickWinningHands(Hand[] trickWinningHands) {
        this.trickWinningHands = trickWinningHands;
    }

    public Hand getPlayingArea() {
        return playingArea;
    }

    public Hand[] getTrickWinningHands() {
        return trickWinningHands;
    }

    public int getTrickWidth() {
        return trickWidth;
    }

    public int getHandWidth() {
        return handWidth;
    }
}