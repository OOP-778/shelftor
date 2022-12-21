package net.manga.core;

import net.manga.api.Manga;
import net.manga.api.reference.EntryReferenceBuilder;
import net.manga.api.store.StoreBuilder;
import net.manga.api.query.Query;
import net.manga.core.query.QueryImpl;
import net.manga.core.reference.CoreEntryReferenceBuilder;
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

    @Override
    public <T> EntryReferenceBuilder<T> createReferenceBuilder() {
        return new CoreEntryReferenceBuilder<>();
    }
}
