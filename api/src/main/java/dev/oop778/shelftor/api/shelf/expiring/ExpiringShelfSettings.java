package dev.oop778.shelftor.api.shelf.expiring;

import dev.oop778.shelftor.api.expiring.policy.ExpiringPolicy;
import dev.oop778.shelftor.api.shelf.ShelfSettings;
import java.util.Collection;

public interface ExpiringShelfSettings<T> extends ShelfSettings {
    long checkInterval();

    Collection<ExpiringPolicy<T>> expiringPolicies();
}
