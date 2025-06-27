import ch.aplu.jcardgame.*;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class Pinochle extends CardGame {
    final GameConfig config;
    private GameState currentState;

    // Managers & Helper classes
    final PinochleUIManager uiManager;
    private final BiddingManager biddingManager;
    private final CutthroatManager cutthroatManager;
    private final TrickManager trickManager;

    final GameLogger gameLogger = new GameLogger();

    // Constants
    private final String version = "1.0";
    public final int nbPlayers = 2;
    public final int nbStartCards = 12;
    private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
    final Map<String, String> trumpImages = new HashMap<>(Map.of(
            Suit.SPADES.getSuitShortHand(), "sprites/bigspade.gif",
            Suit.CLUBS.getSuitShortHand(), "sprites/bigclub.gif",
            Suit.DIAMONDS.getSuitShortHand(), "sprites/bigdiamond.gif",
            Suit.HEARTS.getSuitShortHand(), "sprites/bigheart.gif"));

    // Game variables
    private Hand[] hands;
    private String trumpSuit = null;
    private final List<List<String>> playerAutoMovements = new ArrayList<>();
    private int[] scores = new int[nbPlayers];
    private int[] autoIndexHands = new int[nbPlayers];

    public Pinochle(Properties properties) {
        super(700, 700, 30);
        this.config = new GameConfig(properties);
        this.uiManager = new PinochleUIManager(this);
        this.cutthroatManager = new CutthroatManager(this);

        // Initialise Trick manager
        TrickTakingStrategy[] playerStrategies = new TrickTakingStrategy[nbPlayers];
        playerStrategies[GameConfig.COMPUTER_PLAYER_INDEX] = TrickTakingStrategyFactory.createStrategy(
                config.isSmartTrickMode() ? TrickTakingStrategyFactory.SMART_TRICK_TAKING : TrickTakingStrategyFactory.RANDOM_TRICK_TAKING,
                GameConfig.random
        );
        this.trickManager = new TrickManager(this, playerStrategies);

        // Initialize bidding strategy
        BiddingStrategy computerBiddingStrategy = BiddingStrategyFactory.createStrategy(
                config.isPlayer0SmartBidding() ? BiddingStrategyFactory.SMART_BIDDING : BiddingStrategyFactory.RANDOM_BIDDING,
                BiddingManager.BID_SELECTION_VALUE,
                BiddingManager.MAX_SINGLE_BID
        );
        this.biddingManager = new BiddingManager(this, computerBiddingStrategy);

        MeldScores.useAdditionalMelds = config.useAdditionalMelds();
        currentState = null;
    }

    public void performTrumpSelection() {
        if (config.isAuto()) {
            trumpSuit = config.getTrumpSuit();
            uiManager.updateTrumpActor(trumpSuit);
            trickManager.setTrumpSuit(trumpSuit);
            return;
        }

        int bidWinPlayerIndex = biddingManager.getBidWinPlayerIndex();
        if (bidWinPlayerIndex == GameConfig.COMPUTER_PLAYER_INDEX) {
            if (config.isPlayer0SmartBidding() && biddingManager.getComputerBiddingStrategy() instanceof SmartBiddingStrategy smartStrategy) {
                trumpSuit = smartStrategy.determineLikelyTrumpSuit(hands[GameConfig.COMPUTER_PLAYER_INDEX]);
            } else {
                Suit selectedTrumpSuit = Arrays.stream(Suit.values()).findAny().get();
                trumpSuit = selectedTrumpSuit.getSuitShortHand();
            }
        } else {
            uiManager.initTrumpSelectionUI(selectedTrumpSuit -> trumpSuit = selectedTrumpSuit);
            while (trumpSuit == null) delay(config.getDelayTime());
            uiManager.removeTrumpSelectionUI();
        }
        uiManager.updateTrumpActor(trumpSuit);
        trickManager.setTrumpSuit(trumpSuit);
    }

    public void performMelding() {
        trickManager.resetForNewGame();

        for (int i = 0; i < nbPlayers; i++) {
            scores[i] = MeldScores.calculateMeldingScore(hands[i].getCardList(), trumpSuit);
            uiManager.updateScore(i, scores);;
            delay(config.getDelayTime());
        }
        gameLogger.addTrumpInfoToLog(trumpSuit, scores);
        gameLogger.addPlayerCardsToLog(hands, nbPlayers);
    }

    public void performTrickTaking() {
        int nextPlayer = biddingManager.getBidWinPlayerIndex();
        int numberOfCards = hands[GameConfig.COMPUTER_PLAYER_INDEX].getNumberOfCards();

        for (int i = 0; i < numberOfCards; i++) {
            gameLogger.addRoundInfoToLog(i);

            for (int j = 0; j < nbPlayers; j++) {
                selected = playTurnForPlayer(nextPlayer);

                if (selected != null) {
                    selected.removeFromHand(true);
                    gameLogger.addCardPlayedToLog(nextPlayer, selected);
                    trickManager.getPlayingArea().insert(selected, true);
                    trickManager.updatePlayingAreaDisplay();

                    if (trickManager.getPlayingArea().getCardList().size() == 2) {
                        delay(config.getDelayTime());
                        int trickWinnerIndex = trickManager.determineTrickWinner(nextPlayer);
                        trickManager.transferCardsToWinner(trickWinnerIndex, config.getDelayTime());
                        nextPlayer = trickWinnerIndex;
                    } else {
                        nextPlayer = (nextPlayer + 1) % nbPlayers;
                    }
                }
            }
        }
        updateTrickScore();
    }

    public void performGameOver() {
        for (int i = 0; i < nbPlayers; i++) uiManager.updateScore(i, scores);;
        int maxScore = 0;
        for (int i = 0; i < nbPlayers; i++) if (scores[i] > maxScore) maxScore = scores[i];
        List<Integer> winners = new ArrayList<>();
        for (int i = 0; i < nbPlayers; i++) if (scores[i] == maxScore) winners.add(i);
        String winText;
        if (winners.size() == 1) {
            winText = "Game over. Winner is player: " + winners.iterator().next();
        } else {
            winText = "Game Over. Drawn winners are players: " +
                    String.join(", ", winners.stream().map(String::valueOf).collect(Collectors.toList()));
        }
        uiManager.showGameOverUI(winText);
        gameLogger.addEndOfGameToLog(trickManager.getTrickWinningHands(), scores, winners, nbPlayers);

        // Clean up the UI after the game ends
        uiManager.cleanup();
    }

    private void initScores() {
        Arrays.fill(scores, 0);
    }

    private Card selected;

    private void initGame() {
        hands = new Hand[nbPlayers];
        Hand[] trickWinningHands = new Hand[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            hands[i] = new Hand(deck);
            trickWinningHands[i] = new Hand(deck);
        }
        Hand playingArea = new Hand(deck);

        cutthroatManager.initializeCutthroatMode(deck);

        dealingOut(hands, nbPlayers, nbStartCards);
        uiManager.setupPlayingArea(playingArea, TrickManager.trickWidth);

        for (int i = 0; i < nbPlayers; i++) {
            hands[i].sort(Hand.SortType.SUITPRIORITY, false);
        }

        trickManager.setTrickWinningHands(trickWinningHands);
        trickManager.setPlayingArea(playingArea);

        CardListener cardListener = new CardAdapter() {
            public void leftDoubleClicked(Card card) {
                if (cutthroatManager.isSelectingFinalCards()) {
                    cutthroatManager.handleFinalCardSelection(card);
                    return;
                }
                if (!trickManager.isValidMove(card, hands[GameConfig.HUMAN_PLAYER_INDEX].getCardList())) {
                    setStatus("Card is not valid. Player needs to choose higher card of the same suit or trump suit");
                    return;
                }
                selected = card;
                hands[GameConfig.HUMAN_PLAYER_INDEX].setTouchEnabled(false);
            }
        };
        hands[GameConfig.HUMAN_PLAYER_INDEX].addCardListener(cardListener);
        uiManager.setupHandLayouts(hands, TrickManager.handWidth, nbPlayers);
        uiManager.setupTrickHandLayouts(trickManager.getTrickWinningHands(), TrickManager.handWidth, nbPlayers);
    }


    // return random Card from ArrayList
    public static Card randomCard(ArrayList<Card> list) {
        int x = GameConfig.random.nextInt(list.size());
        return list.get(x);
    }

    private void dealingOut(Hand[] hands, int nbPlayers, int nbCardsPerPlayer) {
        Hand pack = deck.toHand(false);

        for (int i = 0; i < nbPlayers; i++) {
            List<String> initialCards = config.getPlayerInitialCards(i);
            for (String initialCard : initialCards) {
                if (initialCard.length() <= 1) {
                    continue;
                }
                Card card = TrickHelper.getCardFromString(pack.getCardList(), initialCard);
                if (card != null) {
                    card.removeFromHand(false);
                    hands[i].insert(card, false);
                }
            }
        }

        for (int i = 0; i < nbPlayers; i++) {
            int cardsToDealt = nbCardsPerPlayer - hands[i].getNumberOfCards();
            for (int j = 0; j < cardsToDealt; j++) {
                if (pack.isEmpty()) return;
                Card dealt = randomCard(pack.getCardList());
                dealt.removeFromHand(false);
                hands[i].insert(dealt, false);
            }
        }

        cutthroatManager.moveRemainingCardsToStockpile(pack);
    }

    public void performCutThroatMode() {
        int bidWinPlayerIndex = biddingManager.getBidWinPlayerIndex();
        cutthroatManager.performCutThroatMode(hands, trumpSuit, bidWinPlayerIndex, TrickManager.handWidth);
    }

    private void playGame() {
        // First state is the bidding state
        currentState = new BiddingState(this);

        // Execute all the game states
        while (!(currentState instanceof GameOverState)) {
            currentState.handle();
        }
        currentState.handle(); // GameOverState
    }

    private Card playTurnForPlayer(int playerIndex) {
        if (!config.isAuto() && playerIndex == GameConfig.HUMAN_PLAYER_INDEX) {
            return handleHumanTurn(playerIndex);
        } else {
            return handleComputerTurn(playerIndex);
        }
    }

    private Card handleHumanTurn(int playerIndex) {
        hands[playerIndex].setTouchEnabled(true);
        setStatus("Player " + playerIndex + " is playing. Please double click on a card to discard");

        selected = null;
        while (selected == null) {
            delay(config.getDelayTime());
        }

        // Record human played card for strategies that track cards
        trickManager.recordPlayedCard(selected);
        if (trickManager.getPlayerStrategies()[GameConfig.COMPUTER_PLAYER_INDEX] != null) {
            trickManager.getPlayerStrategies()[GameConfig.COMPUTER_PLAYER_INDEX].recordPlayedCard(selected);
        }

        return selected;
    }

    private Card handleComputerTurn(int playerIndex) {
        setStatusText("Player " + playerIndex + " thinking...");

        return trickManager.selectCardForPlayer(
                playerIndex, hands, biddingManager.getBidWinPlayerIndex(), biddingManager.getCurrentBid(),
                config.isAuto(), playerAutoMovements, autoIndexHands
        );
    }

    private void updateTrickScore() {
        int currentBid = biddingManager.getCurrentBid();
        int bidWinPlayerIndex = biddingManager.getBidWinPlayerIndex();

        for (int i = 0; i < nbPlayers; i++) {
            List<Card> cards = trickManager.getTrickWinningHands()[i].getCardList();
            int score = 0;
            for (Card card : cards) {
                Rank rank = (Rank) card.getRank();
                Suit suit = (Suit) card.getSuit();
                boolean isNineCard = rank.getRankCardValue() == Rank.NINE.getRankCardValue();
                boolean isTrumpCard = suit.getSuitShortHand().equals(trumpSuit);
                if (isNineCard && isTrumpCard) {
                    score += Rank.NINE_TRUMP;
                } else {
                    score += rank.getScoreValue();
                }
            }

            scores[i] += score;
            if (i == bidWinPlayerIndex) {
                if (scores[i] < currentBid) {
                    scores[i] = 0;
                }
            }
        }
    }

    private void setupPlayerAutoMovements() {
        List<String> player0Movements = config.getPlayer0CardsPlayed();
        List<String> player1Movements = config.getPlayer1CardsPlayed();

        playerAutoMovements.clear();
        playerAutoMovements.add(player0Movements);
        playerAutoMovements.add(player1Movements);
    }

    public String runApp() {
        setTitle("Pinochle  (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        setStatusText("Initializing...");
        initScores();
        uiManager.initScore(scores, nbPlayers);
        setupPlayerAutoMovements();
        initGame();
        playGame();
        refresh();
        return gameLogger.getLogResult();
    }

    // Getters
    public Hand[] getHands() {
        return hands;
    }
    public int getBidWinPlayerIndex() {
        return biddingManager.getBidWinPlayerIndex();
    }
    public int getNbPlayers() {
        return nbPlayers;
    }
    public BiddingManager getBiddingManager() {
        return biddingManager;
    }

    // Setters
    public void setStatus(String string) {
        setStatusText(string);
    }
    public void setState(GameState state) {
        this.currentState = state;
        setStatus("Current phase: " + state.getStateName());
    }
}