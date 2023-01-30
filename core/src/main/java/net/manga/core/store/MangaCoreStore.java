package net.manga.core.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import lombok.NonNull;
import net.manga.api.index.IndexDefinition;
import net.manga.api.index.StoreIndex;
import net.manga.api.query.Query;
import net.manga.api.reference.EntryReference;
import net.manga.api.store.MangaStore;
import net.manga.core.index.IndexManager;
import net.manga.core.index.MangaStoreIndex;
import net.manga.core.query.QueryImpl;
import net.manga.core.query.QueryImpl.Operator;
import net.manga.core.reference.CoreReferenceManager;
import net.manga.core.util.collection.Collections;
import net.manga.core.util.collection.ListenableCollection;
import net.manga.core.util.collection.ReferencedCollection;
import org.jetbrains.annotations.Nullable;

public class MangaCoreStore<T> extends ListenableCollection<T> implements MangaStore<T> {
    protected final MangaStoreSettings settings;
    protected final CoreReferenceManager<T> referenceManager;
    protected final IndexManager<T> indexManager;

    public MangaCoreStore(MangaStoreSettings settings) {
        super(
            null,
            settings.isConcurrent()
        );

        this.settings = settings;
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
        final boolean result = super.remove(o);

        if (result) {
            this.referenceManager.releaseReference((T) o);
        }

        return result;
    }

    @Override
    public <K> MangaStoreIndex<T, K> index(@NonNull String indexName, @NonNull IndexDefinition<K, T> indexDefinition) {
        return this.indexManager.index(indexName, indexDefinition);
    }

    @Override
    public boolean removeIndex(StoreIndex<T, ?> index) {
        return this.indexManager.removeIndex(index);
    }

    @Override
    public @Nullable <K> MangaStoreIndex<T, K> getIndex(@NonNull String index) {
        return this.indexManager.getIndex(index);
    }

    @Override
    public Collection<T> get(@NonNull Query query, int limit) {
        return this._get(query, limit);
    }

    protected ReferencedCollection<T> _get(@NonNull Query query, int limit) {
        this.referenceManager.runRemoveQueue();

        final QueryImpl rootQuery = (QueryImpl) query;
        return this.fetch(rootQuery);
    }

    private ReferencedCollection<T> fetch(QueryImpl query) {
        if (query.isInitialized() && query.getQueries().isEmpty()) {
            return this.fetchSingle(query);
        }

        final List<QueryImpl> fetchFrom = new ArrayList<>();
        if (query.isInitialized()) {
            fetchFrom.add(query.single());
        }

        fetchFrom.addAll(query.getQueries());

        final Collection<EntryReference<T>> result = new LinkedHashSet<>();

        for (final QueryImpl singleQuery : fetchFrom) {
            final ReferencedCollection<T> fetch = this.fetch(singleQuery);
            if (query.getOperator() == Operator.AND && fetch.isEmpty()) {
                return fetch;
            }

            if (query.getOperator() == Operator.AND) {
                if (result.isEmpty()) {
                    result.addAll(fetch.getBacking());
                } else {
                    result.retainAll(fetch.getBacking());
                }
            }

            if (query.getOperator() == Operator.OR) {
                result.addAll(fetch.getBacking());
            }
        }

        final ReferencedCollection<T> finalCollection = new ReferencedCollection<>(result, null);
        finalCollection.setFetcher(this.referenceManager::createFetchingReference);

        return finalCollection;
    }

    private ReferencedCollection<T> fetchSingle(QueryImpl query) {
        final MangaStoreIndex<T, Object> index = this.getIndex(query.getIndex());
        if (index == null) {
            throw new IllegalStateException(String.format("Invalid index by name `%s`", query.getIndex()));
        }

        return index.get(query.getValue());
    }

    @Override
    public Collection<T> remove(Query query) {
        final Collection<T> toRemove = this.get(query);
        for (final T value : new HashSet<>(toRemove)) {
            this.remove(value);
        }

        return toRemove;
    }

    protected void onAccess(EntryReference<T> reference) {

    }
}
