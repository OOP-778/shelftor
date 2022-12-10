package net.manga.api.index.reducer;

import java.util.Arrays;
import java.util.Collection;
import net.manga.api.reference.ValueReference;

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
