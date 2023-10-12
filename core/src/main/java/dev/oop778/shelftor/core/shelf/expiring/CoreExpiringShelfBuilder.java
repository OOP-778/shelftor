package dev.oop778.shelftor.core.shelf.expiring;

import dev.oop778.shelftor.api.expiring.policy.ExpiringPolicy;
import dev.oop778.shelftor.api.shelf.expiring.ExpiringShelf;
import dev.oop778.shelftor.api.shelf.expiring.ExpiringShelfBuilder;
import dev.oop778.shelftor.core.shelf.CoreShelfBuilder;

public class CoreExpiringShelfBuilder<T> extends CoreShelfBuilder<T, CoreExpiringShelfBuilder<T>> implements ExpiringShelfBuilder<T, CoreExpiringShelfBuilder<T>> {

    public CoreExpiringShelfBuilder(CoreShelfBuilder<T, ?> builder) {
        super(builder, CoreExpiringShelfSettings::new);
    }

    @Override
    public CoreExpiringShelfBuilder<T> expireCheckInterval(long periodMs) {
        this.getSettings().setCheckInterval(periodMs);
        return this;
    }

    @Override
    public CoreExpiringShelfBuilder<T> shouldAllPoliciesMatch(boolean shouldAllPoliciesMatch) {
        this.getSettings().setShouldAllPolicesMatch(shouldAllPoliciesMatch);
        return this;
    }

    @Override
    public CoreExpiringShelfBuilder<T> usePolicy(ExpiringPolicy<T> policy) {
        this.getSettings().addPolicy(policy);
        return this;
    }

    protected CoreExpiringShelfSettings<T> getSettings() {
        return (CoreExpiringShelfSettings<T>) this.settings;
    }

    @Override
    public ExpiringShelf<T> build() {
        return new CoreExpiringShelf<>(this.getSettings());
    }
}
