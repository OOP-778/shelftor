package dev.oop778.shelftor.api.store.expiring;

import java.util.concurrent.TimeUnit;
import dev.oop778.shelftor.api.expiring.policy.ExpiringPolicy;
import dev.oop778.shelftor.api.store.StoreBuilder;

public interface ExpiringShelfBuilder<T, B extends ExpiringShelfBuilder<T, ?>> extends StoreBuilder<T, B> {

    B expireCheckInterval(long interval);

    default B expireCheckInterval(long interval, TimeUnit unit) {
        return this.expireCheckInterval(unit.toMillis(interval));
    }

    B usePolicy(ExpiringPolicy<T> policy);

    @Override
    default ExpiringShelfBuilder<T, ?> expiring() {
        return this;
    }

    @Override
    ExpiringShelf<T> build();
}
