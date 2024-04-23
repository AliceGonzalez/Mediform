package mediformapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FormStatusTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static FormStatus getFormStatusSample1() {
        return new FormStatus().id(1L).formStatusID(1).status("status1");
    }

    public static FormStatus getFormStatusSample2() {
        return new FormStatus().id(2L).formStatusID(2).status("status2");
    }

    public static FormStatus getFormStatusRandomSampleGenerator() {
        return new FormStatus()
            .id(longCount.incrementAndGet())
            .formStatusID(intCount.incrementAndGet())
            .status(UUID.randomUUID().toString());
    }
}
