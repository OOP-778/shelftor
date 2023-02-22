package dev.oop778.shelftor.api.query;

import lombok.NonNull;
import dev.oop778.shelftor.api.Shelftor;

public interface Query {

    static Query create() {
        return Shelftor.get().createQuery();
    }

    static Query where(@NonNull String index, @NonNull Object value) {
        return create().and(index, value);
    }

    Query or(@NonNull Query query);

    Query and(@NonNull Query query);

    Query or(@NonNull String index, @NonNull Object value);

    Query and(@NonNull String index, @NonNull Object value);
}
