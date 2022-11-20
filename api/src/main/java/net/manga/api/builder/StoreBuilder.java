package net.manga.api.builder;

import java.lang.ref.Reference;
import java.util.function.Function;
import lombok.NonNull;
import net.manga.api.store.MangaStore;
import net.manga.api.reference.ReferenceProvider;

public interface StoreBuilder<T, B extends StoreBuilder<T, ?>> {

    default B useWeakReferences() {
        return this.useReferenceProvider(ReferenceProvider.weak());
    }

    B useReferenceProvider(@NonNull Function<T, Reference<T>> referenceProvider);

    B hashable();

    ExpiringStoreBuilder<T, ?> expiring();

    <S extends MangaStore<T>> S build();
}
