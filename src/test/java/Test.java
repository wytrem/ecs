import net.wytrem.ecs.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.stream.IntStream;

public class Test {
    public static void main(String[] args) {
        WorldConfiguration configuration = new WorldConfiguration();

        configuration.setDefaultMapper();

        World world = new World(configuration);


        world.initialize();
        world.push(StateA.class);
        System.out.println("process");

        IntStream.range(0, 4).forEach(world::process);
    }

    public static class MyComponent implements Component {
        public String value = "auie";

        public MyComponent() {

        }

        public MyComponent(String value) {
            this.value = value;
        }
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

        @Inject
        Mapper<MyComponent> myComponentMapper;

        @Override
        public void process() {
            if (pass++ == 2) {
                System.out.println("Mapper is " + myComponentMapper);
                myComponentMapper.set(12, new MyComponent("twelve"));

                world.push(StateB.class);
            }

            System.out.println("This is FirstSystem pass " + pass);
        }
    }

    @Singleton
    public static class SecondSystem extends BaseSystem {
        int pass;

        @Inject
        Mapper<MyComponent> myComponentMapper;

        @Override
        public void process() {
            System.out.println("Mapper is " + myComponentMapper);
            System.out.println(myComponentMapper.get(12).value);
            System.out.println("This is SecondSystem pass " + pass++);
        }
    }
}
