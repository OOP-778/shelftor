import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.SneakyThrows;
import net.manga.api.expiring.policy.implementation.TimedExpiringPolicy;
import net.manga.api.store.MangaStore;
import net.manga.api.store.expiring.ExpiringMangaStore;
import net.manga.core.MangaCore;
import net.manga.core.util.log.LogDebug;

public class Test {

    @SneakyThrows
    public static void main(String[] args) {
        new MangaCore();
        LogDebug.DEBUG = true;

        final ExpiringMangaStore<TestObject> build = MangaStore.<TestObject>builder()
            .expiring()
            .usePolicy(TimedExpiringPolicy.create(10, TimeUnit.MILLISECONDS, true))
            .build();
        build.add(new TestObject("test", "wg"));

        Thread.sleep(100);

        build.invalidate();
        //System.out.println(build.size());
    }

    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class TestObject {
        private String objectA;
        private String objectB;
    }
}
