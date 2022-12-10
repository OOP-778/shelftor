package net.manga.api.index;

import java.util.Optional;
import lombok.NonNull;
import net.manga.api.index.reducer.Reducer;
import org.jetbrains.annotations.Nullable;

public interface Indexable<T> {
    <K> StoreIndex<T> index(@NonNull String indexName, @NonNull IndexDefinition<K, T> indexDefinition);

    default <K> StoreIndex<T> index(String indexName, KeyMapper<K, T> keyMapper) {
        return this.index(indexName, IndexDefinition.withKeyMapping(keyMapper));
    }

    default <K> StoreIndex<T> index(String indexName, KeyMapper<K, T> keyMapper, Reducer<K, T> reducer) {
        return this.index(indexName, IndexDefinition.withKeyMapping(keyMapper).withReducer(reducer));
    }

    boolean removeIndex(StoreIndex<T> index);

    default boolean removeIndex(String indexName) {
        return this.findIndex(indexName).map(this::removeIndex).orElse(false);
    }

    @Nullable
    StoreIndex<T> getIndex(@NonNull String index);

    default Optional<StoreIndex<T>> findIndex(@NonNull String index) {
        return Optional.ofNullable(this.getIndex(index));
    }
}
