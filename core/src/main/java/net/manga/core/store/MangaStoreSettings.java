package net.manga.core.store;

import lombok.ToString;
import net.manga.api.store.StoreSettings;

@ToString
public class MangaStoreSettings implements StoreSettings {
    private boolean concurrent;
    private boolean hashable;
    private boolean weak;

    public MangaStoreSettings(boolean concurrent, boolean hashable) {
        this.concurrent = concurrent;
        this.hashable = hashable;
    }

    public MangaStoreSettings(StoreSettings settings) {
        this.concurrent = settings.isConcurrent();
        this.hashable = settings.isHashable();
        this.weak = settings.isWeak();
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
    public boolean isWeak() {
        return this.weak;
    }

    public void setConcurrent(boolean concurrent) {
        this.concurrent = concurrent;
    }

    public void setHashable(boolean hashable) {
        this.hashable = hashable;
    }

    public void setWeak(boolean weak) {
        this.weak = weak;
    }

    public static MangaStoreSettings create() {
        return new MangaStoreSettings(false, false);
    }
}
