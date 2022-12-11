package net.manga.api.index;

import java.util.Optional;
import lombok.NonNull;
import net.manga.api.index.reducer.Reducer;
import org.jetbrains.annotations.Nullable;

public interface Indexable<T> {
    <K> StoreIndex<T, K> index(@NonNull String indexName, @NonNull IndexDefinition<K, T> indexDefinition);

    default <K> StoreIndex<T, K> index(String indexName, KeyMapper<K, T> keyMapper) {
        return this.index(indexName, IndexDefinition.withKeyMapping(keyMapper));
    }

    default <K> StoreIndex<T, K> index(String indexName, KeyMapper<K, T> keyMapper, Reducer<K, T> reducer) {
        return this.index(indexName, IndexDefinition.withKeyMapping(keyMapper).withReducer(reducer));
    }

    boolean removeIndex(StoreIndex<T, ?> index);

    default boolean removeIndex(String indexName) {
        return this.findIndex(indexName).map(this::removeIndex).orElse(false);
    }

    @Nullable
    <K> StoreIndex<T, K> getIndex(@NonNull String index);

    default <K> Optional<StoreIndex<T, K>> findIndex(@NonNull String index) {
        return Optional.ofNullable(this.getIndex(index));
    }
}
