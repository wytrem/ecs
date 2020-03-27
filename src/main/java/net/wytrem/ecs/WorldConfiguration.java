package net.wytrem.ecs;

import com.google.inject.Module;

import java.util.ArrayList;
import java.util.List;

/**
 * World configuration class. Used to register {@link com.google.inject.Injector} modules before the world is created.
 * @see Module
 */
public class WorldConfiguration {

    List<Module> extraModules;

    public WorldConfiguration() {
        this.extraModules = new ArrayList<>();
    }

    public void addModule(Module module) {
        this.extraModules.add(module);
    }
}
