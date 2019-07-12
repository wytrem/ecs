package net.wytrem.ecs;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class IteratingSystem extends BaseSystem {

    private final Aspect aspect;
    private final IntList entities;

    public IteratingSystem(Aspect aspect) {
        this.aspect = aspect;
        this.entities = new IntArrayList();
    }

    @Override
    public final void process() {
        for (int i = 0; i < this.entities.size(); i++) {
            this.process(this.entities.getInt(i));
        }
    }

    public void process(int entity) {

    }

    public final void notifyAspectChanged(int entity) {
        if (this.entities.contains(entity)) {
            if (!this.world.matches(entity, this.aspect)) {
                this.entities.rem(entity);
            }
        } else {
            if (this.world.matches(entity, this.aspect)) {
                this.entities.add(entity);
            }
        }
    }
}
