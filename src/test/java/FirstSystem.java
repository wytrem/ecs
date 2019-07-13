import net.wytrem.ecs.*;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FirstSystem extends Service {

    @Inject
    Mapper<Name> nameMapper;

    int entity;


}
