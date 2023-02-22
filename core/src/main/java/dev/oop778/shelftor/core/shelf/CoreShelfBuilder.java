package dev.oop778.shelftor.core.shelf;

import dev.oop778.shelftor.core.shelf.expiring.CoreExpiringShelfBuilder;
import java.util.function.UnaryOperator;
import dev.oop778.shelftor.api.store.Shelf;
import dev.oop778.shelftor.api.store.StoreBuilder;
import dev.oop778.shelftor.api.store.expiring.ExpiringShelfBuilder;

public class CoreShelfBuilder<T, B extends StoreBuilder<T, ?>> implements StoreBuilder<T, B> {
    protected CoreShelfSettings settings = CoreShelfSettings.create();

    public CoreShelfBuilder() {
    }

    public CoreShelfBuilder(CoreShelfBuilder<T, ?> from, UnaryOperator<CoreShelfSettings> settingsCreator) {
        this.settings = settingsCreator.apply(from.settings);
    }

    @Override
    public B hashable() {
        this.settings.setHashable(true);
        return (B) this;
    }

    @Override
    public B weak() {
        this.settings.setWeak(true);
        return (B) this;
    }

    @Override
    public B concurrent() {
        this.settings.setConcurrent(true);
        return (B) this;
    }

    @Override
    public ExpiringShelfBuilder<T, ?> expiring() {
        return new CoreExpiringShelfBuilder<>(this);
    }

    @Override
    public Shelf<T> build() {
        return new CoreShelf<>(this.settings);
    }
}
