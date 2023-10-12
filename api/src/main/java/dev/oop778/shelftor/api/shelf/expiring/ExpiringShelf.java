package dev.oop778.shelftor.api.shelf.expiring;

import dev.oop778.shelftor.api.expiring.policy.ExpiringPolicy;
import dev.oop778.shelftor.api.expiring.policy.ExpiringPolicyWithData;
import dev.oop778.shelftor.api.shelf.Shelf;
import dev.oop778.shelftor.api.util.Closeable;
import java.util.Map;
import lombok.NonNull;

public interface ExpiringShelf<T> extends Shelf<T> {

    ExpiringShelfSettings<T> getSettings();

    int invalidate();

    Closeable onExpire(@NonNull ExpirationHandler<T> handler);

    Map<ExpiringPolicyWithData<T, ?>, Object> getExpirationData(T value);

    @FunctionalInterface
    interface ExpirationHandler<T> {
        void onExpire(T object);
    }
}
