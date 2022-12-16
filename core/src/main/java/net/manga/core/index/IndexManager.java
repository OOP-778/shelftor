package net.manga.core.index;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import net.manga.api.index.IndexDefinition;
import net.manga.api.index.Indexable;
import net.manga.api.index.StoreIndex;
import net.manga.api.util.Closeable;
import net.manga.core.store.MangaCoreStore;
import net.manga.core.util.closeable.CloseableHolder;
import org.jetbrains.annotations.Nullable;

public class IndexManager<T> extends CloseableHolder implements Indexable<T> {
    private final MangaCoreStore<T> store;
    private final Map<String, MangaStoreIndex<T, ?>> indexes;

    public IndexManager(MangaCoreStore<T> store) {
        this.store = store;
        this.indexes = store.getSettings().isConcurrent() ? new ConcurrentHashMap<>() : new HashMap<>();
        this.addCloseable(store.getReferenceManager().onReferenceCreated((reference) -> this.indexes.values().forEach((index) -> index.index(reference))));
    }

    @Override
    public <K> MangaStoreIndex<T, K> index(@NonNull String indexName, @NonNull IndexDefinition<K, T> indexDefinition) {
        final MangaStoreIndex<T, K> index = new MangaStoreIndex<>(this.store, indexName, indexDefinition);
        this.indexes.put(indexName, index);
        this.addCloseable(index);

        return index;
    }

    @Override
    public boolean removeIndex(StoreIndex<T, ?> index) {
        this.removeCloseable((Closeable) index);
        return this.indexes.remove(index.getName()) != null;
    }

    @Override
    @Nullable
    public <K> MangaStoreIndex<T, K> getIndex(@NonNull String index) {
        return (MangaStoreIndex<T, K>) this.indexes.get(index);
    }
}
