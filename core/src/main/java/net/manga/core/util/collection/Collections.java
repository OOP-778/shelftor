package net.manga.core.util.collection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import net.manga.api.reference.ReferenceManager;
import net.manga.core.reference.CoreReferenceManager;
import net.manga.core.store.MangaStoreSettings;

public class Collections {

    // We return hashmaps becuase the reference deals with identity and hashable types
    public static <T> ReferencedCollection<T> createReferencedCollection(MangaStoreSettings settings, ReferenceManager<T> referenceManager) {
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
    public static <K, V> ReferencedMap<K, V> createReferencedMap(MangaStoreSettings settings, ReferenceManager<K> referenceManager) {
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
