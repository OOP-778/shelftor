package net.manga.core.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import net.manga.api.index.IndexDefinition;
import net.manga.api.index.StoreIndex;
import net.manga.api.index.comparison.ComparisonPolicy;
import net.manga.api.reference.ReferenceManager;
import net.manga.api.reference.EntryReference;
import net.manga.core.store.MangaCoreStore;
import net.manga.core.store.MangaStoreSettings;
import net.manga.core.util.OptionalLocking;
import net.manga.core.util.closeable.CloseableHolder;
import net.manga.core.util.collection.Collections;
import net.manga.core.util.collection.ReferencedCollection;
import net.manga.core.util.collection.ReferencedMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

@SuppressWarnings("unchecked")
public class MangaStoreIndex<T, K> extends CloseableHolder implements StoreIndex<T, K> {
    private final String name;
    private final IndexDefinition<K, T> definition;
    private final ReferenceManager<T> referenceManager;
    private final MangaStoreSettings settings;

    private final Map<K, IndexedReferences<K, T>> keyToReferences;
    private final ReferencedMap<T, Collection<K>> referenceToKeys;
    private final Map<EntryReference<T>, OptionalLocking> locks;

    public MangaStoreIndex(MangaCoreStore<T> store, String name, IndexDefinition<K, T> indexDefinition) {
        this.name = name;
        this.definition = indexDefinition;
        this.referenceManager = store.getReferenceManager();
        this.settings = store.getSettings();
        this.keyToReferences = this.settings.isConcurrent() ? new ConcurrentHashMap<>() : new HashMap<>();
        this.locks = this.settings.isConcurrent() ? new ConcurrentHashMap<>() : new HashMap<>();
        this.referenceToKeys = Collections.createReferencedMap(store.getSettings(), this.referenceManager);

        this.addCloseable(this.referenceManager.onReferenceRemove(this::removeReference));
    }

    @Override
    public void index(EntryReference<T> reference) {
        final OptionalLocking optionalLocking = this.locks.computeIfAbsent(reference, ($) -> new OptionalLocking(this.settings.isConcurrent()));

        optionalLocking.locking(() -> {
            try {
                final T value = reference.get();

                // Reference been removed ;)
                if (value == null) {
                    this.removeReferenceWithoutLocking(reference);
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
                    final boolean addedToIndex = this.keyToReferences
                        .computeIfAbsent(key, ($) -> new IndexedReferences<>(this.settings, this.referenceManager, key, this.definition))
                        .add(reference);

                    if (!addedToIndex) {
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
    public Optional<T> findFirst(K key) {
        return Optional.empty();
    }

    @Override
    @Unmodifiable
    @NotNull
    public ReferencedCollection<T> get(K key) {
        final IndexedReferences<K, T> indexedReferences = this.keyToReferences.get(key);
        if (indexedReferences == null) {
            return new ReferencedCollection<>(new ArrayList<>(), null);
        }

        return indexedReferences.getCollection();
    }

    private Collection<K> comparableKeys(Collection<K> mapped) {
        return mapped.stream().map(this::toComparableKey).collect(Collectors.toList());
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
    public String getName() {
        return this.name;
    }

    private K toComparableKey(Object key) {
        final ComparisonPolicy<K> comparisonPolicy = this.definition.getComparisonPolicy();
        if (key == null || !comparisonPolicy.supports(key.getClass())) {
            return null;
        }

        return comparisonPolicy.createComparable((K) key);
    }
}
