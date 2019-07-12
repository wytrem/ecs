import net.wytrem.ecs.*;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SecondSystem extends IteratingSystem {

    @Inject
    FirstSystem db;

    @Inject
    Mapper<Name> nameMapper;

    public SecondSystem() {
        super(Aspect.all(Name.class));
    }

    @Override
    public void process(int entity) {
        Name name = this.nameMapper.get(entity);

        System.out.println("Entity #" + entity + " has name : " + name.getFirst() + " " + name.getLast());
    }
}
