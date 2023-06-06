package dev.oop778.shelftor.api.shelf.expiring;

import dev.oop778.shelftor.api.shelf.Shelf;
import dev.oop778.shelftor.api.util.Closeable;
import lombok.NonNull;

public interface ExpiringShelf<T> extends Shelf<T> {

    ExpiringShelfSettings<T> getSettings();

    int invalidate();

    Closeable onExpire(@NonNull ExpirationHandler<T> handler);

    @FunctionalInterface
    interface ExpirationHandler<T> {
        void onExpire(T object);
    }
}
