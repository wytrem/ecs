package net.wytrem.ecs;

import com.google.inject.*;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.google.inject.util.Types;
import net.wytrem.ecs.utils.GenericTypeClassListener;

import javax.inject.Singleton;
import java.util.*;
import java.util.function.Consumer;

/**
 * ECS central class. Manages mappers, game states and entity ids. Standard use:
 * <code>
 *  WorldConfiguration configuration = new WorldConfiguration();
 *  configuration.addModule(...);
 *
 *  World world = new World(configuration);
 *  world.initialize();
 *  world.push(MyGameState.class);
 *
 *  // in game loop
 *  world.process(delta);
 * </code>
 */
public class World {

    private float delta;

    private final WorldConfiguration configuration;

    private int nextEntityId;
    private Map<Class<? extends Component>, Mapper<? extends Component>> mappers;

    private Stack<GameState> stateStack;
    private Injector injector;

    public World(final WorldConfiguration configuration) {
        this.mappers = new HashMap<>();
        this.configuration = configuration;
        this.stateStack = new Stack<>();
    }

    /**
     * Creates this {@link World}'s injector based on the {@link WorldConfiguration}.
     */
    public void initialize() {
        List<Module> modules = new ArrayList<>();
        modules.addAll(this.configuration.extraModules);
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

    /**
     * @return The top of the game state stack
     */
    public GameState current() {
        return this.stateStack.peek();
    }

    /**
     * Pushes a {@link GameState} to the top of the stack
     */
    public void push(Class<? extends GameState> clazz) {
        if (!clazz.isAnnotationPresent(Singleton.class)) {
            throw new IllegalArgumentException("The given GameState " + clazz.getName() + " should be annotated with javax.inject.@Singleton");
        }
        else {
            if (!this.stateStack.isEmpty()) {
                this.stateStack.peek().pause();
            }

            GameState pushed = this.injector.getInstance(clazz);
            pushed.checkAndInitialize();
            this.stateStack.push(pushed);
            pushed.pushed();
        }
    }

    /**
     * Pops the current {@link GameState} from the stack.
     * @return The popped state
     */
    public GameState pop() {
        GameState popped = this.stateStack.pop();
        popped.poped();

        this.stateStack.peek().resume();

        return popped;
    }

    /**
     * Disposes every {@link GameState}.
     */
    public void dispose() {
        this.stateStack.forEach(GameState::dispose);
    }

    /**
     * Creates and register a new entity to be used in this world.
     * @return The new entity ID
     */
    public int createEntity() {
        return nextEntityId++;
    }

    /**
     * Removes the given entity id from this world.
     */
    public void deleteEntity(int entity) {
        this.mappers.values().forEach(mapper -> mapper.unset(entity));
    }

    /**
     * Process the current game state. Note this requires the current state to be non-null.
     *
     * @param delta time since last call
     */
    public void process(float delta) {
        this.delta = delta;
        this.stateStack.peek().process();
    }

    /**
     * @return Time since last tick
     */
    public float getDelta() {
        return delta;
    }

    private final Set<Consumer<Mapper<? extends Component>>> listenerSet = new HashSet<>();

    /**
     * Register a new mapper listener, to be notified when a new {@link Mapper} is created.
     */
    public void addMapperRegisterListener(Consumer<Mapper<? extends Component>> listener) {
        this.mappers.values().forEach(listener);
        this.listenerSet.add(listener);
    }

    /**
     * Inserts the given {@link Mapper} into the mapping.
     */
    private <C extends Component> void registerComponentMapper(Mapper<C> cMapper) {
        if (this.mappers.containsKey(cMapper.getComponentTypeClass())) {
            throw new IllegalArgumentException("Already registered component mapper for " + cMapper.getComponentTypeClass());
        }

        this.mappers.put(cMapper.getComponentTypeClass(), cMapper);
        this.listenerSet.forEach(listener -> listener.accept(cMapper));
    }

    /**
     * @return true if the given entity's components match the given {@link Aspect}
     */
    public boolean matches(int entity, Aspect aspect) {
        for (Class<? extends Component> clazz : aspect) {
            if (!this.has(entity, clazz)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the given entity has the given component type
     * @return true if the entity has the given component
     */
    public <C extends Component> boolean has(int entity, Class<C> clazz) {
        return this.getMapper(clazz).has(entity);
    }

    @SuppressWarnings("unchecked")
    /**
     * Retrieves the {@link Mapper} for the given component type.
     */
    public <C extends Component> Mapper<C> getMapper(Class<C> clazz) {
        if (!this.mappers.containsKey(clazz)) {
            return (Mapper<C>) this.injector.getInstance(Key.get(TypeLiteral.get(Types.newParameterizedType(Mapper.class, clazz))));
        }

        return (Mapper<C>) this.mappers.get(clazz);
    }

    void notifyAspectChanged(int entity) {
        GameState current = this.stateStack.peek();

        if (current != null) {
            current.notifyAspectChanged(entity);
        }
    }
}