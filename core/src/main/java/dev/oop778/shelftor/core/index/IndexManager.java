package dev.oop778.shelftor.core.index;

import dev.oop778.shelftor.core.shelf.CoreShelf;
import dev.oop778.shelftor.core.util.closeable.CloseableHolder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import dev.oop778.shelftor.api.index.IndexDefinition;
import dev.oop778.shelftor.api.index.Indexable;
import dev.oop778.shelftor.api.index.ShelfIndex;
import dev.oop778.shelftor.api.util.Closeable;
import org.jetbrains.annotations.Nullable;

public class IndexManager<T> extends CloseableHolder implements Indexable<T> {
    private final CoreShelf<T> store;
    private final Map<String, CoreShelfIndex<T, ?>> indexes;

    public IndexManager(CoreShelf<T> store) {
        this.store = store;
        this.indexes = store.getSettings().isConcurrent() ? new ConcurrentHashMap<>() : new HashMap<>();
        this.addCloseable(store.getReferenceManager().onReferenceCreated((reference) -> this.indexes.values().forEach((index) -> index.index(reference))));
    }

    @Override
    public <K> CoreShelfIndex<T, K> index(@NonNull String indexName, @NonNull IndexDefinition<K, T> indexDefinition) {
        final CoreShelfIndex<T, K> index = new CoreShelfIndex<>(this.store, indexName, indexDefinition);
        this.indexes.put(indexName, index);
        this.addCloseable(index);

        return index;
    }

    @Override
    public boolean removeIndex(ShelfIndex<T, ?> index) {
        this.removeCloseable((Closeable) index);
        return this.indexes.remove(index.getName()) != null;
    }

    @Override
    @Nullable
    public <K> CoreShelfIndex<T, K> getIndex(@NonNull String index) {
        return (CoreShelfIndex<T, K>) this.indexes.get(index);
    }
}
