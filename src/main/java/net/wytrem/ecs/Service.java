package net.wytrem.ecs;

/**
 * A system which does not need ticking.
 */
public class Service extends BaseSystem {
    @Override
    public final void begin() {
        throw new IllegalStateException("The begin method of a Service is not to be called.");
    }

    @Override
    public final void process() {
        throw new IllegalStateException("The process method of a Service is not to be called.");
    }

    @Override
    public final void end() {
        throw new IllegalStateException("The end method of a Service is not to be called.");
    }
}
