package net.wytrem.ecs;

import javax.inject.Inject;

public abstract class BaseSystem {
    protected boolean enabled;

    @Inject
    protected World world;

    public void initialize() {

    }

    public void process() {

    }

    public void dispose() {

    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
