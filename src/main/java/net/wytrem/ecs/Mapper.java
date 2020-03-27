package net.wytrem.ecs;

import com.google.inject.Injector;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.wytrem.ecs.utils.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

@Singleton
/**
 * A {@link Mapper} hold all the data corresponding to one {@link Component} type. It basically is a mapping from the
 * entity id to its (optional) value.
 */
public final class Mapper<C extends Component> {
    private Int2ObjectMap<C> mapping = new Int2ObjectOpenHashMap<C>();

    @InjectGenericTypeClass
    private Class<C> componentTypeClass;

    @Inject
    private World world;

    @Inject
    private Injector injector;

    private Set<ChangeListener<? super C>> changeListeners = new HashSet<>();

    public Class<C> getComponentTypeClass() {
        return componentTypeClass;
    }

    /**
     * Registers the given component change listener.
     *
     * @param listenerClass the class to be instantiated via the common {@link Injector}
     */
    public void addListener(Class<? extends ChangeListener<? super C>> listenerClass) {
        this.addListener(injector.getInstance(listenerClass));
    }

    /**
     * Registers the given component change listener.
     */
    public void addListener(ChangeListener<? super C> listener) {
        this.changeListeners.add(listener);
    }

    /**
     * Unegister the given component change listener.
     */
    public void removeListener(ChangeListener<? super C> listener) {
        this.changeListeners.remove(listener);
    }

    /**
     * Sets the given component value for the given entity.
     *
     * @return the previous value (can be null)
     */
    public C set(int entity, C value) {
        if (value == null) {
            return this.unset(entity);
        }
        else {
            boolean contained = this.mapping.containsKey(entity);
            C old = this.mapping.put(entity, value);
            this.changeListeners.forEach(listener -> {listener.onSet(entity, old, value);});

            if (!contained) {
                world.notifyAspectChanged(entity);
            }

            return old;
        }
    }

    /**
     * Removes the given entity from this mapping.
     *
     * @return The value stored for this entity or null
     */
    public C unset(int entity) {
        boolean contained = this.mapping.containsKey(entity);

        C value = this.mapping.remove(entity);
        this.changeListeners.forEach(listener -> {listener.onUnset(entity, value);});
        if (contained) {
            world.notifyAspectChanged(entity);
        }
        return value;
    }

    /**
     * @return The value for the given entity (can be null)
     */
    public C get(int entity) {
        return this.mapping.get(entity);
    }

    /**
     * @return The value for the given entity
     */
    public Optional<C> getOptional(int entity) {
        return this.mapping.containsKey(entity) ? Optional.of(this.get(entity)) : Optional.empty();
    }

    /**
     * @return true if this {@link Mapper} contains a value for the given entity
     */
    public boolean has(int entity) {
        return this.mapping.containsKey(entity);
    }

    /**
     * For each entity id/component pair.
     */
    public void forEach(BiConsumer<? super Integer, ? super C> biConsumer) {
        this.mapping.forEach(biConsumer);
    }

    /**
     * For each component stored in this mapper.
     */
    public void forEachValue(Consumer<? super C> consumer) {
        this.mapping.values().forEach(consumer);
    }

    /**
     * For each entity which has a component stored by this mapper.
     */
    public void forEachKey(IntConsumer consumer) {
        this.mapping.keySet().forEach(consumer);
    }

    /**
     * Listens to the value change inside a {@link Mapper}.
     */
    public interface ChangeListener<C extends Component> {
        /**
         * Called when a component value change.
         */
        void onSet(int entity, C oldValue, C newValue);

        /**
         * Called when an entity is removed from this {@link Mapper}.
         */
        void onUnset(int entity, C oldValue);
    }
}
