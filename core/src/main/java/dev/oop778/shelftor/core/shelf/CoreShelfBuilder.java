package dev.oop778.shelftor.core.shelf;

import dev.oop778.shelftor.core.shelf.expiring.CoreExpiringShelfBuilder;
import java.util.function.UnaryOperator;
import dev.oop778.shelftor.api.shelf.Shelf;
import dev.oop778.shelftor.api.shelf.ShelfBuilder;
import dev.oop778.shelftor.api.shelf.expiring.ExpiringShelfBuilder;

public class CoreShelfBuilder<T, B extends ShelfBuilder<T, ?>> implements ShelfBuilder<T, B> {
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
