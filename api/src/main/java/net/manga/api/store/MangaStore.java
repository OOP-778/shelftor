package net.manga.api.store;

import java.util.Collection;
import net.manga.api.Manga;
import net.manga.api.builder.StoreBuilder;

public interface MangaStore<T> extends Collection<T> {

    static <T> StoreBuilder<T, ?> builder() {
        return Manga.get().createBuilder();
    }
}
