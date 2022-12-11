import java.util.Collection;
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

        build.index("test", TestObject::getObjectA);

        TestObject testObject = new TestObject("test", "testa");

        final boolean add = build.add(testObject);
        System.out.println(build.size());

        System.out.println(build.contains(testObject));

        final Collection<TestObject> testObjects = build.get("test", "test");
        System.out.printf("PRE GC: %s%n", testObjects.size());

        testObject = null;
        System.gc();

        System.out.println(String.format("POST GC: %s", testObjects.size()));
        while (true) {
            for (final TestObject object : testObjects) {
                System.out.println("Exists: " + object);
            }

            System.out.println(String.format("POST GC: %s", testObjects.size()));
            Thread.sleep(1);
        }
    }

    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class TestObject {
        private String objectA;
        private String objectB;
    }
}
