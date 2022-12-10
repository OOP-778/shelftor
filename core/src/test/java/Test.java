import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.SneakyThrows;
import net.manga.api.store.MangaStore;
import net.manga.core.MangaCore;

public class Test {

    @SneakyThrows
    public static void main(String[] args) {
        new MangaCore();

        final MangaStore<TestObject> build = MangaStore.<TestObject>builder()
            .weakKeys()
            .build();

        TestObject testObject = new TestObject("test", "testa");

        final boolean add = build.add(testObject);
        System.out.println(build.size());

        System.out.println(build.contains(testObject));

        testObject = null;
        System.gc();

        Thread.sleep(2000);
        System.out.println(build.size());
    }

    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class TestObject {
        private String objectA;
        private String objectB;
    }
}
