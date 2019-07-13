package net.wytrem.ecs;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import net.wytrem.ecs.utils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class World {

    private float delta;
    private final List<BaseSystem> systems;
    private final List<IteratingSystem> iteratingSystems;

    private final WorldConfiguration configuration;

    private int nextEntityId;
    private Map<Class<? extends Component>, Mapper<? extends Component>> mappers;

    public World(final WorldConfiguration configuration) {
        this.systems = new ArrayList<>();
        this.iteratingSystems = new ArrayList<>();
        this.mappers = new HashMap<>();
        this.configuration = configuration;
    }

    public void initialize() {
        Injector injector = Guice.createInjector(this.createGuiceModule());
        for (Class<? extends BaseSystem> clazz : this.configuration.systemClasses) {
            BaseSystem sys = injector.getInstance(clazz);
            this.systems.add(sys);

            if (sys instanceof IteratingSystem) {
                this.iteratingSystems.add((IteratingSystem) sys);
            }
        }

        for (BaseSystem sys : this.systems) {
            sys.initialize();
        }
    }

    public void dispose() {
        for (BaseSystem sys : this.systems) {
            sys.dispose();
        }
    }

    private Module createGuiceModule() {

        return new AbstractModule() {
            @Override
            protected void configure() {
                bindListener(Matchers.any(), new GenericTypeClassListener());
                bind(World.class).toInstance(World.this);
                bindListener(Matchers.any(), new TypeListener() {
                    @Override
                    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
                        encounter.register((InjectionListener<I>) injectee -> {

                            if (injectee instanceof Mapper) {
                                Mapper mapper = (Mapper) injectee;
                                World.this.registerComponentMapper(mapper);
                            }
                        });
                    }
                });
            }
        };
    }

    public int createEntity() {
        return nextEntityId++;
    }

    public void process(float delta) {
        this.delta = delta;

        for (BaseSystem system : this.systems) {
            system.process();
        }
    }

    public float getDelta() {
        return delta;
    }

    private <C extends Component> void registerComponentMapper(Mapper<C> cMapper) {
        this.mappers.put(cMapper.getComponentTypeClass(), cMapper);
    }

    public boolean matches(int entity, Aspect aspect) {
        for (Class<? extends Component> clazz : aspect) {
            if (!this.has(entity, clazz)) {
                return false;
            }
        }

        return true;
    }

    public <C extends Component> boolean has(int entity, Class<C> clazz) {
        return this.getMapper(clazz).has(entity);
    }

    public <C extends Component> Mapper<C> getMapper(Class<C> clazz) {
        return (Mapper<C>) this.mappers.get(clazz);
    }

    public void notifyAspectChanged(int entity) {
        for (IteratingSystem iteratingSystem : this.iteratingSystems) {
            iteratingSystem.notifyAspectChanged(entity);
        }
    }
}