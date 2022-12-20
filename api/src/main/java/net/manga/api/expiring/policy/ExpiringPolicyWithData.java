package net.manga.api.expiring.policy;

import lombok.NonNull;

public interface ExpiringPolicyWithData<T, D extends ExpirationData> extends ExpiringPolicy<T> {

    @Override
    default boolean shouldExpire(@NonNull T value) {
        throw new IllegalStateException("Expiring policy must be checked with data");
    }

    boolean shouldExpire(@NonNull T value, @NonNull D data);

    D createExpirationData(@NonNull T value);

    void onAccess(@NonNull T value, @NonNull D data);
}
