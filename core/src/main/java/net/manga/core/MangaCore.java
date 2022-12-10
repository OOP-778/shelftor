package net.manga.core;

import net.manga.api.Manga;
import net.manga.api.builder.StoreBuilder;
import net.manga.api.query.Query;
import net.manga.core.query.QueryImpl;
import net.manga.core.store.CoreMangaStoreBuilder;

public class MangaCore extends Manga {

    @Override
    public Query createQuery() {
        return new QueryImpl();
    }

    @Override
    public <T> StoreBuilder<T, ?> createBuilder() {
        return new CoreMangaStoreBuilder<>();
    }
}
