package net.manga.core.expiring;

import net.manga.api.expiring.policy.ExpirationData;
import net.manga.api.expiring.policy.ExpiringPolicy;
import net.manga.api.expiring.policy.ExpiringPolicyWithData;
import net.manga.core.reference.CoreReferenceManager;
import net.manga.core.store.expiring.CoreMangaExpiringStore;
import net.manga.core.store.expiring.CoreMangaExpiringStoreSettings;
import net.manga.core.util.closeable.CloseableHolder;

public class ExpirationManager<T> extends CloseableHolder {

    public ExpirationManager(CoreMangaExpiringStore<T> store) {
        final CoreReferenceManager<T> referenceManager = store.getReferenceManager();
        final CoreMangaExpiringStoreSettings<T> settings = store.getSettings();

        // Handle reference creation
        this.addCloseable(referenceManager.onReferenceCreated((value) -> {
            for (final ExpiringPolicy<T> expiringPolicy : settings.expiringPolicies()) {
                if (!(expiringPolicy instanceof ExpiringPolicyWithData)) {
                    continue;
                }

                final T refValue = value.get();
                if (refValue == null) {
                    break;
                }

                final ExpiringPolicyWithData<T, ?> expiringPolicyWithData = (ExpiringPolicyWithData<T, ?>) expiringPolicy;
                final ExpirationData expirationData = expiringPolicyWithData.createExpirationData(refValue);
            }
        }));
    }
}
