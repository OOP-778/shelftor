package dev.oop778.shelftor.core.shelf.expiring;

import dev.oop778.shelftor.core.shelf.CoreShelfSettings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import dev.oop778.shelftor.api.expiring.policy.ExpiringPolicy;
import dev.oop778.shelftor.api.shelf.expiring.ExpiringShelfSettings;

public class CoreExpiringShelfSettings<T> extends CoreShelfSettings implements ExpiringShelfSettings {
    private final Collection<ExpiringPolicy<T>> policies;
    private long checkInterval;
    private boolean shouldAllPolicesMatch = true;

    public CoreExpiringShelfSettings(CoreShelfSettings settings) {
        super(settings);
        this.policies = new ArrayList<>();
    }

    @Override
    public long checkInterval() {
        return this.checkInterval;
    }

    @Override
    public Collection<ExpiringPolicy<T>> expiringPolicies() {
        return Collections.unmodifiableCollection(this.policies);
    }

    public void setCheckInterval(long checkInterval) {
        this.checkInterval = checkInterval;
    }

    public void addPolicy(ExpiringPolicy<T> policy) {
        this.policies.add(policy);
    }

    @Override
    public boolean shouldAllPoliciesMatch() {
        return this.shouldAllPolicesMatch;
    }

    public void setShouldAllPolicesMatch(boolean shouldAllPolicesMatch) {
        this.shouldAllPolicesMatch = shouldAllPolicesMatch;
    }

    @Override
    public void dump(Collection<String> lines) {
        super.dump(lines);
        lines.add("checkInterval = " + this.checkInterval);
        if (!this.policies.isEmpty()) {
            lines.add(" == Expiring Policies ==");
            for (final ExpiringPolicy<T> policy : this.policies) {
                lines.add(policy.toString());
            }
        }

    }
}
