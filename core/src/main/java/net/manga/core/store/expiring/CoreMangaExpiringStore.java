package net.manga.core.store.expiring;

import net.manga.api.reference.EntryReference;
import net.manga.api.store.expiring.ExpiringMangaStore;
import net.manga.core.expiring.ExpirationManager;
import net.manga.core.store.MangaCoreStore;
import net.manga.core.util.log.LogDebug;

public class CoreMangaExpiringStore<T> extends MangaCoreStore<T> implements ExpiringMangaStore<T> {
    private final ExpirationManager<T> expirationManager;

    public CoreMangaExpiringStore(CoreMangaExpiringStoreSettings<T> settings) {
        super(settings);

        this.expirationManager = new ExpirationManager<>(this);
    }

    @Override
    public void invalidate() {
        for (final EntryReference<T> reference : this.referenceManager.getReferenceMap().values()) {
            if (this.expirationManager.shouldExpire(reference)) {
                LogDebug.log("[ExpiringStore]: Expiring reference: " + reference.get());
                this.referenceManager.releaseReference(reference);
            }
        }
    }

    @Override
    public CoreMangaExpiringStoreSettings<T> getSettings() {
        return (CoreMangaExpiringStoreSettings<T>) super.getSettings();
    }
}
