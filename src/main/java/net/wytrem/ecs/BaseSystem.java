package net.wytrem.ecs;

import javax.inject.Inject;

/**
 * The root class for the ECS system.
 */
public abstract class BaseSystem {
    @Inject
    protected World world;

    private boolean init = false;

    /**
     * Just in case a singleton system is registered to several game states, we only initialize it once.
     */
    final void checkAndInit() {
        if (!init) {
            this.initialize();
            init = true;
        }
    }

    /**
     * Called only once, when this system is instantiated and registered to a game state.
     * Note that if this system is registered to multiple game states, this method is called only when the first
     * game state is pushed.
     */
    public void initialize() {

    }

    /**
     * Called before process.
     */
    public void begin() {

    }

    /**
     * Called every tick.
     */
    public void process() {

    }

    /**
     * Called after process.
     */
    public void end() {

    }

    /**
     * Should free sub resources.
     */
    public void dispose() {

    }

    /**
     * @return if {@link BaseSystem#process()} should be called on this system.
     */
    public boolean isEnabled() {
        return true;
    }
}
