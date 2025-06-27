import java.util.*;

/**
 * Centralised configuration class that loads and provides access to all game properties
 */
public class GameConfig {
    private final Properties properties;

    static public final int seed = 30008;
    static final Random random = new Random(seed);

    // Game mode settings
    private final boolean isAuto;
    private final boolean isCutthroatMode;
    private final boolean useAdditionalMelds;
    private final boolean isSmartTrickMode;

    // Timing settings
    private final int thinkingTime;
    private final int delayTime;

    // Player settings
    static final int COMPUTER_PLAYER_INDEX = 0;
    static final int HUMAN_PLAYER_INDEX = 1;

    private final boolean isPlayer0SmartBidding;
    private final String trumpSuit;
    private final String bidOrder;

    // Player auto movements
    private final List<Integer> player0AutoBids;
    private final List<Integer> player1AutoBids;
    private final List<String> player0InitialCards;
    private final List<String> player1InitialCards;
    private final List<String> player0CardsPlayed;
    private final List<String> player1CardsPlayed;

    // Cut-throat mode specific
    private final List<String> player0ExtraCards;
    private final List<String> player1ExtraCards;
    private final List<String> player1FinalCards;

    public GameConfig(Properties properties) {
        this.properties = properties;

        // Load game mode settings
        this.isAuto = parseBoolean("isAuto", false);
        this.isCutthroatMode = parseBoolean("mode.cutthroat", false);
        this.useAdditionalMelds = parseBoolean("melds.additional", false);
        this.isSmartTrickMode = parseBoolean("mode.smarttrick", false);

        // Load timing settings
        this.thinkingTime = parseInt("thinkingTime", 2000);
        this.delayTime = parseInt("delayTime", 600);

        // Load player settings
        this.isPlayer0SmartBidding = parseBoolean("players.0.smartbids", false);
        this.trumpSuit = parseString("players.trump", "C");
        this.bidOrder = parseString("players.bid_first", "random");

        // Load player auto movements and cards
        this.player0AutoBids = parseIntegerList("players.0.bids");
        this.player1AutoBids = parseIntegerList("players.1.bids");
        this.player0InitialCards = parseStringList("players.0.initialcards");
        this.player1InitialCards = parseStringList("players.1.initialcards");
        this.player0CardsPlayed = parseStringList("players.0.cardsPlayed");
        this.player1CardsPlayed = parseStringList("players.1.cardsPlayed");

        // Load cut-throat mode specific settings
        this.player0ExtraCards = parseStringList("players.0.extra_cards");
        this.player1ExtraCards = parseStringList("players.1.extra_cards");
        this.player1FinalCards = parseStringList("players.1.final_cards");
    }

    // Utility methods for parsing properties
    private boolean parseBoolean(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    private int parseInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private String parseString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    private List<String> parseStringList(String key) {
        String value = properties.getProperty(key, "");
        if (value.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(value.split(",")));
    }

    private List<Integer> parseIntegerList(String key) {
        List<String> stringList = parseStringList(key);
        List<Integer> intList = new ArrayList<>();
        for (String str : stringList) {
            try {
                intList.add(Integer.parseInt(str.trim()));
            } catch (NumberFormatException e) {
                // Skip invalid numbers
            }
        }
        return intList;
    }

    // Getter methods
    public boolean isAuto() { return isAuto; }
    public boolean isCutthroatMode() { return isCutthroatMode; }
    public boolean useAdditionalMelds() { return useAdditionalMelds; }
    public boolean isSmartTrickMode() { return isSmartTrickMode; }

    public int getThinkingTime() { return thinkingTime; }
    public int getDelayTime() { return delayTime; }

    public boolean isPlayer0SmartBidding() { return isPlayer0SmartBidding; }
    public String getTrumpSuit() { return trumpSuit; }
    public String getBidOrder() { return bidOrder; }

    public List<Integer> getPlayer0AutoBids() { return new ArrayList<>(player0AutoBids); }
    public List<Integer> getPlayer1AutoBids() { return new ArrayList<>(player1AutoBids); }
    public List<String> getPlayer0InitialCards() { return new ArrayList<>(player0InitialCards); }
    public List<String> getPlayer1InitialCards() { return new ArrayList<>(player1InitialCards); }
    public List<String> getPlayer0CardsPlayed() { return new ArrayList<>(player0CardsPlayed); }
    public List<String> getPlayer1CardsPlayed() { return new ArrayList<>(player1CardsPlayed); }

    public List<String> getPlayer0ExtraCards() { return new ArrayList<>(player0ExtraCards); }
    public List<String> getPlayer1ExtraCards() { return new ArrayList<>(player1ExtraCards); }
    public List<String> getPlayer1FinalCards() { return new ArrayList<>(player1FinalCards); }

    // Convenience methods for accessing player-specific data
    public List<String> getPlayerInitialCards(int playerIndex) {
        return playerIndex == 0 ? getPlayer0InitialCards() : getPlayer1InitialCards();
    }

    public List<String> getPlayerCardsPlayed(int playerIndex) {
        return playerIndex == 0 ? getPlayer0CardsPlayed() : getPlayer1CardsPlayed();
    }

    public List<Integer> getPlayerAutoBids(int playerIndex) {
        return playerIndex == 0 ? getPlayer0AutoBids() : getPlayer1AutoBids();
    }

    public List<String> getPlayerExtraCards(int playerIndex) {
        return playerIndex == 0 ? getPlayer0ExtraCards() : getPlayer1ExtraCards();
    }
}