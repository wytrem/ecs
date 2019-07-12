import net.wytrem.ecs.*;

import java.util.stream.IntStream;

public class Test {
    public static void main(String[] args) {
        WorldConfiguration configuration = new WorldConfiguration();

        configuration.register(FirstSystem.class);
        configuration.register(SecondSystem.class);

        World world = new World(configuration);
        world.initialize();

        IntStream.range(0, 3).forEach(world::process);
    }
}
