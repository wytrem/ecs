package net.wytrem.ecs;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Aspect implements Iterable<Class<? extends Component>> {
    private List<Class<? extends Component>> all;

    public Aspect(List<Class<? extends Component>> all) {
        this.all = all;
    }

    public static Aspect all(Class<? extends Component>... classes) {
        return new Aspect(Arrays.asList(classes));
    }

    @Override
    public Iterator<Class< ? extends Component>> iterator() {
        return all.iterator();
    }
}
