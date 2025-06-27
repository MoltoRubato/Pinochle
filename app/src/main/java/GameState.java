public abstract class GameState {
    protected Pinochle gameContext;

    public GameState(Pinochle gameContext) {
        this.gameContext = gameContext;
    }

    public abstract void handle();
    public abstract String getStateName();
}