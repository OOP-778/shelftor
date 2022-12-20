package net.manga.core.store.expiring;

import net.manga.api.expiring.policy.ExpiringPolicy;
import net.manga.api.store.expiring.ExpiringMangaStore;
import net.manga.api.store.expiring.ExpiringStoreBuilder;
import net.manga.core.store.CoreMangaStoreBuilder;

public class CoreMangaExpiringStoreBuilder<T> extends CoreMangaStoreBuilder<T, CoreMangaExpiringStoreBuilder<T>> implements ExpiringStoreBuilder<T, CoreMangaExpiringStoreBuilder<T>> {

    public CoreMangaExpiringStoreBuilder(CoreMangaStoreBuilder<T, ?> builder) {
        super(builder, CoreMangaExpiringStoreSettings::new);
    }

    @Override
    public CoreMangaExpiringStoreBuilder<T> expireCheckInterval(long periodMs) {
        this.getSettings().setCheckInterval(periodMs);
        return this;
    }

    @Override
    public CoreMangaExpiringStoreBuilder<T> usePolicy(ExpiringPolicy<T> policy) {
        this.getSettings().addPolicy(policy);
        return this;
    }

    protected CoreMangaExpiringStoreSettings<T> getSettings() {
        return (CoreMangaExpiringStoreSettings<T>) this.settings;
    }

    @Override
    public ExpiringMangaStore<T> build() {
        return new CoreMangaExpiringStore<>(this.getSettings());
    }
}
