package net.wytrem.ecs;

import javax.inject.Inject;

public abstract class BaseSystem {
    @Inject
    protected World world;

    private boolean init = false;

    final void checkAndInit() {
        if (!init) {
            this.initialize();
            init = true;
        }
    }

    public void initialize() {

    }

    public void begin() {

    }

    public void process() {

    }

    public void end() {

    }

    public void dispose() {

    }

    public boolean isEnabled() {
        return true;
    }
}
