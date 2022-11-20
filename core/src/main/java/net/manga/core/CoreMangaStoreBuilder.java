package net.manga.core;

import java.lang.ref.Reference;
import java.util.Comparator;
import java.util.function.Function;
import lombok.NonNull;
import net.manga.api.store.MangaStore;
import net.manga.api.builder.StoreBuilder;
import net.manga.api.reference.ReferenceProvider;

public class CoreMangaStoreBuilder<T, B extends StoreBuilder<T, ?>> implements StoreBuilder<T, B> {
    protected Function<T, Reference<T>> referenceProvider = ReferenceProvider.soft();

    @Override
    public B useReferenceProvider(@NonNull Function<T, Reference<T>> referenceProvider) {
        this.referenceProvider = referenceProvider;
        return (B) this;
    }

    @Override
    public MangaStore<T> build() {
        return null;
    }
}
