package mediformapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LoginTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Login getLoginSample1() {
        return new Login().id(1L).username("username1").password("password1");
    }

    public static Login getLoginSample2() {
        return new Login().id(2L).username("username2").password("password2");
    }

    public static Login getLoginRandomSampleGenerator() {
        return new Login().id(longCount.incrementAndGet()).username(UUID.randomUUID().toString()).password(UUID.randomUUID().toString());
    }
}
