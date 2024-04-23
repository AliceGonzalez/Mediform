package mediformapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ChildVisitsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ChildVisits getChildVisitsSample1() {
        return new ChildVisits().id(1L).visitID(1).visitType("visitType1");
    }

    public static ChildVisits getChildVisitsSample2() {
        return new ChildVisits().id(2L).visitID(2).visitType("visitType2");
    }

    public static ChildVisits getChildVisitsRandomSampleGenerator() {
        return new ChildVisits()
            .id(longCount.incrementAndGet())
            .visitID(intCount.incrementAndGet())
            .visitType(UUID.randomUUID().toString());
    }
}
