package mediformapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TemplateFormTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static TemplateForm getTemplateFormSample1() {
        return new TemplateForm().id(1L).templateFormID(1).formType("formType1");
    }

    public static TemplateForm getTemplateFormSample2() {
        return new TemplateForm().id(2L).templateFormID(2).formType("formType2");
    }

    public static TemplateForm getTemplateFormRandomSampleGenerator() {
        return new TemplateForm()
            .id(longCount.incrementAndGet())
            .templateFormID(intCount.incrementAndGet())
            .formType(UUID.randomUUID().toString());
    }
}
