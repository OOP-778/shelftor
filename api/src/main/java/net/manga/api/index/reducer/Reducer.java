package net.manga.api.index.reducer;

import java.util.Arrays;
import java.util.Collection;

@FunctionalInterface
public interface Reducer<K, V> {
    void reduce(K key, Collection<V> elements);

    default Reducer<K, V> andThen(final Reducer<K, V> reducer) {
        return new MultiReducer<>(Arrays.asList(this, reducer));
    }

    static <K, V> Reducer<K, V> combine(final Reducer<K, V>... reducer) {
        return new MultiReducer<>(Arrays.asList(reducer));
    }
}
