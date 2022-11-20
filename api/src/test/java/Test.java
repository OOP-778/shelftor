import java.util.UUID;
import net.manga.api.store.MangaStore;
import net.manga.api.reference.ReferenceProvider;

public class Test {

    public static void main(String[] args) {
        final MangaStore<UUID> build = MangaStore.<UUID>builder()
            .hashable()
            .useReferenceProvider(ReferenceProvider.soft())
            .useWeakReferences()
            .build();
    }
}
