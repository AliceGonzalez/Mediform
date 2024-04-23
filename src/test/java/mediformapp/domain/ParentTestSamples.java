package mediformapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ParentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Parent getParentSample1() {
        return new Parent().id(1L).parentID(1).name("name1").lastName("lastName1");
    }

    public static Parent getParentSample2() {
        return new Parent().id(2L).parentID(2).name("name2").lastName("lastName2");
    }

    public static Parent getParentRandomSampleGenerator() {
        return new Parent()
            .id(longCount.incrementAndGet())
            .parentID(intCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString());
    }
}
