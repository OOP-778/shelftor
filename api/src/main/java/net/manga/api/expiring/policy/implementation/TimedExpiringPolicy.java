package net.manga.api.expiring.policy.implementation;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import net.manga.api.expiring.policy.ExpiringPolicy;
import net.manga.api.expiring.policy.ExpiringPolicyWithData;
import net.manga.api.expiring.policy.implementation.TimedExpiringPolicy.TimedExpirationData;

public class TimedExpiringPolicy<T> implements ExpiringPolicyWithData<T, TimedExpirationData> {
    private final Function<T, TimedExpirationData> expirationDataFunction;

    protected TimedExpiringPolicy(final Function<T, TimedExpirationData> expirationDataFunction) {
        this.expirationDataFunction = expirationDataFunction;
    }

    public static <T> ExpiringPolicy<T> create(long time, TimeUnit unit, boolean shouldResetAfterAccess) {
        return new TimedExpiringPolicy<>($ -> new TimedExpirationData(unit, time, shouldResetAfterAccess));
    }

    public static <T> ExpiringPolicy<T> create(Function<T, TimedExpirationData> expirationDataFunction) {
        return new TimedExpiringPolicy<>(expirationDataFunction);
    }

    @Override
    public TimedExpirationData createExpirationData(final T value) {
        return this.expirationDataFunction.apply(value);
    }

    @Override
    public boolean shouldExpire(final T value, final TimedExpirationData data) {
        return (System.currentTimeMillis() - data.lastFetched) >= data.unit.toMillis(data.time);
    }

    @Override
    public void onAccess(final T value, final TimedExpirationData data) {
        if (!data.shouldResetAfterAccess) {
            return;
        }

        data.lastFetched = System.currentTimeMillis();
    }

    public static class TimedExpirationData {
        private final TimeUnit unit;
        private final long time;
        private final boolean shouldResetAfterAccess;
        /** When was reference last fetched. By default it's set to it's creation time */
        private long lastFetched = System.currentTimeMillis();

        public TimedExpirationData(final TimeUnit unit, final long time, final boolean shouldResetAfterAccess) {
            this.unit = unit;
            this.time = time;
            this.shouldResetAfterAccess = shouldResetAfterAccess;
        }
    }
}
