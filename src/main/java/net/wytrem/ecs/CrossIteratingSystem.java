package net.wytrem.ecs;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

/**
 * A system that operates on pairs of entities with specific aspects.
 */
public abstract class CrossIteratingSystem extends IteratingSystem {
    private final Aspect secondAspect;
    private final IntList secondAspectEntities;

    public CrossIteratingSystem(Aspect both) {
        this(both, both);
    }

    public CrossIteratingSystem(Aspect first, Aspect second) {
        super(first);
        this.secondAspect = second;
        this.secondAspectEntities = new IntArrayList();
    }

    @Override
    public final void process(int entity) {
        for (int i = 0; i < this.secondAspectEntities.size(); i++) {
            this.process(entity, this.secondAspectEntities.getInt(i));
        }
    }

    @Override
    public void notifyAspectChanged(int entity) {
        super.notifyAspectChanged(entity);

        if (this.secondAspectEntities.contains(entity)) {
            if (!this.world.matches(entity, this.secondAspect)) {
                this.secondAspectEntities.rem(entity);
            }
        } else {
            if (this.world.matches(entity, this.secondAspect)) {
                this.secondAspectEntities.add(entity);
            }
        }
    }

    public abstract void process(int first, int second);
}
