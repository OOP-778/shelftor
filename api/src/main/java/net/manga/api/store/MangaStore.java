package net.manga.api.store;

import java.util.Collection;
import net.manga.api.Manga;
import net.manga.api.index.Indexable;
import net.manga.api.query.Queryable;

public interface MangaStore<T> extends Collection<T>, Indexable<T>, Queryable<T> {

    static <T> StoreBuilder<T, ?> builder() {
        return Manga.get().createBuilder();
    }
}
