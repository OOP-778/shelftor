package dev.oop778.shelftor.api.expiring.policy;

import lombok.NonNull;

public interface ExpiringPolicy<T> {
    boolean shouldExpire(@NonNull T value);
}
