package net.manga.core.store;

import net.manga.api.builder.ExpiringStoreBuilder;
import net.manga.api.builder.StoreBuilder;
import net.manga.api.store.MangaStore;

public class CoreMangaStoreBuilder<T, B extends StoreBuilder<T, ?>> implements StoreBuilder<T, B> {
    protected MangaStoreSettings settings = MangaStoreSettings.create();

    @Override
    public B hashable() {
        this.settings.setHashable(true);
        return (B) this;
    }

    @Override
    public B weakKeys() {
        this.settings.setWeakKeys(true);
        return (B) this;
    }

    @Override
    public B concurrent() {
        this.settings.setConcurrent(true);
        return (B) this;
    }

    @Override
    public ExpiringStoreBuilder<T, ?> expiring() {
        return null;
    }

    @Override
    public MangaStore<T> build() {
        return new MangaCoreStore<>(this);
    }
}
