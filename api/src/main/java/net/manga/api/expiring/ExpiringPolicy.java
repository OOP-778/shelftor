package net.manga.api.expiring;

import lombok.NonNull;

public interface ExpiringPolicy<T> {

    boolean shouldExpire(@NonNull T value);

}
