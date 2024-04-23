package mediformapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ChildTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Child getChildSample1() {
        return new Child().id(1L).childID(1).name("name1").lastName("lastName1");
    }

    public static Child getChildSample2() {
        return new Child().id(2L).childID(2).name("name2").lastName("lastName2");
    }

    public static Child getChildRandomSampleGenerator() {
        return new Child()
            .id(longCount.incrementAndGet())
            .childID(intCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString());
    }
}
