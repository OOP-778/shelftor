package net.manga.api.query;

import lombok.NonNull;
import net.manga.api.Manga;

public interface Query {

    static Query create() {
        return Manga.get().createQuery();
    }

    static Query where(@NonNull String index, @NonNull Object value) {
        return create().and(index, value);
    }

    Query or(@NonNull Query query);

    Query and(@NonNull Query query);

    Query or(@NonNull String index, @NonNull Object value);

    Query and(@NonNull String index, @NonNull Object value);
}
