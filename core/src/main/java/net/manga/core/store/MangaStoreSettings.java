package net.manga.core.store;

import lombok.ToString;
import net.manga.api.store.StoreSettings;

@ToString
public class MangaStoreSettings implements StoreSettings {
    private boolean concurrent;
    private boolean hashable;
    private boolean weakKeys;

    public MangaStoreSettings(boolean concurrent, boolean hashable) {
        this.concurrent = concurrent;
        this.hashable = hashable;
    }

    @Override
    public boolean isConcurrent() {
        return this.concurrent;
    }

    @Override
    public boolean isHashable() {
        return this.hashable;
    }

    @Override
    public boolean isWeakKeys() {
        return this.weakKeys;
    }

    public void setConcurrent(boolean concurrent) {
        this.concurrent = concurrent;
    }

    public void setHashable(boolean hashable) {
        this.hashable = hashable;
    }

    public void setWeakKeys(boolean weakKeys) {
        this.weakKeys = weakKeys;
    }

    public static MangaStoreSettings create() {
        return new MangaStoreSettings(false, false);
    }
}
