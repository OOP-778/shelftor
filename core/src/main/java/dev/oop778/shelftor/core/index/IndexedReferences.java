package dev.oop778.shelftor.core.index;

import dev.oop778.shelftor.api.index.IndexDefinition;
import dev.oop778.shelftor.api.reference.EntryReference;
import dev.oop778.shelftor.api.reference.ReferenceManager;
import dev.oop778.shelftor.core.shelf.CoreShelfSettings;
import dev.oop778.shelftor.core.util.collection.Collections;
import dev.oop778.shelftor.core.util.collection.ReferencedCollection;

public class IndexedReferences<K, V> {
    private final K key;
    private final IndexDefinition<K, V> definition;
    private final ReferencedCollection<V> collection;

    public IndexedReferences(CoreShelfSettings storeSettings, ReferenceManager<V> referenceManager, K key, IndexDefinition<K, V> definition) {
        this.key = key;
        this.definition = definition;
        this.collection = this.createCollection(storeSettings, referenceManager);
    }

    private ReferencedCollection<V> createCollection(CoreShelfSettings storeSettings, ReferenceManager<V> referenceManager) {
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

    public ReferencedCollection<V> getCollection() {
        return this.collection;
    }
}
