import ch.aplu.jcardgame.Hand;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class responsible for bidding logic
 */
public class BiddingManager {
    private final Pinochle gameContext;

    // Bidding state variables
    private int currentBid = 0;
    private boolean isFirstBid = true;
    private boolean hasHumanBid = false;
    private int humanBid = 0;
    private boolean hasComputerPassed = false;
    private boolean hasHumanPassed = false;
    private int bidWinPlayerIndex = 0;

    // Auto-bidding lists
    private final List<Integer> computerAutoBids = new ArrayList<>();
    private final List<Integer> humanAutoBids = new ArrayList<>();
    private int computerAutoBidIndex = 0;
    private int humanAutoBidIndex = 0;

    // Bidding strategies
    private final BiddingStrategy computerBiddingStrategy;

    // Constants
    public static final int BID_SELECTION_VALUE = 10;
    public static final int MAX_SINGLE_BID = 20;

    public static final String RANDOM_BID = "random";
    public static final String COMPUTER_BID = "computer";
    public static final String HUMAN_BID = "human";

    public BiddingManager(Pinochle gameContext, BiddingStrategy computerBiddingStrategy) {
        this.gameContext = gameContext;
        this.computerBiddingStrategy = computerBiddingStrategy;
    }


    public void performBidding(Hand[] hands) {
        initBids();
        gameContext.uiManager.setBiddingButtonsEnabled(false);
        String bidOrder = gameContext.config.getBidOrder();
        List<Integer> player0Bids = gameContext.config.getPlayer0AutoBids();
        List<Integer> player1Bids = gameContext.config.getPlayer1AutoBids();

        computerAutoBids.clear();
        computerAutoBids.addAll(player0Bids);
        humanAutoBids.clear();
        humanAutoBids.addAll(player1Bids);

        boolean isContinueBidding = true;
        gameContext.uiManager.updateBidDisplay(-1, currentBid, 0);
        Random rand = new Random(1);
        int playerIndex = switch (bidOrder) {
            case RANDOM_BID -> rand.nextInt(gameContext.getNbPlayers());
            case HUMAN_BID -> GameConfig.HUMAN_PLAYER_INDEX;
            default -> GameConfig.COMPUTER_PLAYER_INDEX;
        };

        do {
            for (int i = 0; i < gameContext.getNbPlayers(); i++) {
                askForBidForPlayerIndex(playerIndex, hands);
                playerIndex = (playerIndex + 1) % gameContext.getNbPlayers();
                isContinueBidding = !hasHumanPassed && !hasComputerPassed;
                if (!isContinueBidding) {
                    bidWinPlayerIndex = playerIndex;
                    break;
                }
            }
        } while (isContinueBidding);

        gameContext.uiManager.removeBiddingUI();
        gameContext.uiManager.updateBidResult(bidWinPlayerIndex, currentBid, GameConfig.COMPUTER_PLAYER_INDEX);
        gameContext.gameLogger.addBidInfoToLog(bidWinPlayerIndex, currentBid);
    }

    private void initBids() {
        System.out.println("init bids");
        gameContext.uiManager.initBiddingUI(new PinochleUIManager.BiddingCallbacks() {
            @Override
            public void onBidSelection() {
                hasHumanBid = false;
                if (humanBid >= MAX_SINGLE_BID) {
                    gameContext.uiManager.setBiddingButtonsEnabled(false);
                    gameContext.setStatus("Maximum amount of a single bid reached");
                } else {
                    humanBid += BID_SELECTION_VALUE;
                }
                gameContext.uiManager.updateBidDisplay(GameConfig.HUMAN_PLAYER_INDEX, currentBid, humanBid + currentBid);
            }

            @Override
            public void onBidConfirm() {
                if(humanBid == 0) return;
                currentBid = currentBid + humanBid;
                hasHumanBid = true;
                humanBid = 0;
                gameContext.uiManager.updateBidDisplay(GameConfig.HUMAN_PLAYER_INDEX, currentBid, currentBid);
                gameContext.setStatus("");
            }

            @Override
            public void onBidPass() {
                gameContext.uiManager.updateBidDisplay(GameConfig.HUMAN_PLAYER_INDEX, currentBid, 0);
                humanBid = 0;
                hasHumanPassed = true;
                gameContext.setStatus("");
            }
        });
    }

    private void askForBidForPlayerIndex(int playerIndex, Hand[] hands) {
        if (playerIndex == GameConfig.COMPUTER_PLAYER_INDEX) {
            int bidValue;
            if (gameContext.config.isAuto() && computerAutoBids != null && computerAutoBidIndex < computerAutoBids.size()) {
                bidValue = computerAutoBids.get(computerAutoBidIndex);
                computerAutoBidIndex++;
            } else {
                bidValue = computerBiddingStrategy.calculateBid(
                        hands[GameConfig.COMPUTER_PLAYER_INDEX],
                        currentBid,
                        isFirstBid
                );
            }
            gameContext.uiManager.updateBidDisplay(playerIndex, currentBid, currentBid + bidValue);
            delay(gameContext.config.getThinkingTime());
            if (bidValue == 0) {
                hasComputerPassed = true;
                hasHumanBid = false;
                return;
            }
            currentBid += bidValue;
            gameContext.uiManager.updateBidDisplay(playerIndex, currentBid, 0);
            hasHumanBid = false;
        } else {
            gameContext.uiManager.setBiddingButtonsEnabled(true);
            gameContext.uiManager.updateBidDisplay(playerIndex, currentBid, 0);
            if (gameContext.config.isAuto() && humanAutoBids != null && humanAutoBidIndex < humanAutoBids.size()) {
                humanBid = humanAutoBids.get(humanAutoBidIndex);
                currentBid = currentBid + humanBid;
                humanAutoBidIndex++;
                if (humanBid == 0) {
                    hasHumanPassed = true;
                }
                gameContext.uiManager.updateBidDisplay(GameConfig.HUMAN_PLAYER_INDEX, currentBid, currentBid);
            } else {
                while (!hasHumanBid && !hasHumanPassed) delay(gameContext.config.getDelayTime());
            }
            hasHumanBid = true;
        }
        // After each bid, it is no longer the first bid
        isFirstBid = false;
    }

    private void delay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Getters for bidding results
    public int getCurrentBid() {
        return currentBid;
    }

    public int getBidWinPlayerIndex() {
        return bidWinPlayerIndex;
    }

    public BiddingStrategy getComputerBiddingStrategy() {
        return computerBiddingStrategy;
    }
}