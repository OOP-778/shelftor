package net.manga.core.store.expiring;

import java.util.ArrayList;
import java.util.Collection;
import net.manga.api.expiring.ExpiringPolicy;
import net.manga.api.store.expiring.ExpiringStoreSettings;
import net.manga.core.store.MangaStoreSettings;

public class CoreMangaExpiringStoreSettings<T> extends MangaStoreSettings implements ExpiringStoreSettings {
    private final Collection<ExpiringPolicy<T>> policies;
    private long checkInterval;

    public CoreMangaExpiringStoreSettings(MangaStoreSettings settings) {
        super(settings);
        this.policies = new ArrayList<>();
    }

    @Override
    public long checkInterval() {
        return this.checkInterval;
    }

    @Override
    public Collection<ExpiringPolicy<T>> expiringPolicies() {
        return this.policies;
    }

    public void setCheckInterval(long checkInterval) {
        this.checkInterval = checkInterval;
    }

    public void addPolicy(ExpiringPolicy<T> policy) {
        this.policies.add(policy);
    }
}
