package net.manga.api.reference;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.function.Function;

public interface ReferenceProvider {

    static <T> Function<T, Reference<T>> soft() {
        return SoftReference::new;
    }

    static <T> Function<T, Reference<T>> weak() {
        return WeakReference::new;
    }

}
