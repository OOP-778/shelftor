package net.manga.core.util.collection;

import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.manga.api.reference.ReferenceManager;
import net.manga.api.reference.ValueReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReferencedMap<K, V> implements Map<K, V> {
    private final Map<ValueReference<K>, V> map;
    private final ReferenceManager<K> referenceManager;

    public ReferencedMap(
        Map<ValueReference<K>, V> map,
        ReferenceManager<K> referenceManager
    ) {
        this.map = map;
        this.referenceManager = referenceManager;
        this.referenceManager.onReferenceRemove(this::removeReference);
    }

    public V removeReference(ValueReference<K> reference) {
        return this.map.remove(reference);
    }

    public V putReference(ValueReference<K> reference, V value) {
        return this.map.put(reference, value);
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.map.containsKey(this.referenceManager.createFetchingReference((K) key));
    }

    @Override
    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return this.map.get(this.referenceManager.createFetchingReference((K) key));
    }

    @Nullable
    @Override
    public V put(K key, V value) {
        return this.map.put(this.referenceManager.createReference(key), value);
    }

    @Override
    public V remove(Object key) {
        return this.map.remove(this.referenceManager.createFetchingReference((K) key));
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public Set<K> keySet() {
        return new AbstractSet<K>() {
            @Override
            public java.util.Iterator<K> iterator() {
                return new ReferencedIterator<K, ValueReference<K>>(ReferencedMap.this.map.keySet().iterator()) {
                    @Override
                    public K extractKey(ValueReference<K> u) {
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
        return this.map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new AbstractSet<Entry<K, V>>() {
            @Override
            public java.util.Iterator<Entry<K, V>> iterator() {
                return new ReferencedIterator<Entry<K, V>, Entry<ValueReference<K>, V>>(ReferencedMap.this.map.entrySet().iterator()) {

                    @Override
                    public Entry<K, V> extractKey(Entry<ValueReference<K>, V> u) {
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
