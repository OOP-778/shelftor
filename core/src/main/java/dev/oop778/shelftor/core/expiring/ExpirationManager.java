package dev.oop778.shelftor.core.expiring;

import dev.oop778.shelftor.api.expiring.policy.ExpiringPolicy;
import dev.oop778.shelftor.api.expiring.policy.ExpiringPolicyWithData;
import dev.oop778.shelftor.api.reference.EntryReference;
import dev.oop778.shelftor.core.reference.CoreReferenceManager;
import dev.oop778.shelftor.core.shelf.expiring.CoreExpiringShelf;
import dev.oop778.shelftor.core.shelf.expiring.CoreExpiringShelfSettings;
import dev.oop778.shelftor.core.util.closeable.CloseableHolder;
import dev.oop778.shelftor.core.util.log.LogDebug;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ExpirationManager<T> extends CloseableHolder {
    private final CoreExpiringShelfSettings<T> settings;
    private final Map<Integer, Map<Class<? extends ExpiringPolicyWithData>, Object>> expirationData;

    public ExpirationManager(CoreExpiringShelf<T> store) {
        final CoreReferenceManager<T> referenceManager = store.getReferenceManager();
        this.settings = store.getSettings();
        this.expirationData = this.settings.isConcurrent() ? new ConcurrentHashMap<>() : new HashMap<>();

        // Handle removing of references
        this.addCloseable(referenceManager.onReferenceRemove((reference) -> {
            this.expirationData.remove(reference.hashCode());
        }));

        // Handle reference creation
        this.addCloseable(referenceManager.onReferenceCreated((reference) -> {
            LogDebug.log("Reference created: %s", reference.get());
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
            LogDebug.log("Reference accessed: %s", reference.get());

            final List<? extends ExpiringPolicyWithData<T, ?>> policies = this.settings.expiringPolicies()
                .stream()
                .map((policy) -> policy instanceof ExpiringPolicyWithData ? (ExpiringPolicyWithData<T, ?>) policy : null)
                .filter(Objects::nonNull)
                .filter(ExpiringPolicyWithData::shouldCallOnAccess)
                .collect(Collectors.toList());

            final Map<Class<? extends ExpiringPolicyWithData>, Object> policyToData = this.expirationData.get(reference.hashCode());

            LogDebug.log("Calling on access with %s policies", policies.size());

            for (final ExpiringPolicyWithData policy : policies) {
                policy.onAccess(reference.get(), policyToData.get(policy.getClass()));
            }
        }));
    }

    public boolean shouldExpire(EntryReference<T> reference) {
        final Map<Class<? extends ExpiringPolicyWithData>, Object> policyToData = this.expirationData.get(reference.hashCode());

        if (this.settings.expiringPolicies().isEmpty()) {
            return false;
        }

        final boolean shouldAllMatch = this.settings.shouldAllPoliciesMatch();
        if (shouldAllMatch) {
            return this.settings.expiringPolicies()
                .stream().allMatch((policy) -> this.shouldExpire(reference, policy, policyToData));
        }

        return this.settings.expiringPolicies()
            .stream().anyMatch((policy) -> this.shouldExpire(reference, policy, policyToData));
    }

    private boolean shouldExpire(EntryReference<T> reference, ExpiringPolicy<T> policy, Map<Class<? extends ExpiringPolicyWithData>, Object> policyToData) {
        if (policy instanceof ExpiringPolicyWithData) {
            final Object expirationData = policyToData.get(policy.getClass());
            if (expirationData == null) {
                return true;
            }

            return ((ExpiringPolicyWithData) policy).shouldExpire(reference.get(), expirationData);
        }

        return policy.shouldExpire(reference.get());
    }

    public Map<ExpiringPolicyWithData<T, ?>, Object> getExpirationData(EntryReference<T> reference) {
        final Map<Class<? extends ExpiringPolicyWithData>, Object> classObjectMap = this.expirationData.get(reference.hashCode());
        if (classObjectMap == null) {
            return new HashMap<>();
        }

        final Map<ExpiringPolicyWithData<T, ?>, Object> policyData = new HashMap<>();
        for (final ExpiringPolicy<T> expiringPolicy : this.settings.expiringPolicies()) {
            if (!(expiringPolicy instanceof ExpiringPolicyWithData)) {
                continue;
            }

            policyData.put((ExpiringPolicyWithData<T, ?>) expiringPolicy, classObjectMap.get(expiringPolicy.getClass()));
        }

        return policyData;
    }
}
