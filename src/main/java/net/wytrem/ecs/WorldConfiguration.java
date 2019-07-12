package net.wytrem.ecs;

import java.util.ArrayList;
import java.util.List;

public class WorldConfiguration {

    public List<Class<? extends BaseSystem>> systemClasses;
    private int nextComponentId = 0;

    public WorldConfiguration() {
        this.systemClasses = new ArrayList<>();
    }

    public void register(Class<? extends BaseSystem> clazz) {
        this.systemClasses.add(clazz);
    }


}
