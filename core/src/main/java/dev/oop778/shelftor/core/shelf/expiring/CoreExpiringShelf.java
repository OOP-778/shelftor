package dev.oop778.shelftor.core.shelf.expiring;

import dev.oop778.shelftor.api.expiring.policy.ExpiringPolicyWithData;
import dev.oop778.shelftor.api.reference.EntryReference;
import dev.oop778.shelftor.api.shelf.expiring.ExpiringShelf;
import dev.oop778.shelftor.api.util.Closeable;
import dev.oop778.shelftor.core.expiring.ExpirationManager;
import dev.oop778.shelftor.core.shelf.CoreShelf;
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
            .stream()
            .anyMatch((policy) -> {
                if (!(policy instanceof ExpiringPolicyWithData)) {
                    return false;
                }

                return ((ExpiringPolicyWithData<?, ?>) policy).shouldCallOnAccess();
            });
    }

    @Override
    public int invalidate() {
        this.referenceManager.runRemoveQueue();

        int removed = 0;
        for (final EntryReference<T> reference : this.referenceManager.getReferenceMap().values()) {
            if (this.tryExpire(reference)) {
                removed++;
            }
        }

        return removed;
    }

    @Override
    public Closeable onExpire(@NonNull ExpirationHandler<T> handler) {
        this.globalExpirationListeners.add(handler);
        return () -> this.globalExpirationListeners.remove(handler);
    }

    protected boolean tryExpire(EntryReference<T> reference) {
        if (reference.isMarked()) {
            return false;
        }

        if (this.expirationManager.shouldExpire(reference)) {
            this.globalExpirationListeners.forEach((handler) -> handler.onExpire(reference.get()));

            LogDebug.log("Expiring reference: " + reference.get());
            return this.referenceManager.releaseReference(reference);
        }

        return false;
    }

    @Override
    public CoreExpiringShelfSettings<T> getSettings() {
        return (CoreExpiringShelfSettings<T>) super.getSettings();
    }

    @Override
    protected void postFetch(Collection<T> result) {
        for (final T value : result) {
            final EntryReference<T> fetchingReference = this.referenceManager.getRealReference(value);
            if (fetchingReference == null) {
                continue;
            }

            if (this.tryExpire(fetchingReference)) {
                continue;
            }

            if (this.needsAccessCalls) {
                this.referenceManager.callReferenceAccess(fetchingReference);
            }
        }
    }
}
