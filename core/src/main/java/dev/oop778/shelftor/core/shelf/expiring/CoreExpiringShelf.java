package dev.oop778.shelftor.core.shelf.expiring;

import dev.oop778.shelftor.core.shelf.CoreShelf;
import lombok.NonNull;
import dev.oop778.shelftor.api.expiring.policy.ExpiringPolicyWithData;
import dev.oop778.shelftor.api.query.Query;
import dev.oop778.shelftor.api.reference.EntryReference;
import dev.oop778.shelftor.api.store.expiring.ExpiringShelf;
import dev.oop778.shelftor.core.expiring.ExpirationManager;
import dev.oop778.shelftor.core.util.collection.ReferencedCollection;
import dev.oop778.shelftor.core.util.log.LogDebug;

public class CoreExpiringShelf<T> extends CoreShelf<T> implements ExpiringShelf<T> {
    private final ExpirationManager<T> expirationManager;
    private final boolean needsAccessCalls;

    public CoreExpiringShelf(CoreExpiringShelfSettings<T> settings) {
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
    public CoreExpiringShelfSettings<T> getSettings() {
        return (CoreExpiringShelfSettings<T>) super.getSettings();
    }
}
