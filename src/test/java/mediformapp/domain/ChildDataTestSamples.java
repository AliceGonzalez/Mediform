package mediformapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ChildDataTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ChildData getChildDataSample1() {
        return new ChildData().id(1L).childDataID(1).name("name1").lastName("lastName1");
    }

    public static ChildData getChildDataSample2() {
        return new ChildData().id(2L).childDataID(2).name("name2").lastName("lastName2");
    }

    public static ChildData getChildDataRandomSampleGenerator() {
        return new ChildData()
            .id(longCount.incrementAndGet())
            .childDataID(intCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString());
    }
}
