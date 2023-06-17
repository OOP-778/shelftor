package dev.oop778.shelftor.api.index;

import dev.oop778.shelftor.api.index.reducer.Reducer;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public interface Indexable<T> {
    default <K> ShelfIndex<T, K> index(String indexName, KeyMapper<K, T> keyMapper) {
        return this.index(indexName, IndexDefinition.withKeyMapping(keyMapper));
    }

    <K> ShelfIndex<T, K> index(@NonNull String indexName, @NonNull IndexDefinition<K, T> indexDefinition);

    default <K> ShelfIndex<T, K> index(String indexName, KeyMapper<K, T> keyMapper, Reducer<K, T> reducer) {
        return this.index(indexName, IndexDefinition.withKeyMapping(keyMapper).withReducer(reducer));
    }

    default boolean removeIndex(String indexName) {
        return this.findIndex(indexName).map(this::removeIndex).orElse(false);
    }

    default <K> Optional<ShelfIndex<T, K>> findIndex(@NonNull String index) {
        return Optional.ofNullable(this.getIndex(index));
    }

    boolean removeIndex(ShelfIndex<T, ?> index);

    @Nullable <K> ShelfIndex<T, K> getIndex(@NonNull String index);

    default void reindex(@NonNull T value) {
        this.reindex(value, this.getIndexes().keySet());
    }

    void reindex(@NonNull T value, @NonNull Collection<String> indexNames);

    <K> Map<String, ShelfIndex<T, K>> getIndexes();

    default void reindex(@NonNull T value, String index) {
        this.reindex(value, Collections.singletonList(index));
    }
}
