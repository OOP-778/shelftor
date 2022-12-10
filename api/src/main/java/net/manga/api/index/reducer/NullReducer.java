package net.manga.api.index.reducer;

import java.util.Collection;
import java.util.function.Function;
import net.manga.api.reference.ValueReference;

public class NullReducer<K, V> implements Reducer<K, V> {
    private final Function<V, ?> valueProvider;

    public NullReducer(final Function<V, ?> valueProvider) {
        this.valueProvider = valueProvider;
    }

    @Override
    public void reduce(final K key, final Collection<ValueReference<V>> elements) {
        for (final ValueReference<V> reference : elements) {
            if (this.valueProvider.apply(reference.get()) == null) {
                reference.clear();
            }
        }
    }
}
