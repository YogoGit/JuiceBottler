import java.util.ArrayList;
import java.util.List;

public class AssemblyLine {
    private final List<Orange> oranges;

    AssemblyLine() {
        oranges = new ArrayList<Orange>();
    }

    public void addOrange(Orange o) {
        oranges.add(o);
    }

    public Orange getOrange() {
        return oranges.get(0);
    }

    public int countOranges() {
        return oranges.size();
    }
}
