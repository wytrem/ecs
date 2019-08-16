package net.wytrem.ecs;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.wytrem.ecs.utils.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

@Singleton
public class Mapper<C extends Component> extends ParentMapper<C> {

    private Int2ObjectMap<C> mapping = new Int2ObjectOpenHashMap<C>();

    @ComponentTypeClass
    private Class<C> componentTypeClass;

    @Inject
    private World world;

    public Class<C> getComponentTypeClass() {
        return componentTypeClass;
    }

    public void set(int entity, C value) {
        boolean contained = this.mapping.containsKey(entity);
        this.mapping.put(entity, value);
        if (!contained) {
            world.notifyAspectChanged(entity);
        }
    }

    public C unset(int entity) {
        boolean contained = this.mapping.containsKey(entity);

        C value = this.mapping.remove(entity);
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
}
