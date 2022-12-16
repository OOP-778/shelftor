package net.manga.api.store.expiring;

import java.util.Collection;
import net.manga.api.expiring.ExpiringPolicy;
import net.manga.api.store.StoreSettings;

public interface ExpiringStoreSettings<T> extends StoreSettings {
    long checkInterval();

    Collection<ExpiringPolicy<T>> expiringPolicies();
}
