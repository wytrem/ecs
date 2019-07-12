import net.wytrem.ecs.*;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FirstSystem extends BaseSystem {

    @Inject
    Mapper<Name> nameMapper;

    int entity;

    @Override
    public void process() {
        if (this.world.getDelta() == 1) {
            System.out.println("Adding entity in first system");
             entity = this.world.createEntity();
            this.nameMapper.set(entity, new Name("Bob", "McSystem"));
        }
        else if (this.world.getDelta() == 2) {
            this.nameMapper.unset(entity);
        }
    }
}
