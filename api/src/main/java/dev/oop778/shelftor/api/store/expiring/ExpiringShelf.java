package dev.oop778.shelftor.api.store.expiring;

import dev.oop778.shelftor.api.store.Shelf;

public interface ExpiringShelf<T> extends Shelf<T> {

    ExpiringShelfSettings getSettings();

    void invalidate();

}
