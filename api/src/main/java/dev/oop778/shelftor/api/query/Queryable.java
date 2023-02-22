package dev.oop778.shelftor.api.query;

import java.util.Collection;
import java.util.Optional;
import lombok.NonNull;

public interface Queryable<T> {
    Collection<T> get(@NonNull Query query, int limit);

    default Collection<T> get(@NonNull Query query) {
        return this.get(query, -1);
    }

    default Collection<T> get(@NonNull String indexName, @NonNull Object key, int limit) {
        return this.get(Query.where(indexName, key), limit);
    }

    default Collection<T> get(@NonNull String indexName, @NonNull Object key) {
        return this.get(Query.where(indexName, key), -1);
    }

    default T getFirst(@NonNull Query query) {
        final Collection<T> results = this.get(query, 1);
        return results.isEmpty() ? null : results.stream().findFirst().orElse(null);
    }

    default T getFirst(@NonNull String indexName, @NonNull Object key) {
        return this.getFirst(Query.where(indexName, key));
    }

    default Optional<T> findFirst(@NonNull Query query) {
        return Optional.ofNullable(this.getFirst(query));
    }

    default Optional<T> findFirst(@NonNull String indexName, @NonNull Object key) {
        return this.findFirst(Query.where(indexName,key));
    }

    Collection<T> remove(Query query);
}
