package net.manga.api.expiring;

import java.util.function.Consumer;

public interface Expirable<T> {
    void onExpire(Consumer<T> expiryConsumer);

    void invalidate();
}
