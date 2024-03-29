package dev.oop778.shelftor.api.shelf.expiring;

import java.util.concurrent.TimeUnit;
import dev.oop778.shelftor.api.expiring.policy.ExpiringPolicy;
import dev.oop778.shelftor.api.shelf.ShelfBuilder;

public interface ExpiringShelfBuilder<T, B extends ExpiringShelfBuilder<T, ?>> extends ShelfBuilder<T, B> {

    B expireCheckInterval(long interval);

    default B expireCheckInterval(long interval, TimeUnit unit) {
        return this.expireCheckInterval(unit.toMillis(interval));
    }

    B shouldAllPoliciesMatch(boolean shouldAllPoliciesMatch);

    B usePolicy(ExpiringPolicy<T> policy);

    @Override
    default ExpiringShelfBuilder<T, ?> expiring() {
        return this;
    }

    @Override
    ExpiringShelf<T> build();
}
