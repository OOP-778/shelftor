package net.manga.api.store.expiring;

import java.util.concurrent.TimeUnit;
import net.manga.api.expiring.ExpiringPolicy;
import net.manga.api.store.StoreBuilder;

public interface ExpiringStoreBuilder<T, B extends ExpiringStoreBuilder<T, ?>> extends StoreBuilder<T, B> {

    B expireCheckInterval(long interval);

    default B expireCheckInterval(long interval, TimeUnit unit) {
        return this.expireCheckInterval(unit.toMillis(interval));
    }

    B usePolicy(ExpiringPolicy<T> policy);

    @Override
    default ExpiringStoreBuilder<T, ?> expiring() {
        return this;
    }

    @Override
    ExpiringMangaStore<T> build();
}
