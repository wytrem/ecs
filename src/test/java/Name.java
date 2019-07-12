import net.wytrem.ecs.*;

public class Name implements Component {
    private String first;

    public Name(String first, String last) {
        this.first = first;
        this.last = last;
    }

    private String last;

    public String getFirst() {
        return first;
    }

    public String getLast() {
        return last;
    }
}
