package dev.oop778.shelftor.api.expiring.check;

import dev.oop778.shelftor.api.shelf.expiring.ExpiringShelf;

public interface ExpirationCheck<T> {

    void onInitialization(ExpiringShelf<T> store);
}
