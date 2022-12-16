package net.manga.api.store.expiring;

import net.manga.api.store.MangaStore;

public interface ExpiringMangaStore<T> extends MangaStore<T> {

    ExpiringStoreSettings getSettings();

    void invalidate();

}
