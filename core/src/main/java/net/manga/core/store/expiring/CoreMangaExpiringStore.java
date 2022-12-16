package net.manga.core.store.expiring;

import net.manga.api.store.expiring.ExpiringMangaStore;
import net.manga.core.expiring.ExpirationManager;
import net.manga.core.store.MangaCoreStore;

public class CoreMangaExpiringStore<T> extends MangaCoreStore<T> implements ExpiringMangaStore<T> {
    private final ExpirationManager<T> expirationManager;

    public CoreMangaExpiringStore(CoreMangaExpiringStoreSettings<T> settings) {
        super(settings);

        this.expirationManager = new ExpirationManager<>(this);
    }

    @Override
    public void invalidate() {

    }

    @Override
    public CoreMangaExpiringStoreSettings<T> getSettings() {
        return (CoreMangaExpiringStoreSettings<T>) super.getSettings();
    }
}
