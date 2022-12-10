package net.manga.core.index;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import net.manga.api.index.IndexDefinition;
import net.manga.api.index.Indexable;
import net.manga.api.index.StoreIndex;
import net.manga.core.store.MangaCoreStore;
import org.jetbrains.annotations.Nullable;

public class IndexManager<T> implements Indexable<T> {
    private final MangaCoreStore<T> store;
    private final Map<String, MangaStoreIndex<T, ?>> indexes;

    public IndexManager(MangaCoreStore<T> store) {
        this.store = store;
        this.indexes = store.getSettings().isConcurrent() ? new ConcurrentHashMap<>() : new HashMap<>();
    }

    @Override
    public <K> StoreIndex<T> index(@NonNull String indexName, @NonNull IndexDefinition<K, T> indexDefinition) {
        final MangaStoreIndex<T, K> index = new MangaStoreIndex<>(this.store, indexName, indexDefinition);
        this.indexes.put(indexName, index);

        return index;
    }

    @Override
    public boolean removeIndex(StoreIndex<T> index) {
        return this.indexes.remove(index.getName()) != null;
    }

    @Override
    @Nullable
    public StoreIndex<T> getIndex(@NonNull String index) {
        return this.indexes.get(index);
    }
}
