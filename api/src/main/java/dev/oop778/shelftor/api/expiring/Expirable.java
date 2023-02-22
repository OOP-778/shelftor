package dev.oop778.shelftor.api.expiring;

import java.util.function.Consumer;

public interface Expirable<T> {
    void onExpire(Consumer<T> expiryConsumer);

    void invalidate();
}
