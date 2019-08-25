package net.wytrem.ecs;

import com.google.inject.Module;

import java.util.ArrayList;
import java.util.List;

public class WorldConfiguration {

    List<Module> extraModules;

    public WorldConfiguration() {
        this.extraModules = new ArrayList<>();
    }

    public void addModule(Module module) {
        this.extraModules.add(module);
    }
}
