package dev.oop778.shelftor.core.index;

import dev.oop778.shelftor.api.index.IndexDefinition;
import dev.oop778.shelftor.api.index.ShelfIndex;
import dev.oop778.shelftor.api.index.comparison.ComparisonPolicy;
import dev.oop778.shelftor.api.reference.EntryReference;
import dev.oop778.shelftor.api.reference.ReferenceManager;
import dev.oop778.shelftor.core.shelf.CoreShelf;
import dev.oop778.shelftor.core.shelf.CoreShelfSettings;
import dev.oop778.shelftor.core.util.OptionalLocking;
import dev.oop778.shelftor.core.util.closeable.CloseableHolder;
import dev.oop778.shelftor.core.util.collection.Collections;
import dev.oop778.shelftor.core.util.collection.ReferencedCollection;
import dev.oop778.shelftor.core.util.collection.ReferencedMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

@SuppressWarnings("unchecked")
public class CoreShelfIndex<T, K> extends CloseableHolder implements ShelfIndex<T, K> {
    private final String name;
    private final IndexDefinition<K, T> definition;
    private final ReferenceManager<T> referenceManager;
    private final CoreShelfSettings settings;

    private final Map<K, IndexedReferences<K, T>> keyToReferences;
    private final ReferencedMap<T, Collection<K>> referenceToKeys;
    private final Map<EntryReference<T>, OptionalLocking> locks;

    public CoreShelfIndex(CoreShelf<T> store, String name, IndexDefinition<K, T> indexDefinition) {
        this.name = name;
        this.definition = indexDefinition;
        this.referenceManager = store.getReferenceManager();
        this.settings = store.getSettings();
        this.keyToReferences = this.settings.isConcurrent() ? new ConcurrentHashMap<>() : new HashMap<>();
        this.locks = this.settings.isConcurrent() ? new ConcurrentHashMap<>() : new HashMap<>();
        this.referenceToKeys = Collections.createReferencedMap(store.getSettings(), this.referenceManager);

        this.addCloseable(this.referenceManager.onReferenceRemove(this::removeReference));
    }

    private void removeReference(EntryReference<T> reference) {
        final OptionalLocking optionalLocking = this.locks.computeIfAbsent(reference, r -> new OptionalLocking(this.settings.isConcurrent()));

        optionalLocking.locking(() -> {
            try {
                this.removeReferenceWithoutLocking(reference);
            } finally {
                this.locks.remove(reference);
            }
        });
    }

    private void removeReferenceWithoutLocking(EntryReference<T> reference) {
        final Collection<K> keys = this.referenceToKeys.removeReference(reference);
        if (keys == null) {
            return;
        }

        for (final K key : keys) {
            final IndexedReferences<K, T> indexedReferences = this.keyToReferences.get(key);
            if (indexedReferences == null) {
                continue;
            }

            indexedReferences.remove(reference);
        }
    }

    @Override
    public void index(EntryReference<T> reference) {
        final OptionalLocking optionalLocking = this.locks.computeIfAbsent(reference, ($) -> new OptionalLocking(this.settings.isConcurrent()));

        optionalLocking.locking(() -> {
            try {
                // Remove previous data
                this.removeReferenceWithoutLocking(reference);

                if (reference.isMarked()) {
                    return;
                }

                final T value = reference.get();

                if (value == null) {
                    return;
                }

                final Collection<K> mapped = this.definition.getComparisonPolicy() == null
                                             ? new ArrayList<>(this.definition.getKeyMapper().map(value))
                                             : this.comparableKeys(this.definition.getKeyMapper().map(value));
                if (mapped.isEmpty()) {
                    this.removeReferenceWithoutLocking(reference);
                    return;
                }



                final Iterator<K> iterator = mapped.iterator();
                while (iterator.hasNext()) {
                    final K key = iterator.next();
                    final IndexedReferences<K, T> indexedReferences = this.keyToReferences
                        .computeIfAbsent(
                            key,
                            ($) -> new IndexedReferences<>(this.settings, this.referenceManager, key, this.definition)
                        );

                    if (!indexedReferences.add(reference)) {
                        iterator.remove();
                    }
                }

                this.referenceToKeys.putReference(reference, mapped);
            } finally {
                this.locks.remove(reference);
            }
        });
    }

    @Override
    public Optional<T> findFirst(@NotNull K key) {
        return this.get(key).stream().findFirst();
    }

    @Override
    @Unmodifiable
    @NotNull
    public ReferencedCollection<T> get(@NotNull K key) {
        final IndexedReferences<K, T> indexedReferences = this.keyToReferences.get(key);
        if (indexedReferences == null) {
            return new ReferencedCollection<>(new ArrayList<>(), null);
        }

        return indexedReferences.getCollection();
    }

    @Override
    public String getName() {
        return this.name;
    }

    private Collection<K> comparableKeys(Collection<K> mapped) {
        return mapped.stream().map(this::toComparableKey).collect(Collectors.toList());
    }

    private K toComparableKey(Object key) {
        final ComparisonPolicy<K> comparisonPolicy = this.definition.getComparisonPolicy();
        if (key == null || !comparisonPolicy.supports(key.getClass())) {
            return null;
        }

        return comparisonPolicy.createComparable((K) key);
    }
}
