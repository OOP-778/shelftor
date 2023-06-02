package dev.oop778.shelftor.core.shelf.expiring;

import dev.oop778.shelftor.api.expiring.policy.ExpiringPolicyWithData;
import dev.oop778.shelftor.api.query.Query;
import dev.oop778.shelftor.api.reference.EntryReference;
import dev.oop778.shelftor.api.shelf.expiring.ExpiringShelf;
import dev.oop778.shelftor.api.util.Closeable;
import dev.oop778.shelftor.core.expiring.ExpirationManager;
import dev.oop778.shelftor.core.shelf.CoreShelf;
import dev.oop778.shelftor.core.util.collection.ReferencedCollection;
import dev.oop778.shelftor.core.util.log.LogDebug;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import lombok.NonNull;

public class CoreExpiringShelf<T> extends CoreShelf<T> implements ExpiringShelf<T> {
    private final ExpirationManager<T> expirationManager;
    private final boolean needsAccessCalls;
    private final Collection<ExpirationHandler<T>> globalExpirationListeners;

    public CoreExpiringShelf(CoreExpiringShelfSettings<T> settings) {
        super(settings);

        this.globalExpirationListeners = settings.isConcurrent() ? new LinkedHashSet<>() : new HashSet<>();
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

    @Override
    public Closeable onExpire(@NonNull ExpirationHandler<T> handler) {
        this.globalExpirationListeners.add(handler);
        return () -> this.globalExpirationListeners.remove(handler);
    }

    protected void checkup(EntryReference<T> reference) {
        if (this.expirationManager.shouldExpire(reference)) {
            this.globalExpirationListeners.forEach((handler) -> handler.onExpire(reference.get()));

            LogDebug.log("[ExpiringStore]: Expiring reference: " + reference.get());
            this.referenceManager.releaseReference(reference);
        }
    }

    @Override
    public CoreExpiringShelfSettings<T> getSettings() {
        return (CoreExpiringShelfSettings<T>) super.getSettings();
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
}
