package mediformapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SavedFormsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static SavedForms getSavedFormsSample1() {
        return new SavedForms().id(1L).savedFormID(1).formID(1).formType("formType1");
    }

    public static SavedForms getSavedFormsSample2() {
        return new SavedForms().id(2L).savedFormID(2).formID(2).formType("formType2");
    }

    public static SavedForms getSavedFormsRandomSampleGenerator() {
        return new SavedForms()
            .id(longCount.incrementAndGet())
            .savedFormID(intCount.incrementAndGet())
            .formID(intCount.incrementAndGet())
            .formType(UUID.randomUUID().toString());
    }
}
