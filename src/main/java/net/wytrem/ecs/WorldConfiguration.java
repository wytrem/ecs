package net.wytrem.ecs;

import com.google.inject.Module;

import java.util.ArrayList;
import java.util.List;

public class WorldConfiguration {

    public List<Module> extraModules;

    public WorldConfiguration() {
        this.extraModules = new ArrayList<>();
    }

    public List<Module> getExtraModules() {
        return extraModules;
    }
}
