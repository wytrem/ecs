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
public final class Mapper<C extends Component> {

    private Int2ObjectMap<C> mapping = new Int2ObjectOpenHashMap<C>();

    @ComponentTypeClass
    private Class<C> componentTypeClass;

    @Inject
    private World world;

    private Set<ChangeListener<? super C>> changeListeners = new HashSet<>();

    public Class<C> getComponentTypeClass() {
        return componentTypeClass;
    }

    void setComponentTypeClass(Class<C> componentTypeClass) {
        this.componentTypeClass = componentTypeClass;
    }

    @Inject
    Injector injector;

    public void addListener(Class<? extends ChangeListener<? super C>> listenerClass) {
        this.changeListeners.add(injector.getInstance(listenerClass));
    }

    public void addListener(ChangeListener<? super C> listener) {
        this.changeListeners.add(listener);
    }

    public void removeListener(ChangeListener<? super C> listener) {
        this.changeListeners.remove(listener);
    }

    public void set(int entity, C value) {
        boolean contained = this.mapping.containsKey(entity);
        C old = this.mapping.put(entity, value);
        this.changeListeners.forEach(listener -> {listener.onSet(entity, old, value);});

        if (!contained) {
            world.notifyAspectChanged(entity);
        }
    }

    public C unset(int entity) {
        boolean contained = this.mapping.containsKey(entity);

        C value = this.mapping.remove(entity);
        this.changeListeners.forEach(listener -> {listener.onUnset(entity, value);});
        if (contained) {
            world.notifyAspectChanged(entity);
        }
        return value;
    }

    public C get(int entity) {
        return this.mapping.get(entity);
    }

    public Optional<C> getOptional(int entity) {
        return this.mapping.containsKey(entity) ? Optional.of(this.get(entity)) : Optional.empty();
    }

    public boolean has(int entity) {
        return this.mapping.containsKey(entity);
    }

    public void forEach(BiConsumer<? super Integer, ? super C> biConsumer) {
        this.mapping.forEach(biConsumer);
    }

    public void forEachValue(Consumer<? super C> consumer) {
        this.mapping.values().forEach(consumer);
    }

    public void forEachKey(IntConsumer consumer) {
        this.mapping.keySet().forEach(consumer);
    }

    public interface ChangeListener<C extends Component> {
        void onSet(int entity, C oldValue, C newValue);

        void onUnset(int entity, C oldValue);
    }
}
