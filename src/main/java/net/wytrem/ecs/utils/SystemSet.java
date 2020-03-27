package net.wytrem.ecs.utils;

import com.google.inject.Injector;
import net.wytrem.ecs.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of {@link BaseSystem}, divided in three sub-collections : all, processing systems (ie. those which need
 * ticking), and iterating systems (ie. those which need notification when entity aspect changed).
 */
public class SystemSet {
    private final List<BaseSystem> allSystems;
    private final List<BaseSystem> processingSystems;
    private final List<IteratingSystem> iteratingSystems;

    private SystemSet() {
        this.allSystems = new ArrayList<>();
        this.iteratingSystems = new ArrayList<>();
        this.processingSystems = new ArrayList<>();
    }

    public List<BaseSystem> allSystems() {
        return allSystems;
    }

    public List<BaseSystem> processingSystems() {
        return processingSystems;
    }

    public List<IteratingSystem> iteratingSystems() {
        return iteratingSystems;
    }

    public <S extends BaseSystem> S getSystem(Class<S> clazz) {
        for (BaseSystem sys : this.allSystems) {
            if (sys.getClass().equals(clazz)) {
                return (S) sys;
            }
        }
        return null;
    }

    /**
     * Instantiate each system class using the given injector.
     */
    public static SystemSet fromClasses(Iterable<Class<? extends BaseSystem>> classes, Injector injector) {
        SystemSet set = new SystemSet();

        for (Class<? extends BaseSystem> clazz : classes) {
            BaseSystem sys = injector.getInstance(clazz);
            set.allSystems.add(sys);

            if (sys instanceof IteratingSystem) {
                set.iteratingSystems.add((IteratingSystem) sys);
            }

            if (!(sys instanceof Service)) {
                set.processingSystems.add(sys);
            }
        }

        return set;
    }
}
