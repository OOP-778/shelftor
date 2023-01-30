package net.manga.core.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import net.manga.api.store.MangaStore;
import net.manga.core.store.MangaCoreStore;
import net.manga.core.test.helper.TestBase;
import net.manga.core.test.helper.data.Student;
import org.junit.jupiter.api.Test;

class WeakValuesTest extends TestBase {

    @Test
    @SneakyThrows
    void testTimeOut() {
        Student student = Student.dummy();

        final MangaStore<Student> store = MangaStore.<Student>builder()
            .weak()
            .build();

        store.add(student);
        student = null;

        System.gc();

        TimeUnit.MILLISECONDS.sleep(10);

        assertTrue(store.isEmpty(), "Store not empty");
    }

    @Test
    @SneakyThrows
    void removeTest() {
        final Student student = new Student(1, "A", "B", 1, Collections.emptyList());

        final MangaStore<Student> store = MangaStore.<Student>builder()
            .weak()
            .build();

        System.out.println(store.size());
        store.remove(student);

        final MangaCoreStore<Student> store1 = (MangaCoreStore<Student>) store;

        assertTrue(store.isEmpty(), "Store not empty");
        assertTrue(store1.getReferenceManager().getReferenceMap().isEmpty(), "The reference map is not empty");
    }
}
