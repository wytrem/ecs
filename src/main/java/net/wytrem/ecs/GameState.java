package net.wytrem.ecs;

import com.google.inject.Injector;
import net.wytrem.ecs.utils.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public abstract class GameState {

    private final List<Class<? extends BaseSystem>> systemClasses;
    private SystemSet systemSet;

    @Inject
    Injector injector;

    protected GameState() {
        this.systemClasses = new ArrayList<>();
    }

    public void register(Class<? extends BaseSystem> clazz) {
        this.systemClasses.add(clazz);
    }

    void checkAndInitialize() {
        if (this.systemSet == null) {
            this.systemSet = SystemSet.fromClasses(systemClasses, injector);
            this.systemSet.allSystems().forEach(BaseSystem::checkAndInit);
        }
    }

    public void poped() {

    }

    public void pushed() {

    }

    public void pause() {

    }

    public void resume() {

    }

    public SystemSet systems() {
        return this.systemSet;
    }

    public void process() {
        for (BaseSystem baseSystem : this.systemSet.processingSystems()) {
            if (baseSystem.isEnabled()) {
                baseSystem.begin();
                baseSystem.process();
                baseSystem.end();
            }
        }
    }

    public void dispose() {
        this.systemSet.allSystems().forEach(BaseSystem::dispose);
    }

    public void notifyAspectChanged(int entity) {
        for (IteratingSystem iteratingSystem : this.systemSet.iteratingSystems()) {
            iteratingSystem.notifyAspectChanged(entity);
        }
    }

}
