package net.manga.core.store;

import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.manga.api.store.MangaStore;
import net.manga.api.store.StoreBuilder;
import net.manga.api.store.expiring.ExpiringStoreBuilder;
import net.manga.core.store.expiring.CoreMangaExpiringStoreBuilder;

public class CoreMangaStoreBuilder<T, B extends StoreBuilder<T, ?>> implements StoreBuilder<T, B> {
    protected MangaStoreSettings settings = MangaStoreSettings.create();

    public CoreMangaStoreBuilder() {
    }

    public CoreMangaStoreBuilder(CoreMangaStoreBuilder<T, ?> from, UnaryOperator<MangaStoreSettings> settingsCreator) {
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
    public ExpiringStoreBuilder<T, ?> expiring() {
        return new CoreMangaExpiringStoreBuilder<>(this);
    }

    @Override
    public MangaStore<T> build() {
        return new MangaCoreStore<>(this.settings);
    }
}
