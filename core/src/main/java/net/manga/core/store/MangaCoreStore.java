package net.manga.core.store;

import lombok.NonNull;
import net.manga.api.index.IndexDefinition;
import net.manga.api.index.StoreIndex;
import net.manga.api.store.MangaStore;
import net.manga.core.index.IndexManager;
import net.manga.core.reference.CoreReferenceManager;
import net.manga.core.util.collection.Collections;
import net.manga.core.util.collection.ListenableCollection;
import org.jetbrains.annotations.Nullable;

public class MangaCoreStore<T> extends ListenableCollection<T> implements MangaStore<T> {
    private final MangaStoreSettings settings;
    private final CoreReferenceManager<T> referenceManager;
    private final IndexManager<T> indexManager;

    public MangaCoreStore(CoreMangaStoreBuilder builder) {
        super(
            null,
            builder.settings.isConcurrent()
        );

        this.settings = builder.settings;
        this.referenceManager = new CoreReferenceManager<>(this);
        this.indexManager = new IndexManager<>(this);

        this.setBacking(Collections.createReferencedCollection(
            this.settings,
            this.referenceManager
        ));
    }

    public static <T> CoreMangaStoreBuilder<T, ?> builder() {
        return new CoreMangaStoreBuilder<>();
    }

    public MangaStoreSettings getSettings() {
        return this.settings;
    }

    public CoreReferenceManager<T> getReferenceManager() {
        return this.referenceManager;
    }

    @Override
    public boolean add(T t) {
        this.referenceManager.runRemoveQueue();
        return super.add(t);
    }

    @Override
    public boolean remove(Object o) {
        this.referenceManager.runRemoveQueue();
        return super.remove(o);
    }

    @Override
    public int size() {
        this.referenceManager.runRemoveQueue();
        return super.size();
    }

    @Override
    public <K> StoreIndex<T> index(@NonNull String indexName, @NonNull IndexDefinition<K, T> indexDefinition) {
        return this.indexManager.index(indexName, indexDefinition);
    }

    @Override
    public boolean removeIndex(StoreIndex<T> index) {
        return this.indexManager.removeIndex(index);
    }

    @Override
    public @Nullable StoreIndex<T> getIndex(@NonNull String index) {
        return this.indexManager.getIndex(index);
    }
}
