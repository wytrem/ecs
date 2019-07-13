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
import net.wytrem.ecs.utils.GenericTypeClassListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class World {

    private float delta;
    private final List<BaseSystem> allSystems;
    private final List<BaseSystem> processingSystems;
    private final List<IteratingSystem> iteratingSystems;

    private final WorldConfiguration configuration;

    private int nextEntityId;
    private Map<Class<? extends Component>, Mapper<? extends Component>> mappers;

    private Stack<GameState> stateStack;
    private Injector injector;

    public World(final WorldConfiguration configuration) {
        this.allSystems = new ArrayList<>();
        this.iteratingSystems = new ArrayList<>();
        this.processingSystems = new ArrayList<>();
        this.mappers = new HashMap<>();
        this.configuration = configuration;
        this.stateStack = new Stack<>();
    }

    public void initialize() {
        List<Module> modules = new ArrayList<>();
        modules.addAll(this.configuration.getExtraModules());
        modules.add(new AbstractModule() {
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
        });

        this.injector = Guice.createInjector(modules);
    }

    public GameState current() {
        return this.stateStack.peek();
    }

    public void push(Class<? extends GameState> clazz) {

        if (!this.stateStack.isEmpty()) {
            this.stateStack.peek().pause();
        }

        GameState pushed = this.injector.getInstance(clazz);
        pushed.checkAndInitialize();
        pushed.pushed();
        this.stateStack.push(pushed);
    }

    public GameState pop() {
        GameState poped = this.stateStack.pop();
        poped.poped();

        this.stateStack.peek().resume();

        return poped;
    }

    public void dispose() {
        this.stateStack.forEach(GameState::dispose);
    }

    public int createEntity() {
        return nextEntityId++;
    }

    public void process(float delta) {
        this.stateStack.peek().process();
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
        GameState current = this.stateStack.peek();

        if (current != null) {
            current.notifyAspectChanged(entity);
        }
    }
}