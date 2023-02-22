package dev.oop778.shelftor.api.index;

import dev.oop778.shelftor.api.index.comparison.ComparisonPolicy;
import dev.oop778.shelftor.api.index.reducer.Reducer;
import java.util.Collection;
import java.util.Collections;

public final class IndexDefinition<K, V> {
    private final KeyMapper<Collection<K>, V> keyMapper;
    private ComparisonPolicy<K> comparisonPolicy;
    private Reducer<K, V> reducer;

    private IndexDefinition(KeyMapper<Collection<K>, V> keyMapper) {
        this.keyMapper = keyMapper;
        this.reducer = null;
    }

    public static <K, V> IndexDefinition<K, V> withKeyMapping(KeyMapper<K, V> keyMapper) {
        return withKeyMappings(value -> {
            final K key = keyMapper.map(value);
            return key == null ? Collections.emptyList() : Collections.singletonList(key);
        });
    }

    public static <K, V> IndexDefinition<K, V> withKeyMappings(KeyMapper<Collection<K>, V> mapper) {
        return new IndexDefinition<>(mapper);
    }

    public IndexDefinition<K, V> withReducer(Reducer<K, V> reducer) {
        this.reducer = reducer;
        return this;
    }

    public IndexDefinition<K, V> withComparisonPolicy(ComparisonPolicy<K> comparisonPolicy) {
        this.comparisonPolicy = comparisonPolicy;
        return this;
    }

    public KeyMapper<Collection<K>, V> getKeyMapper() {
        return this.keyMapper;
    }

    public ComparisonPolicy<K> getComparisonPolicy() {
        return this.comparisonPolicy;
    }

    public Reducer<K, V> getReducer() {
        return this.reducer;
    }
}
