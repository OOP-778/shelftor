package net.manga.core.index;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import net.manga.api.index.IndexDefinition;
import net.manga.api.index.StoreIndex;
import net.manga.api.index.comparison.ComparisonPolicy;
import net.manga.api.reference.ReferenceManager;
import net.manga.api.reference.ValueReference;
import net.manga.core.store.MangaCoreStore;
import net.manga.core.store.MangaStoreSettings;
import net.manga.core.util.OptionalLocking;
import net.manga.core.util.collection.Collections;
import net.manga.core.util.collection.ReferencedMap;

@SuppressWarnings("unchecked")
public class MangaStoreIndex<T, K> implements StoreIndex<T> {
    private final String name;
    private final IndexDefinition<K, T> definition;
    private final ReferenceManager<T> referenceManager;
    private final MangaStoreSettings settings;

    private final Map<K, IndexedReferences<K, T>> referencesMap;
    private final ReferencedMap<T, Collection<K>> referenceToKeys;
    private final Map<ValueReference<T>, OptionalLocking> locks;

    public MangaStoreIndex(MangaCoreStore<T> store, String name, IndexDefinition<K, T> indexDefinition) {
        this.name = name;
        this.definition = indexDefinition;
        this.referenceManager = store.getReferenceManager();
        this.settings = store.getSettings();
        this.referencesMap = this.settings.isConcurrent() ? new ConcurrentHashMap<>() : new HashMap<>();
        this.locks = this.settings.isConcurrent() ? new ConcurrentHashMap<>() : new HashMap<>();
        this.referenceToKeys = Collections.createReferencedMap(store.getSettings(), this.referenceManager);
        this.referenceManager.onReferenceRemove(this::removeReference);
    }

    @Override
    public void index(ValueReference<T> reference) {
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
                                             ? this.definition.getKeyMapper().map(value)
                                             : this.comparableKeys(this.definition.getKeyMapper().map(value));
                if (mapped.isEmpty()) {
                    this.removeReferenceWithoutLocking(reference);
                    return;
                }

                final Iterator<K> iterator = mapped.iterator();
                while (iterator.hasNext()) {
                    final K key = iterator.next();
                    final boolean addedToIndex = this.referencesMap
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

    private Collection<K> comparableKeys(Collection<K> mapped) {
        return mapped.stream().map(this::toComparableKey).collect(Collectors.toList());
    }

    private void removeReference(ValueReference<T> reference) {
        final OptionalLocking optionalLocking = this.locks.computeIfAbsent(reference, r -> new OptionalLocking(this.settings.isConcurrent()));

        optionalLocking.locking(() -> {
            try {
                this.removeReferenceWithoutLocking(reference);
            } finally {
                this.locks.remove(reference);
            }
        });
    }

    private void removeReferenceWithoutLocking(ValueReference<T> reference) {
        final Collection<K> keys = this.referenceToKeys.removeReference(reference);
        if (keys == null) {
            return;
        }

        for (final K key : keys) {
            final IndexedReferences<K, T> indexedReferences = this.referencesMap.get(key);
            if (indexedReferences == null) {
                continue;
            }

            indexedReferences.remove(reference);
        }
    }

    @Override
    public Optional<T> findFirst(Object key) {
        return Optional.empty();
    }

    @Override
    public List<T> get(Object key) {
        return null;
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
