import ch.aplu.jcardgame.*;

import java.util.*;

/**
 * Class responsible for cutthroat logic
 */
public class CutthroatManager {
    private final Pinochle gameContext;

    // Cutthroat-specific fields
    private Hand stockpile;
    private Hand[] faceUpCards;
    private Card selectedStockpileCard = null;
    private boolean hasSelectedStockpileCard = false;

    // Card selection phase variables
    private boolean isSelectingFinalCards = false;
    private Set<Card> selectedCards = new HashSet<>();

    // Cut throat extra cards
    private int computerExtraCardIndex = 0;
    private int humanExtraCardIndex = 0;

    // Constants
    private final int COMPUTER_PLAYER_INDEX = 0;
    private final int HUMAN_PLAYER_INDEX = 1;

    public CutthroatManager(Pinochle gameContext) {
        this.gameContext = gameContext;
    }

    public void initializeCutthroatMode(Deck deck) {
        if (gameContext.config.isCutthroatMode()) {
            stockpile = new Hand(deck);
            faceUpCards = new Hand[2];
            faceUpCards[0] = new Hand(deck);
            faceUpCards[1] = new Hand(deck);
        }
    }

    public void moveRemainingCardsToStockpile(Hand pack) {
        if (gameContext.config.isCutthroatMode()) {
            while (!pack.isEmpty()) {
                Card card = pack.getCardList().get(0);
                card.removeFromHand(false);
                stockpile.insert(card, false);
            }
        }
    }

    public void performCutThroatMode(Hand[] hands, String trumpSuit, int bidWinPlayerIndex, int handWidth) {
        addExtraCardsToHands(hands);
        drawStockpileCards(hands, handWidth);
        selectFinalCards(hands, trumpSuit, bidWinPlayerIndex, handWidth);
        gameContext.refresh();
    }

    private void addExtraCardsToHands(Hand[] hands) {
        // Add extra cards to computer player (player 0)
        for (String cardName : gameContext.config.getPlayer0ExtraCards()) {
            Card card = TrickHelper.getCardFromString(stockpile.getCardList(), cardName);
            if (card != null) {
                card.removeFromHand(false);
                hands[COMPUTER_PLAYER_INDEX].insert(card, false);
            }
        }

        // Add extra cards to human player (player 1)
        for (String cardName : gameContext.config.getPlayer1ExtraCards()) {
            Card card = TrickHelper.getCardFromString(stockpile.getCardList(), cardName);
            if (card != null) {
                card.removeFromHand(false);
                hands[HUMAN_PLAYER_INDEX].insert(card, false);
            }
        }
    }

    private void drawStockpileCards(Hand[] hands, int handWidth) {
        System.out.println("Stockpile size: " + stockpile.getNumberOfCards());

        // Draw 2 face-up cards from stockpile
        for (int i = 0; i < 2 && !stockpile.isEmpty(); i++) {
            Card card = stockpile.getCardList().get(0);
            card.removeFromHand(false);
            faceUpCards[i].insert(card, true);
        }

        gameContext.uiManager.setupStockpileDisplay(faceUpCards);
        showStockpileSelection(hands);

        // Distribute remaining stockpile cards alternately (bid winner first)
        int currentPlayer = gameContext.getBidWinPlayerIndex();
        while (!stockpile.isEmpty()) {
            Card card = stockpile.getCardList().get(0);
            card.removeFromHand(false);
            hands[currentPlayer].insert(card, false);
            currentPlayer = (currentPlayer + 1) % gameContext.getNbPlayers();
        }

        for (int i = 0; i < gameContext.getNbPlayers(); i++) {
            gameContext.uiManager.redrawHand(hands[i], i, handWidth);
        }
    }

    private void showStockpileSelection(Hand[] hands) {
        int bidWinPlayerIndex = gameContext.getBidWinPlayerIndex();

        // Display face-up cards
        for (int i = 0; i < 2; i++) {
            if (!faceUpCards[i].isEmpty()) {
                faceUpCards[i].setView(gameContext, new RowLayout(gameContext.uiManager.stockpileCardLocations[i], 100));
                faceUpCards[i].draw();
            }
        }

        if (bidWinPlayerIndex == HUMAN_PLAYER_INDEX && !gameContext.config.isAuto()) {
            // Human player selection (only in non-auto mode)
            gameContext.uiManager.initCutthroatUI(cardIndex -> {
                if (!faceUpCards[cardIndex].isEmpty()) {
                    selectedStockpileCard = faceUpCards[cardIndex].getCardList().get(0);
                    hasSelectedStockpileCard = true;
                    System.out.println("Selected stockpile card: " + getCardName(selectedStockpileCard));
                }
            });
            while (!hasSelectedStockpileCard) {
                gameContext.delay(gameContext.config.getDelayTime());
            }
            gameContext.uiManager.removeCutthroatUI();
        } else {
            // Computer player or auto mode selection
            handleComputerStockpileSelection();
        }

        // Add selected card to bid winner's hand
        if (selectedStockpileCard != null) {
            System.out.println("Bid winner (player " + bidWinPlayerIndex + ") selected: " + getCardName(selectedStockpileCard));
            hands[bidWinPlayerIndex].insert(selectedStockpileCard, false);

            // Add remaining card to dealer's hand
            int dealerIndex = 1 - bidWinPlayerIndex;
            for (int i = 0; i < 2; i++) {
                if (!faceUpCards[i].isEmpty()) {
                    Card remainingCard = faceUpCards[i].getCardList().get(0);
                    if (remainingCard != selectedStockpileCard) {
                        System.out.println("Dealer (player " + dealerIndex + ") gets: " + getCardName(remainingCard));
                        hands[dealerIndex].insert(remainingCard, false);
                        break;
                    }
                }
            }
        }

        // Clear face-up cards display
        for (int i = 0; i < 2; i++) {
            faceUpCards[i].removeAll(true);
        }
    }

    private void handleComputerStockpileSelection() {
        if (gameContext.config.isAuto()) {
            List<String> computerExtraCards = gameContext.config.getPlayer0ExtraCards();
            if (computerExtraCardIndex < computerExtraCards.size()) {
                // Use predetermined card from auto mode
                String cardName = computerExtraCards.get(computerExtraCardIndex);
                computerExtraCardIndex++;

                for (int i = 0; i < 2; i++) {
                    if (!faceUpCards[i].isEmpty()) {
                        Card card = faceUpCards[i].getCardList().get(0);
                        if (getCardName(card).equals(cardName)) {
                            selectedStockpileCard = card;
                            hasSelectedStockpileCard = true;
                            return;
                        }
                    }
                }
            }
        }

        // Default: choose first available card
        if (!faceUpCards[0].isEmpty()) {
            selectedStockpileCard = faceUpCards[0].getCardList().get(0);
        } else if (!faceUpCards[1].isEmpty()) {
            selectedStockpileCard = faceUpCards[1].getCardList().get(0);
        }
        hasSelectedStockpileCard = true;
    }

    private void selectFinalCards(Hand[] hands, String trumpSuit, int bidWinPlayerIndex, int handWidth) {
        System.out.println("Starting final card selection phase...");
        // Each player must select 12 cards from their 24 cards
        for (int playerIndex = 0; playerIndex < gameContext.getNbPlayers(); playerIndex++) {
            System.out.println("Player " + playerIndex + " selecting final cards from " + hands[playerIndex].getNumberOfCards() + " cards...");
            if (playerIndex == HUMAN_PLAYER_INDEX && !gameContext.config.isAuto()) {
                handleHumanFinalCardSelection(hands[playerIndex], handWidth);
            } else {
                handleComputerFinalCardSelection(hands[playerIndex], trumpSuit);
            }
            System.out.println("Player " + playerIndex + " final hand size: " + hands[playerIndex].getNumberOfCards());
        }
        gameContext.refresh();
    }

    private void handleHumanFinalCardSelection(Hand humanHand, int handWidth) {
        if (gameContext.config.isAuto() && !gameContext.config.getPlayer1FinalCards().isEmpty()) {
            // Auto mode: use predetermined final cards
            List<Card> cardsToKeep = new ArrayList<>();
            for (String cardName : gameContext.config.getPlayer1FinalCards()) {
                Card card = TrickHelper.getCardFromString(humanHand.getCardList(), cardName);
                if (card != null) {
                    cardsToKeep.add(card);
                }
            }
            // Remove cards not in final selection
            List<Card> allCards = new ArrayList<>(humanHand.getCardList());
            for (Card card : allCards) {
                if (!cardsToKeep.contains(card)) {
                    card.removeFromHand(false);
                }
            }
        } else {
            // Manual selection
            isSelectingFinalCards = true;
            selectedCards.clear();

            gameContext.uiManager.setupExpandedHandLayout(humanHand, HUMAN_PLAYER_INDEX, handWidth);
            gameContext.uiManager.initFinalCardSelectionUI(this::finalizeFinalCardSelection);
            humanHand.setTouchEnabled(true);

            while (isSelectingFinalCards) {
                gameContext.delay(gameContext.config.getDelayTime());
            }
            gameContext.uiManager.removeFinalCardSelectionUI();
            humanHand.setTouchEnabled(false);
            gameContext.refresh();
        }

        gameContext.uiManager.redrawHand(humanHand, HUMAN_PLAYER_INDEX, handWidth);
        gameContext.refresh();
    }

    private void finalizeFinalCardSelection() {
        Hand humanHand = gameContext.getHands()[HUMAN_PLAYER_INDEX];
        if (selectedCards.size() == 12) {
            // Remove unselected cards
            List<Card> allCards = new ArrayList<>(humanHand.getCardList());
            for (Card card : allCards) {
                if (!selectedCards.contains(card)) {
                    card.removeFromHand(false);
                }
            }
            // Reset card visuals
            for (Card card : selectedCards) {
                card.setVerso(false);
            }
            isSelectingFinalCards = false;
        }
    }

    private void handleComputerFinalCardSelection(Hand playerHand, String trumpSuit) {
        List<Card> allCards = new ArrayList<>(playerHand.getCardList());
        Map<String, List<Card>> suitGroups = new HashMap<>();

        // Group cards by suit
        for (Card card : allCards) {
            String suitKey = ((Suit) card.getSuit()).getSuitShortHand();
            suitGroups.computeIfAbsent(suitKey, k -> new ArrayList<>()).add(card);
        }

        // Count cards in each suit (excluding trump)
        Map<String, Integer> nonTrumpSuitCounts = new HashMap<>();
        for (Map.Entry<String, List<Card>> entry : suitGroups.entrySet()) {
            if (!entry.getKey().equals(trumpSuit)) {
                nonTrumpSuitCounts.put(entry.getKey(), entry.getValue().size());
            }
        }

        // Sort suits by count (ascending) to discard from least represented suits first
        List<String> suitsToDiscard = nonTrumpSuitCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .toList();

        int cardsToDiscard = allCards.size() - 12;
        List<Card> cardsToRemove = new ArrayList<>();

        // Discard cards from suits with fewer cards first
        for (String suit : suitsToDiscard) {
            List<Card> suitCards = suitGroups.get(suit);
            if (suitCards != null) {
                // Sort by value (ascending) to discard lower value cards first
                suitCards.sort((a, b) -> {
                    Rank rankA = (Rank) a.getRank();
                    Rank rankB = (Rank) b.getRank();
                    return Integer.compare(rankA.getRankCardValue(), rankB.getRankCardValue());
                });

                for (Card card : suitCards) {
                    if (cardsToRemove.size() < cardsToDiscard) {
                        cardsToRemove.add(card);
                    } else {
                        break;
                    }
                }
                if (cardsToRemove.size() >= cardsToDiscard) {
                    break;
                }
            }
        }

        // Remove selected cards
        for (Card card : cardsToRemove) {
            card.removeFromHand(false);
        }

        playerHand.sort(Hand.SortType.SUITPRIORITY, false);
        playerHand.draw();
    }

    private String getCardName(Card card) {
        Rank rank = (Rank) card.getRank();
        Suit suit = (Suit) card.getSuit();
        return rank.getRankCardValue() + suit.getSuitShortHand();
    }

    // Method to handle final card selection from the main game class
    public void handleFinalCardSelection(Card card) {
        if (selectedCards.contains(card)) {
            selectedCards.remove(card);
            card.setVerso(false); // Deselect visual indicator
        } else if (selectedCards.size() < 12) {
            selectedCards.add(card);
            card.setVerso(true); // Select visual indicator
        }
        gameContext.getHands()[HUMAN_PLAYER_INDEX].draw();
        gameContext.uiManager.setFinalCardSelectionButtonEnabled(selectedCards.size() == 12);
        gameContext.refresh();
    }

    // Getters for accessing private fields if needed
    public boolean isSelectingFinalCards() {
        return isSelectingFinalCards;
    }
}