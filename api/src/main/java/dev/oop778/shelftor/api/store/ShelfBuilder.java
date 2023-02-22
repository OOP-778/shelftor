package dev.oop778.shelftor.api.store;

import dev.oop778.shelftor.api.store.expiring.ExpiringShelfBuilder;

public interface ShelfBuilder<T, B extends ShelfBuilder<T, ?>> {
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
    ExpiringShelfBuilder<T, ?> expiring();

    <S extends Shelf<T>> S build();
}
