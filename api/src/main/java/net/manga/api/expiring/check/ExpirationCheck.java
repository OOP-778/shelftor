package net.manga.api.expiring.check;

import net.manga.api.store.expiring.ExpiringMangaStore;

public interface ExpirationCheck<T> {

    void onInitialization(ExpiringMangaStore<T> store);
}
