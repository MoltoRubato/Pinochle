import java.util.List;
public abstract class MeldFactory {
    public abstract List<Meld> getMelds(String trumpSuit);

    public List<Meld> createMelds(String trumpSuit) {
        List<Meld> allMeld = getMelds(trumpSuit);
        allMeld.sort((m1, m2) -> Integer.compare(m2.getScore(), m1.getScore()));
        return allMeld;
    }
}