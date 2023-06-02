package dev.oop778.shelftor.core.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.oop778.shelftor.api.shelf.Shelf;
import dev.oop778.shelftor.core.shelf.CoreShelf;
import dev.oop778.shelftor.core.test.helper.TestBase;
import dev.oop778.shelftor.core.test.helper.data.Student;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

class WeakValuesTest extends TestBase {

    @Test
    @SneakyThrows
    void testTimeOut() {
        Student student = Student.dummy();

        final Shelf<Student> store = Shelf.<Student>builder()
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

        final Shelf<Student> store = Shelf.<Student>builder()
            .weak()
            .build();

        System.out.println(store.size());
        store.remove(student);

        assertTrue(store.isEmpty(), "Store not empty");
        assertTrue(((CoreShelf) store).getReferenceManager().getReferenceMap().isEmpty(), "The reference map is not empty");
    }
}
