package net.wytrem.ecs;

import com.google.inject.Injector;
import net.wytrem.ecs.utils.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * A collections a {@link BaseSystem}, which represents a state of the game (loading, main menu, ingame...).
 */
public abstract class GameState {

    private final List<Class<? extends BaseSystem>> systemClasses;
    private SystemSet systemSet;

    @Inject
    Injector injector;

    protected GameState() {
        this.systemClasses = new ArrayList<>();
    }

    /**
     * Register the given system class to be part of this system.
     * @param clazz
     */
    public void register(Class<? extends BaseSystem> clazz) {
        if (!clazz.isAnnotationPresent(Singleton.class)) {
            throw new IllegalArgumentException("The registered system " + clazz.getName() + " should be annotated with javax.inject.@Singleton");
        }
        else {
            this.systemClasses.add(clazz);
        }
    }

    /**
     * Instantiate each system using injector.
     */
    void checkAndInitialize() {
        if (this.systemSet == null) {
            this.systemSet = SystemSet.fromClasses(systemClasses, injector);
            this.systemSet.allSystems().forEach(BaseSystem::checkAndInit);
        }
    }

    /**
     * Called when popped from the game states stack.
     * @see World#pop()
     */
    public void poped() {

    }

    /**
     * Called when pushed to the game states stack.
     * @see World#push(Class)
     */
    public void pushed() {

    }

    /**
     * Called when another state is pushed to the stack (this one is not anymore the top).
     */
    public void pause() {

    }

    /**
     * Called when this state comes back to the top of the stack.
     */
    public void resume() {

    }

    /**
     * @return Attached systems.
     */
    public SystemSet systems() {
        return this.systemSet;
    }

    /**
     * Processes all the systems.
     */
    public void process() {
        for (BaseSystem baseSystem : this.systemSet.processingSystems()) {
            if (baseSystem.isEnabled()) {
                baseSystem.begin();
                baseSystem.process();
                baseSystem.end();
            }
        }
    }

    /**
     * Disposes every {@link BaseSystem}.
     */
    public void dispose() {
        this.systemSet.allSystems().forEach(BaseSystem::dispose);
    }

    /**
     * Called when an entity aspect changed. Delegates to {@link IteratingSystem}.
     * @see IteratingSystem#notifyAspectChanged(int)
     */
    void notifyAspectChanged(int entity) {
        for (IteratingSystem iteratingSystem : this.systemSet.iteratingSystems()) {
            iteratingSystem.notifyAspectChanged(entity);
        }
    }
}
