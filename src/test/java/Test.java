import net.wytrem.ecs.*;

import javax.inject.Singleton;
import java.util.stream.IntStream;

public class Test {
    public static void main(String[] args) {
        WorldConfiguration configuration = new WorldConfiguration();

        World world = new World(configuration);
        world.initialize();
        world.push(StateA.class);
        System.out.println("process");


        IntStream.range(0, 30).forEach(world::process);
    }

    public static class StateA extends GameState {
        public StateA() {
            super();
            register(FirstSystem.class);
        }
    }

    public static class StateB extends GameState {
        public StateB() {
            super();
            register(SecondSystem.class);
        }
    }

    @Singleton
    public static class FirstSystem extends BaseSystem {
        int pass;

        @Override
        public void process() {
            if (pass++ == 10) {
                world.push(StateB.class);
            }

            System.out.println("This is FirstSystem pass " + pass);
        }
    }

    @Singleton
    public static class SecondSystem extends BaseSystem {
        int pass;

        @Override
        public void process() {
            System.out.println("This is SecondSystem pass " + pass);
        }
    }
}
