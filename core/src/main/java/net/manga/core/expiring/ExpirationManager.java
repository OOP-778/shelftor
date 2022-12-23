package net.manga.core.expiring;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import net.manga.api.expiring.policy.ExpiringPolicy;
import net.manga.api.expiring.policy.ExpiringPolicyWithData;
import net.manga.api.reference.EntryReference;
import net.manga.core.reference.CoreReferenceManager;
import net.manga.core.store.expiring.CoreMangaExpiringStore;
import net.manga.core.store.expiring.CoreMangaExpiringStoreSettings;
import net.manga.core.util.closeable.CloseableHolder;
import net.manga.core.util.log.LogDebug;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ExpirationManager<T> extends CloseableHolder {
    private final CoreMangaExpiringStoreSettings<T> settings;
    private final Map<Integer, Map<Class<?>, Object>> expirationData;

    public ExpirationManager(CoreMangaExpiringStore<T> store) {
        final CoreReferenceManager<T> referenceManager = store.getReferenceManager();
        this.settings = store.getSettings();
        this.expirationData = this.settings.isConcurrent() ? new ConcurrentHashMap<>() : new HashMap<>();

        // Handle removing of references
        this.addCloseable(referenceManager.onReferenceRemove((reference) -> {
            LogDebug.log("[ExpirationManager]: Reference removed: " + reference.get());
            this.expirationData.remove(reference.hashCode());
        }));

        // Handle reference creation
        this.addCloseable(referenceManager.onReferenceCreated((reference) -> {
            LogDebug.log("[ExpirationManager]: Reference created: " + reference.get());
            for (final ExpiringPolicy<T> expiringPolicy : this.settings.expiringPolicies()) {
                if (!(expiringPolicy instanceof ExpiringPolicyWithData)) {
                    continue;
                }

                final T refValue = reference.get();
                if (refValue == null) {
                    break;
                }

                final ExpiringPolicyWithData<T, ?> expiringPolicyWithData = (ExpiringPolicyWithData<T, ?>) expiringPolicy;
                final Object expirationData = expiringPolicyWithData.createExpirationData(refValue);

                this.expirationData
                    .computeIfAbsent(reference.hashCode(), ($) -> new IdentityHashMap<>())
                    .put(expiringPolicyWithData.getClass(), expirationData);
            }
        }));

        // Handle access if any policies require it
        this.addCloseable(referenceManager.onReferenceAccess((reference) -> {
            final List<? extends ExpiringPolicyWithData<T, ?>> policies = this.settings.expiringPolicies()
                .stream()
                .map((policy) -> policy instanceof ExpiringPolicyWithData ? (ExpiringPolicyWithData<T, ?>) policy : null)
                .filter(Objects::nonNull)
                .filter(ExpiringPolicyWithData::shouldCallOnAccess)
                .collect(Collectors.toList());

            final Map<Class<?>, Object> policyToData = this.expirationData.get(reference.hashCode());

            for (final ExpiringPolicyWithData policy : policies) {
                policy.onAccess(reference.get(), policyToData.get(policy.getClass()));
            }
        }));
    }

    public boolean shouldExpire(EntryReference<T> reference) {
        boolean shouldExpire = false;
        final Map<Class<?>, Object> policyToData = this.expirationData.get(reference.hashCode());

        for (final ExpiringPolicy<T> expiringPolicy : this.settings.expiringPolicies()) {
            if (expiringPolicy instanceof ExpiringPolicyWithData) {
                final Object expirationData = policyToData.get(expiringPolicy.getClass());
                if (((ExpiringPolicyWithData) expiringPolicy).shouldExpire(reference.get(), expirationData)) {
                    shouldExpire = true;
                }
                continue;
            }

            if (expiringPolicy.shouldExpire(reference.get())) {
                shouldExpire = true;
            }
        }

        return shouldExpire;
    }
}
