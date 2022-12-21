package net.manga.core.util.collection;

import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.manga.api.reference.ReferenceManager;
import net.manga.api.reference.EntryReference;
import net.manga.core.reference.CoreReferenceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReferencedMap<K, V> implements Map<K, V> {
    private final Map<EntryReference<K>, V> map;
    private final CoreReferenceManager<K> referenceManager;

    public ReferencedMap(
        Map<EntryReference<K>, V> map,
        ReferenceManager<K> referenceManager
    ) {
        this.map = map;
        this.referenceManager = (CoreReferenceManager<K>) referenceManager;
        this.referenceManager.onReferenceRemove(this::removeReference);
    }

    public V removeReference(EntryReference<K> reference) {
        return this.map.remove(reference);
    }

    public V putReference(EntryReference<K> reference, V value) {
        return this.map.put(reference, value);
    }

    @Override
    public int size() {
        this.referenceManager.runRemoveQueue();
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        this.referenceManager.runRemoveQueue();
        return this.map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        this.referenceManager.runRemoveQueue();
        return this.map.containsKey(this.referenceManager.createFetchingReference((K) key));
    }

    @Override
    public boolean containsValue(Object value) {
        this.referenceManager.runRemoveQueue();
        return this.map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        this.referenceManager.runRemoveQueue();
        return this.map.get(this.referenceManager.createFetchingReference((K) key));
    }

    @Nullable
    @Override
    public V put(K key, V value) {
        this.referenceManager.runRemoveQueue();
        return this.map.put(this.referenceManager.getOrCreateReference(key), value);
    }

    @Override
    public V remove(Object key) {
        this.referenceManager.runRemoveQueue();
        return this.map.remove(this.referenceManager.createFetchingReference((K) key));
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        for (final Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public Set<K> keySet() {
        this.referenceManager.runRemoveQueue();
        return new AbstractSet<K>() {
            @Override
            public java.util.Iterator<K> iterator() {
                return new ReferencedIterator<K, EntryReference<K>>(ReferencedMap.this.map.keySet().iterator()) {
                    @Override
                    public K extractKey(EntryReference<K> u) {
                        return u.get();
                    }
                };
            }

            @Override
            public boolean contains(Object o) {
                return ReferencedMap.this.map.containsKey(o);
            }

            @Override
            public int size() {
                return ReferencedMap.this.map.size();
            }
        };
    }

    @NotNull
    @Override
    public Collection<V> values() {
        this.referenceManager.runRemoveQueue();
        return this.map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        this.referenceManager.runRemoveQueue();
        return new AbstractSet<Entry<K, V>>() {
            @Override
            public java.util.Iterator<Entry<K, V>> iterator() {
                return new ReferencedIterator<Entry<K, V>, Entry<EntryReference<K>, V>>(ReferencedMap.this.map.entrySet().iterator()) {

                    @Override
                    public Entry<K, V> extractKey(Entry<EntryReference<K>, V> u) {
                        return new SimpleEntry<>(u.getKey().get(), u.getValue());
                    }
                };
            }

            @Override
            public int size() {
                return ReferencedMap.this.map.size();
            }
        };
    }
}
