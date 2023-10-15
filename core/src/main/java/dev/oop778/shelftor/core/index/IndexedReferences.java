package dev.oop778.shelftor.core.index;

import dev.oop778.shelftor.api.index.IndexDefinition;
import dev.oop778.shelftor.api.reference.EntryReference;
import dev.oop778.shelftor.api.reference.ReferenceManager;
import dev.oop778.shelftor.core.shelf.CoreShelfSettings;
import dev.oop778.shelftor.core.util.collection.Collections;
import dev.oop778.shelftor.core.util.collection.ReferencedCollection;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;

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
        // need to keep order
        if (this.definition.getReducer() != null) {
            if (storeSettings.isConcurrent()) {
                return new ReferencedCollection<>(new ConcurrentLinkedDeque<>(), referenceManager);
            }

            return new ReferencedCollection<>(new ArrayList<>(), referenceManager);
        }

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

        //System.out.println(String.format("PRE REDUCE: %s", this.collection));

        if (this.definition.getReducer() != null) {
            this.definition.getReducer().reduce(this.key, this.collection);
        }

        //System.out.println(String.format("POST REDUCE: %s", this.collection));

        return this.collection.contains(reference.get());
    }

    public void remove(EntryReference<V> reference) {
        this.collection.removeReference(reference);
    }

    public ReferencedCollection<V> getCollection() {
        return this.collection;
    }
}
