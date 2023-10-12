package dev.oop778.shelftor.core.shelf.expiring;

import dev.oop778.shelftor.api.expiring.policy.ExpiringPolicyWithData;
import dev.oop778.shelftor.api.reference.EntryReference;
import dev.oop778.shelftor.api.shelf.expiring.ExpiringShelf;
import dev.oop778.shelftor.api.util.Closeable;
import dev.oop778.shelftor.core.expiring.ExpirationManager;
import dev.oop778.shelftor.core.shelf.CoreShelf;
import dev.oop778.shelftor.core.util.log.LogDebug;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
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

        this.onAccess((value) -> {
            final EntryReference<T> realReference = this.referenceManager.getRealReference(value);
            if (realReference == null) {
                return false;
            }

            return !this.tryExpire(realReference);
        });
    }

    protected boolean tryExpire(EntryReference<T> reference) {
        if (reference.isMarked()) {
            return false;
        }

        if (this.expirationManager.shouldExpire(reference)) {
            final T value = reference.get();
            if (!this.referenceManager.releaseReference(reference)) {
                return false;
            }

            LogDebug.log("Expired reference: %s", reference.get());
            this.globalExpirationListeners.forEach((handler) -> handler.onExpire(value));
            return true;
        }

        return false;
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

    @Override
    public Map<ExpiringPolicyWithData<T, ?>, Object> getExpirationData(T value) {
        final EntryReference<T> realReference = this.referenceManager.getRealReference(value);
        if (realReference == null || realReference.isMarked()) {
            return new HashMap<>();
        }

        return this.expirationManager.getExpirationData(realReference);
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
