package net.manga.api.index.reducer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.manga.api.reference.ValueReference;

public class MultiReducer<K, V> implements Reducer<K, V> {
    private final List<Reducer<K, V>> reducers;

    protected MultiReducer(final List<Reducer<K, V>> reducers) {
        this.reducers = new ArrayList<>(reducers);
    }

    @Override
    public void reduce(final K key, final Collection<ValueReference<V>> elements) {
        for (final Reducer<K, V> reducer : this.reducers) {
            reducer.reduce(
                key,
                elements.stream()
                    .filter(element -> element.get() != null)
                    .collect(Collectors.toList())
            );
        }
    }

    @Override
    public MultiReducer<K, V> andThen(final Reducer<K, V> reducer) {
        final List<Reducer<K, V>> updatedReducers = new ArrayList<>(this.reducers);
        updatedReducers.add(reducer);
        return new MultiReducer<>(updatedReducers);
    }

    public List<Reducer<K, V>> getReducers() {
        return Collections.unmodifiableList(this.reducers);
    }
}
