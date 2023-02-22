package dev.oop778.shelftor.api.expiring.policy;

import lombok.NonNull;

public interface ExpiringPolicyWithData<T, D> extends ExpiringPolicy<T> {

    @Override
    default boolean shouldExpire(@NonNull T value) {
        throw new IllegalStateException("Expiring policy must be checked with data");
    }

    default boolean shouldCallOnAccess() {
        return false;
    }

    boolean shouldExpire(@NonNull T value, @NonNull D data);

    D createExpirationData(@NonNull T value);

    void onAccess(@NonNull T value, @NonNull D data);
}
