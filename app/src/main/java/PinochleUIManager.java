import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import java.awt.*;

/**
 * Class responsible for UI
 */
public class PinochleUIManager {
    private final Pinochle game;
    private final Font bigFont = new Font("Arial", Font.BOLD, 36);
    private final Font smallFont = new Font("Arial", Font.BOLD, 18);

    // Game layout constants
    private final Location[] handLocations = {
            new Location(350, 625),
            new Location(350, 75),
    };

    private final Location[] scoreLocations = {
            new Location(575, 675),
            new Location(25, 25),
    };

    static final Location[] trickHandLocations = {
            new Location(75, 350),
            new Location(625, 350)
    };

    static final Location playingLocation = new Location(350, 350);
    private final Location textLocation = new Location(350, 450);
    private final TextActor[] scoreActors = {null, null, null, null};

    // Bidding UI locations
    private final Location bidSelectionLocation = new Location(600, 100);
    private final Location bidConfirmLocation = new Location(660, 100);
    private final Location bidPassLocation = new Location(630, 150);
    private final Location playerBidLocation = new Location(550, 30);
    private final Location currentBidLocation = new Location(550, 50);
    private final Location newBidLocation = new Location(550, 75);

    // Trump selection locations
    private final Location trumpInstructionLocation = new Location(550, 80);
    private final Location clubTrumpLocation = new Location(580, 100);
    private final Location spadeTrumpLocation = new Location(610, 100);
    private final Location diamondTrumpLocation = new Location(640, 100);
    private final Location heartTrumpLocation = new Location(670, 100);
    private final Location trumpLocation = new Location(620, 120);

    // Cut-throat mode locations
    private final Location cutthroatInstructionLocation = new Location(350, 250);
    final Location[] stockpileCardLocations = {
            new Location(300, 350),
            new Location(400, 350)
    };
    private final Location confirmCardSelectionLocation = new Location(350, 500);
    private final Location cardSelectionInstructionLocation = new Location(350, 480);

    // Bidding UI elements
    private final GGButton bidSelectionActor = new GGButton("sprites/bid_10.gif", false);
    private final GGButton bidConfirmActor = new GGButton("sprites/done30.gif", false);
    private final GGButton bidPassActor = new GGButton("sprites/bid_pass.gif", false);
    private TextActor playerBidActor;
    private TextActor currentBidActor;
    private TextActor newBidActor;

    // Trump selection UI
    private TextActor trumpInstructionActor;
    private final GGButton clubTrumpActor = new GGButton("sprites/clubs_item.png", false);
    private final GGButton spadeTrumpActor = new GGButton("sprites/spades_item.png", false);
    private final GGButton diamondTrumpActor = new GGButton("sprites/diamonds_item.png", false);
    private final GGButton heartTrumpActor = new GGButton("sprites/hearts_item.png", false);
    private Actor trumpActor;

    // Cut-throat mode UI
    private GGButton[] stockpileCardButtons = new GGButton[2];
    private TextActor cutthroatInstructionActor;
    private GGButton confirmCardSelectionButton;
    private TextActor cardSelectionInstructionActor;

    // Callback interfaces
    public interface BiddingCallbacks {
        void onBidSelection();
        void onBidConfirm();
        void onBidPass();
    }

    public interface TrumpSelectionCallbacks {
        void onTrumpSelected(String selectedTrumpSuit);
    }

    public interface CutthroatCallbacks {
        void onStockpileCardSelected(int cardIndex);
    }

    public interface FinalCardSelectionCallbacks {
        void onConfirmCardSelection();
    }

    public PinochleUIManager(Pinochle game) {
        this.game = game;
    }

    // LAYOUT SETUP
    public void setupHandLayouts(Hand[] hands, int handWidth, int nbPlayers) {
        RowLayout[] layouts = new RowLayout[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            layouts[i] = new RowLayout(handLocations[i], handWidth);
            layouts[i].setRotationAngle(180 * i);
            hands[i].setView(game, layouts[i]);
            hands[i].setTargetArea(new TargetArea(playingLocation));
            hands[i].draw();
        }
    }

    public void setupTrickHandLayouts(Hand[] trickWinningHands, int handWidth, int nbPlayers) {
        RowLayout[] trickHandLayouts = new RowLayout[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            trickHandLayouts[i] = new RowLayout(trickHandLocations[i], handWidth);
            trickHandLayouts[i].setRotationAngle(90 + 180 * i);
            trickWinningHands[i].setView(game, trickHandLayouts[i]);
            trickWinningHands[i].draw();
        }
    }

    public void setupPlayingArea(Hand playingArea, int trickWidth) {
        playingArea.setView(game, new RowLayout(playingLocation, (playingArea.getNumberOfCards() + 3) * trickWidth));
        playingArea.draw();
    }

    public void redrawHand(Hand hand, int playerIndex, int handWidth) {
        if (hand == null) {
            return;
        }
        try {
            hand.sort(Hand.SortType.SUITPRIORITY, false);
            RowLayout layout = new RowLayout(handLocations[playerIndex], handWidth);
            layout.setRotationAngle(180 * playerIndex);
            hand.setView(game, layout);
            hand.draw();
            game.refresh();
        } catch (Exception e) {
            System.err.println("Error redrawing hand for player " + playerIndex + ": " + e.getMessage());
        }
    }

    public void setupExpandedHandLayout(Hand hand, int playerIndex, int handWidth) {
        // Calculate maximum available width
        int gameWidth = 700;
        int margin = 20;
        int maxAvailableWidth = gameWidth - (2 * margin);
        int expandedWidth = Math.min(handWidth * 2, maxAvailableWidth);

        // Center the layout
        Location centeredLocation = new Location(
                gameWidth / 2,
                handLocations[playerIndex].y
        );

        RowLayout expandedLayout = new RowLayout(centeredLocation, expandedWidth);
        expandedLayout.setRotationAngle(180 * playerIndex);
        hand.setView(game, expandedLayout);
        hand.draw();
    }

    // BIDDING UI
    public void initBiddingUI(BiddingCallbacks callbacks) {
        // Initialize bid selection button
        game.addActor(bidSelectionActor, bidSelectionLocation);
        bidSelectionActor.addButtonListener(new GGButtonListener() {
            @Override
            public void buttonPressed(GGButton ggButton) {
                callbacks.onBidSelection();
            }
            @Override
            public void buttonReleased(GGButton ggButton) {}
            @Override
            public void buttonClicked(GGButton ggButton) {}
        });

        // Initialize bid confirm button
        game.addActor(bidConfirmActor, bidConfirmLocation);
        bidConfirmActor.addButtonListener(new GGButtonListener() {
            @Override
            public void buttonPressed(GGButton ggButton) {
                callbacks.onBidConfirm();
            }
            @Override
            public void buttonReleased(GGButton ggButton) {}
            @Override
            public void buttonClicked(GGButton ggButton) {}
        });

        // Initialize bid pass button
        game.addActor(bidPassActor, bidPassLocation);
        bidPassActor.addButtonListener(new GGButtonListener() {
            @Override
            public void buttonPressed(GGButton ggButton) {
                callbacks.onBidPass();
            }
            @Override
            public void buttonReleased(GGButton ggButton) {}
            @Override
            public void buttonClicked(GGButton ggButton) {}
        });

        // Initialize text displays
        updateBidDisplay(-1, 0, 0); // Initialize with default values
    }

    public void removeBiddingUI() {
        game.removeActor(bidSelectionActor);
        game.removeActor(bidConfirmActor);
        game.removeActor(bidPassActor);

        if (playerBidActor != null) game.removeActor(playerBidActor);
//        if (currentBidActor != null) game.removeActor(currentBidActor);
        if (newBidActor != null) game.removeActor(newBidActor);
    }

    public void setBiddingButtonsEnabled(boolean enabled) {
        bidSelectionActor.setActEnabled(enabled);
        bidConfirmActor.setActEnabled(enabled);
        bidPassActor.setActEnabled(enabled);
    }

    public void updateBidDisplay(int playerIndex, int currentBid, int newBid) {
        String playerBidString = "";
        switch (playerIndex) {
            case -1:
                playerBidString = "Bid";
                break;
            case 0:
                playerBidString = "Computer Bid";
                break;
            case 1:
                playerBidString = "Human Bid";
                break;
        }

        // Remove existing actors if they exist
        if (playerBidActor != null) game.removeActor(playerBidActor);
        if (currentBidActor != null) game.removeActor(currentBidActor);
        if (newBidActor != null) game.removeActor(newBidActor);

        // Create and add new actors
        if (playerIndex >= 0) {
            playerBidActor = new TextActor(playerBidString, Color.WHITE, game.bgColor, smallFont);
            game.addActor(playerBidActor, playerBidLocation);
        }

        currentBidActor = new TextActor("Current Bid: " + currentBid, Color.WHITE, game.bgColor, smallFont);
        game.addActor(currentBidActor, currentBidLocation);

        newBidActor = new TextActor("New Bid: " + newBid, Color.WHITE, game.bgColor, smallFont);
        game.addActor(newBidActor, newBidLocation);
    }

    public void updateBidResult(int winnerIndex, int finalBid, int computerPlayerIndex) {
        // Remove existing bid displays
        if (playerBidActor != null) game.removeActor(playerBidActor);
        if (currentBidActor != null) game.removeActor(currentBidActor);
        if (newBidActor != null) game.removeActor(newBidActor);

        currentBidActor = new TextActor("Current Bid: " + finalBid, Color.WHITE, game.bgColor, smallFont);
        game.addActor(currentBidActor, currentBidLocation);

        // Show winner result
        String playerBidString = winnerIndex == computerPlayerIndex ? "Computer Win" : "Human Win";
        TextActor playerBidActor = new TextActor(playerBidString, Color.WHITE, game.bgColor, smallFont);
        game.addActor(playerBidActor, playerBidLocation);
    }

    // TRUMP SELECTION
    public void updateTrumpActor(String trumpSuit) {
        if (trumpActor != null) {
            game.removeActor(trumpActor);
        }

        String trumpImage = game.trumpImages.get(trumpSuit);
        trumpActor = new Actor(trumpImage);
        game.addActor(trumpActor, trumpLocation);

        trumpInstructionActor = new TextActor("Trump Selection", Color.white, game.bgColor, smallFont);
        game.addActor(trumpInstructionActor, trumpInstructionLocation);
    }

    public void initTrumpSelectionUI(TrumpSelectionCallbacks callbacks) {
        trumpInstructionActor = new TextActor("Trump Selection", Color.white, game.bgColor, smallFont);
        game.addActor(trumpInstructionActor, trumpInstructionLocation);

        // Club trump button
        game.addActor(clubTrumpActor, clubTrumpLocation);
        clubTrumpActor.addButtonListener(new GGButtonListener() {
            @Override
            public void buttonPressed(GGButton ggButton) {
                callbacks.onTrumpSelected(Suit.CLUBS.getSuitShortHand());
            }
            @Override
            public void buttonReleased(GGButton ggButton) {}
            @Override
            public void buttonClicked(GGButton ggButton) {}
        });

        // Spade trump button
        game.addActor(spadeTrumpActor, spadeTrumpLocation);
        spadeTrumpActor.addButtonListener(new GGButtonListener() {
            @Override
            public void buttonPressed(GGButton ggButton) {
                callbacks.onTrumpSelected(Suit.SPADES.getSuitShortHand());
            }
            @Override
            public void buttonReleased(GGButton ggButton) {}
            @Override
            public void buttonClicked(GGButton ggButton) {}
        });

        // Diamond trump button
        game.addActor(diamondTrumpActor, diamondTrumpLocation);
        diamondTrumpActor.addButtonListener(new GGButtonListener() {
            @Override
            public void buttonPressed(GGButton ggButton) {
                callbacks.onTrumpSelected(Suit.DIAMONDS.getSuitShortHand());
            }
            @Override
            public void buttonReleased(GGButton ggButton) {}
            @Override
            public void buttonClicked(GGButton ggButton) {}
        });

        // Heart trump button
        game.addActor(heartTrumpActor, heartTrumpLocation);
        heartTrumpActor.addButtonListener(new GGButtonListener() {
            @Override
            public void buttonPressed(GGButton ggButton) {
                callbacks.onTrumpSelected(Suit.HEARTS.getSuitShortHand());
            }
            @Override
            public void buttonReleased(GGButton ggButton) {}
            @Override
            public void buttonClicked(GGButton ggButton) {}
        });
    }

    public void removeTrumpSelectionUI() {
        game.removeActor(clubTrumpActor);
        game.removeActor(spadeTrumpActor);
        game.removeActor(diamondTrumpActor);
        game.removeActor(heartTrumpActor);
    }

    // CUT THROAT MODE
    public void initCutthroatUI(CutthroatCallbacks callbacks) {
        cutthroatInstructionActor = new TextActor("Choose one card", Color.WHITE, game.bgColor, smallFont);

        int textWidth = cutthroatInstructionActor.getTextWidth();
        int textHeight = cutthroatInstructionActor.getTextHeight();
        Location centeredLocation = new Location(
                cutthroatInstructionLocation.x - textWidth / 2,
                cutthroatInstructionLocation.y - textHeight / 2
        );

        game.addActor(cutthroatInstructionActor, centeredLocation);

        // Create buttons for each stockpile card
        for (int i = 0; i < 2; i++) {
            final int cardIndex = i;
            stockpileCardButtons[i] = new GGButton("sprites/done30.gif", true);
            game.addActor(stockpileCardButtons[i], stockpileCardLocations[i]);

            stockpileCardButtons[i].addButtonListener(new GGButtonListener() {
                @Override
                public void buttonPressed(GGButton ggButton) {
                    callbacks.onStockpileCardSelected(cardIndex);
                }
                @Override
                public void buttonReleased(GGButton ggButton) {}
                @Override
                public void buttonClicked(GGButton ggButton) {}
            });
        }
    }

    public void removeCutthroatUI() {
        if (cutthroatInstructionActor != null) {
            game.removeActor(cutthroatInstructionActor);
            cutthroatInstructionActor = null;
        }

        for (int i = 0; i < stockpileCardButtons.length; i++) {
            if (stockpileCardButtons[i] != null) {
                game.removeActor(stockpileCardButtons[i]);
                stockpileCardButtons[i] = null;
            }
        }
        game.refresh();
    }

    public void setupStockpileDisplay(Hand[] faceUpCards) {
        // Set up face-up cards display
        for (int i = 0; i < 2; i++) {
            RowLayout layout = new RowLayout(stockpileCardLocations[i], 0);
            faceUpCards[i].setView(game, layout);
            faceUpCards[i].draw();
        }
    }

    // FINAL CARD SELECTION
    public void initFinalCardSelectionUI(FinalCardSelectionCallbacks callbacks) {
        cardSelectionInstructionActor = new TextActor("Select 12 cards to keep", Color.WHITE, game.bgColor, smallFont);

        int textWidth = cardSelectionInstructionActor.getTextWidth();
        int textHeight = cardSelectionInstructionActor.getTextHeight();
        Location centeredLocation = new Location(
                cardSelectionInstructionLocation.x - textWidth / 2,
                cardSelectionInstructionLocation.y - textHeight / 2
        );

        game.addActor(cardSelectionInstructionActor, centeredLocation);

        confirmCardSelectionButton = new GGButton("sprites/done30.gif", false);
        game.addActor(confirmCardSelectionButton, confirmCardSelectionLocation);
        confirmCardSelectionButton.setActEnabled(false);

        confirmCardSelectionButton.addButtonListener(new GGButtonListener() {
            @Override
            public void buttonPressed(GGButton ggButton) {
                callbacks.onConfirmCardSelection();
            }
            @Override
            public void buttonReleased(GGButton ggButton) {}
            @Override
            public void buttonClicked(GGButton ggButton) {}
        });
    }

    public void removeFinalCardSelectionUI() {
        if (cardSelectionInstructionActor != null) {
            game.removeActor(cardSelectionInstructionActor);
            cardSelectionInstructionActor = null;
        }
        if (confirmCardSelectionButton != null) {
            game.removeActor(confirmCardSelectionButton);
            confirmCardSelectionButton = null;
        }
        game.refresh();
    }

    public void setFinalCardSelectionButtonEnabled(boolean enabled) {
        if (confirmCardSelectionButton != null) {
            confirmCardSelectionButton.setActEnabled(enabled);
        }
    }

    // SCORE DISPLAY
    public void initScore(int[] scores, int nbPlayers) {
        for (int i = 0; i < nbPlayers; i++) {
            updateScoreDisplay(i, scores[i], scoreLocations[i], scoreActors);
        }
    }

    public void updateScoreDisplay(int playerIndex, int score, Location location, TextActor[] scoreActors) {
        if (scoreActors[playerIndex] != null) {
            game.removeActor(scoreActors[playerIndex]);
        }

        int displayScore = Math.max(score, 0);
        String text = "P" + playerIndex + "[" + displayScore + "]";
        scoreActors[playerIndex] = new TextActor(text, Color.WHITE, game.bgColor, bigFont);
        game.addActor(scoreActors[playerIndex], location);
    }

    public void updateScore(int player, int[] scores) {
        if (scores == null || player < 0 || player >= scores.length) {
            return;
        }

        updateScoreDisplay(player, scores[player], scoreLocations[player], scoreActors);
    }

    // GAME OVER
    public void showGameOverUI(String winText) {
        game.addActor(new Actor("sprites/gameover.gif"), textLocation);
        game.setStatusText(winText);
        game.refresh();
    }


    public void cleanup() {
        removeBiddingUI();
        removeTrumpSelectionUI();
        removeCutthroatUI();
        removeFinalCardSelectionUI();
    }
}