package net.manga.api.builder;

import net.manga.api.store.MangaStore;

public interface StoreBuilder<T, B extends StoreBuilder<T, ?>> {
    B hashable();

    B weakKeys();

    B concurrent();

    ExpiringStoreBuilder<T, ?> expiring();

    <S extends MangaStore<T>> S build();
}
