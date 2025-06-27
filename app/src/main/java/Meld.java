import java.util.List;

public class Meld {
    private final String name;
    private final List<String> cards;
    private final int score;

    public Meld(String name, List<String> cards, int score) {
        this.name = name;
        this.cards = cards;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public List<String> getCards() {
        return cards;
    }

    public int getScore() {
        return score;
    }
}
