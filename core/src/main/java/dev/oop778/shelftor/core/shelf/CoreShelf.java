package dev.oop778.shelftor.core.shelf;

import dev.oop778.shelftor.core.query.CoreQuery;
import dev.oop778.shelftor.core.query.CoreQuery.Operator;
import dev.oop778.shelftor.core.util.collection.ListenableCollection;
import dev.oop778.shelftor.core.util.collection.ReferencedCollection;
import dev.oop778.shelftor.api.store.Shelf;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import lombok.NonNull;
import dev.oop778.shelftor.api.index.IndexDefinition;
import dev.oop778.shelftor.api.index.ShelfIndex;
import dev.oop778.shelftor.api.query.Query;
import dev.oop778.shelftor.api.reference.EntryReference;
import dev.oop778.shelftor.core.index.IndexManager;
import dev.oop778.shelftor.core.index.CoreShelfIndex;
import dev.oop778.shelftor.core.reference.CoreReferenceManager;
import dev.oop778.shelftor.core.util.collection.Collections;
import org.jetbrains.annotations.Nullable;

public class CoreShelf<T> extends ListenableCollection<T> implements Shelf<T> {
    protected final CoreShelfSettings settings;
    protected final CoreReferenceManager<T> referenceManager;
    protected final IndexManager<T> indexManager;

    public CoreShelf(CoreShelfSettings settings) {
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

    public static <T> CoreShelfBuilder<T, ?> builder() {
        return new CoreShelfBuilder<>();
    }

    public CoreShelfSettings getSettings() {
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
    public <K> CoreShelfIndex<T, K> index(@NonNull String indexName, @NonNull IndexDefinition<K, T> indexDefinition) {
        return this.indexManager.index(indexName, indexDefinition);
    }

    @Override
    public boolean removeIndex(ShelfIndex<T, ?> index) {
        return this.indexManager.removeIndex(index);
    }

    @Override
    public @Nullable <K> CoreShelfIndex<T, K> getIndex(@NonNull String index) {
        return this.indexManager.getIndex(index);
    }

    @Override
    public Collection<T> get(@NonNull Query query, int limit) {
        return this._get(query, limit);
    }

    protected ReferencedCollection<T> _get(@NonNull Query query, int limit) {
        this.referenceManager.runRemoveQueue();

        final CoreQuery rootQuery = (CoreQuery) query;
        return this.fetch(rootQuery);
    }

    private ReferencedCollection<T> fetch(CoreQuery query) {
        if (query.isInitialized() && query.getQueries().isEmpty()) {
            return this.fetchSingle(query);
        }

        final List<CoreQuery> fetchFrom = new ArrayList<>();
        if (query.isInitialized()) {
            fetchFrom.add(query.single());
        }

        fetchFrom.addAll(query.getQueries());

        final Collection<EntryReference<T>> result = new LinkedHashSet<>();

        for (final CoreQuery singleQuery : fetchFrom) {
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

    private ReferencedCollection<T> fetchSingle(CoreQuery query) {
        final CoreShelfIndex<T, Object> index = this.getIndex(query.getIndex());
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
