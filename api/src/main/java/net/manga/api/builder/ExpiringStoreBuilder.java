package net.manga.api.builder;

import net.manga.api.expiring.ExpiringPolicy;
import net.manga.api.store.ExpiringMangaStore;

public interface ExpiringStoreBuilder<T, B extends ExpiringStoreBuilder<T, ?>> extends StoreBuilder<T, B> {

    B usePolicy(ExpiringPolicy<T> policy);

    @Override
    default ExpiringStoreBuilder<T, ?> expiring() {
        return this;
    }

    @Override
    ExpiringMangaStore<T> build();
}
