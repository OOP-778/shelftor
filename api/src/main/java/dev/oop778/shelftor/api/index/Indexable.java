package dev.oop778.shelftor.api.index;

import dev.oop778.shelftor.api.index.reducer.Reducer;
import java.util.Optional;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public interface Indexable<T> {
    <K> ShelfIndex<T, K> index(@NonNull String indexName, @NonNull IndexDefinition<K, T> indexDefinition);

    default <K> ShelfIndex<T, K> index(String indexName, KeyMapper<K, T> keyMapper) {
        return this.index(indexName, IndexDefinition.withKeyMapping(keyMapper));
    }

    default <K> ShelfIndex<T, K> index(String indexName, KeyMapper<K, T> keyMapper, Reducer<K, T> reducer) {
        return this.index(indexName, IndexDefinition.withKeyMapping(keyMapper).withReducer(reducer));
    }

    boolean removeIndex(ShelfIndex<T, ?> index);

    default boolean removeIndex(String indexName) {
        return this.findIndex(indexName).map(this::removeIndex).orElse(false);
    }

    @Nullable
    <K> ShelfIndex<T, K> getIndex(@NonNull String index);

    default <K> Optional<ShelfIndex<T, K>> findIndex(@NonNull String index) {
        return Optional.ofNullable(this.getIndex(index));
    }
}
