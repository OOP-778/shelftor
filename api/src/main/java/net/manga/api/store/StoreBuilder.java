package net.manga.api.store;

import net.manga.api.store.expiring.ExpiringStoreBuilder;

public interface StoreBuilder<T, B extends StoreBuilder<T, ?>> {
    /**
     * Make the store use hashable references rather than identity references.
     */
    B hashable();

    /**
     * Use weak references for the store, this will allow the store values to be garbage collected
     */
    B weak();

    /**
     * Make the store use concurrent data structures
     */
    B concurrent();

    /**
     * Move onto expiring store
     */
    ExpiringStoreBuilder<T, ?> expiring();

    <S extends MangaStore<T>> S build();
}
