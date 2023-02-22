package dev.oop778.shelftor.api.expiring.policy.implementation;

import dev.oop778.shelftor.api.expiring.policy.ExpiringPolicy;
import dev.oop778.shelftor.api.expiring.policy.ExpiringPolicyWithData;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import dev.oop778.shelftor.api.expiring.policy.implementation.TimedExpiringPolicy.TimedExpirationData;
import org.jetbrains.annotations.NotNull;

public class TimedExpiringPolicy<T> implements ExpiringPolicyWithData<T, TimedExpirationData> {
    private final boolean shouldCallOnAccess;
    private final Function<T, TimedExpirationData> expirationDataFunction;

    protected TimedExpiringPolicy(boolean shouldCallOnAccess, final Function<T, TimedExpirationData> expirationDataFunction) {
        this.shouldCallOnAccess = shouldCallOnAccess;
        this.expirationDataFunction = expirationDataFunction;
    }

    public static <T> ExpiringPolicy<T> create(long time, TimeUnit unit, boolean shouldResetAfterAccess) {
        return new TimedExpiringPolicy<>(shouldResetAfterAccess, $ -> new TimedExpirationData(unit, time, shouldResetAfterAccess));
    }

    public static <T> ExpiringPolicy<T> create(Function<T, TimedExpirationData> expirationDataFunction) {
        return new TimedExpiringPolicy<>(true, expirationDataFunction);
    }

    @Override
    public TimedExpirationData createExpirationData(final @NotNull T value) {
        return this.expirationDataFunction.apply(value);
    }

    @Override
    public boolean shouldExpire(final T value, final TimedExpirationData data) {
        return (System.currentTimeMillis() - data.lastFetched) >= data.unit.toMillis(data.time);
    }

    @Override
    public boolean shouldCallOnAccess() {
        return this.shouldCallOnAccess;
    }

    @Override
    public void onAccess(final @NotNull T value, final @NotNull TimedExpirationData data) {
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
