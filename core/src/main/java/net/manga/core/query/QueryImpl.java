package net.manga.core.query;

import lombok.NonNull;
import net.manga.api.query.Query;

public class QueryImpl implements Query {

    @Override
    public Query or(@NonNull Query query) {
        return null;
    }

    @Override
    public Query or(@NonNull String index, @NonNull Object value) {
        return null;
    }

    @Override
    public Query and(@NonNull String index, @NonNull Object value) {
        return null;
    }

    @Override
    public Query and(@NonNull Query query) {
        return null;
    }
}
