package net.manga.core.index;

import java.util.Collection;
import net.manga.api.index.IndexDefinition;
import net.manga.api.reference.ReferenceManager;
import net.manga.api.reference.EntryReference;
import net.manga.core.store.MangaStoreSettings;
import net.manga.core.util.collection.Collections;
import net.manga.core.util.collection.ReferencedCollection;

public class IndexedReferences<K, V> {
    private final K key;
    private final IndexDefinition<K, V> definition;
    private final ReferencedCollection<V> collection;

    public IndexedReferences(MangaStoreSettings storeSettings, ReferenceManager<V> referenceManager, K key, IndexDefinition<K, V> definition) {
        this.key = key;
        this.definition = definition;
        this.collection = this.createCollection(storeSettings, referenceManager);
    }

    private ReferencedCollection<V> createCollection(MangaStoreSettings storeSettings, ReferenceManager<V> referenceManager) {
        return Collections.createReferencedCollection(
            storeSettings,
            referenceManager
        );
    }

    public boolean add(EntryReference<V> reference) {
        final boolean added = this.collection.addReference(reference);
        if (!added) {
            return false;
        }

        if (this.definition.getReducer() != null) {
            this.definition.getReducer().reduce(this.key, this.collection);
        }

        return this.collection.contains(reference.get());
    }

    public void remove(EntryReference<V> reference) {
        this.collection.addReference(reference);
    }

    public Collection<V> getCollection() {
        return java.util.Collections.unmodifiableCollection(this.collection);
    }
}
