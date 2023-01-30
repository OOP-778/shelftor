package net.manga.core.store.expiring;

import lombok.NonNull;
import net.manga.api.expiring.policy.ExpiringPolicyWithData;
import net.manga.api.query.Query;
import net.manga.api.reference.EntryReference;
import net.manga.api.store.expiring.ExpiringMangaStore;
import net.manga.core.expiring.ExpirationManager;
import net.manga.core.store.MangaCoreStore;
import net.manga.core.util.collection.ReferencedCollection;
import net.manga.core.util.log.LogDebug;

public class CoreMangaExpiringStore<T> extends MangaCoreStore<T> implements ExpiringMangaStore<T> {
    private final ExpirationManager<T> expirationManager;
    private final boolean needsAccessCalls;

    public CoreMangaExpiringStore(CoreMangaExpiringStoreSettings<T> settings) {
        super(settings);

        this.expirationManager = new ExpirationManager<>(this);
        this.needsAccessCalls = settings.expiringPolicies()
            .stream().anyMatch((policy) -> {
                if (!(policy instanceof ExpiringPolicyWithData)) {
                    return false;
                }

                return ((ExpiringPolicyWithData<?, ?>) policy).shouldCallOnAccess();
            });
    }

    @Override
    public void invalidate() {
        for (final EntryReference<T> reference : this.referenceManager.getReferenceMap().values()) {
            this.checkup(reference);
        }
    }

    protected void checkup(EntryReference<T> reference) {
        if (this.expirationManager.shouldExpire(reference)) {
            LogDebug.log("[ExpiringStore]: Expiring reference: " + reference.get());
            this.referenceManager.releaseReference(reference);
        }
    }

    @Override
    protected ReferencedCollection<T> _get(@NonNull Query query, int limit) {
        final ReferencedCollection<T> result = super._get(query, limit);
        if (this.needsAccessCalls) {
            for (final EntryReference<T> reference : result.getBacking()) {
                this.checkup(reference);
            }
        }

        return result;
    }

    @Override
    public CoreMangaExpiringStoreSettings<T> getSettings() {
        return (CoreMangaExpiringStoreSettings<T>) super.getSettings();
    }
}
