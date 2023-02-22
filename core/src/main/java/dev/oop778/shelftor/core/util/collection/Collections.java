package dev.oop778.shelftor.core.util.collection;

import dev.oop778.shelftor.core.shelf.CoreShelfSettings;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import dev.oop778.shelftor.api.reference.ReferenceManager;

public class Collections {

    // We return hashmaps becuase the reference deals with identity and hashable types
    public static <T> ReferencedCollection<T> createReferencedCollection(CoreShelfSettings settings, ReferenceManager<T> referenceManager) {
        if (settings.isConcurrent()) {
            return new ReferencedCollection<>(
                ConcurrentHashMap.newKeySet(),
                referenceManager
            );
        }

        return new ReferencedCollection<>(
            new HashSet<>(),
            referenceManager
        );
    }

    // We return hashmaps becuase the reference deals with identity and hashable types
    public static <K, V> ReferencedMap<K, V> createReferencedMap(CoreShelfSettings settings, ReferenceManager<K> referenceManager) {
        if (settings.isConcurrent()) {
            return new ReferencedMap<>(
                new ConcurrentHashMap<>(),
                referenceManager
            );
        }

        return new ReferencedMap<>(
            new HashMap<>(),
            referenceManager
        );
    }
}
