package net.wytrem.ecs;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a set of traits of an entity (a set of component <i>types</i>).
 */
public class Aspect implements Iterable<Class<? extends Component>> {
    private List<Class<? extends Component>> all;

    private Aspect(List<Class<? extends Component>> all) {
        this.all = all;
    }

    /**
     * @return an {@link Aspect} matching the given component types
     */
    public static Aspect all(Class<? extends Component>... classes) {
        return new Aspect(Arrays.asList(classes));
    }

    private static final Aspect ANY = new Aspect(Collections.emptyList());

    /**
     * @return an {@link Aspect} matching any entity
     */
    public static Aspect any() {
        return ANY;
    }

    @Override
    public Iterator<Class< ? extends Component>> iterator() {
        return all.iterator();
    }
}
