package net.manga.api.query;

import lombok.NonNull;
import net.manga.api.Manga;

public interface Query {

    static Query create() {
        return Manga.get().createQuery();
    }

    Query or(@NonNull Query query);

    Query and(@NonNull Query query);

    default Query or(@NonNull String index, @NonNull Object value) {
        return this.or(create().or(index, value));
    }

    default Query and(@NonNull String index, @NonNull Object value) {
        return this.or(create().and(index, value));
    }
}
