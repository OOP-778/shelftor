package dev.oop778.shelftor.core.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.oop778.shelftor.api.expiring.policy.implementation.TimedExpiringPolicy;
import dev.oop778.shelftor.api.query.Query;
import dev.oop778.shelftor.api.store.Shelf;
import dev.oop778.shelftor.api.store.expiring.ExpiringShelf;
import dev.oop778.shelftor.core.test.helper.TestBase;
import dev.oop778.shelftor.core.test.helper.data.Student;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

class ExpiringTest extends TestBase {

    @Test
    @SneakyThrows
    void automaticExpiration() {
        final Student student = Student.dummy();

        final Shelf<Student> store = Shelf.<Student>builder()
            .expiring()
            .usePolicy(TimedExpiringPolicy.create(100, TimeUnit.MILLISECONDS, true))
            .build();
        initBaseIndexes(store);

        store.add(student);

        TimeUnit.MILLISECONDS.sleep(200);

        final Collection<Student> id = store.get(Query.where("id", student.getId()));
        assertTrue(id.isEmpty(), "Store not empty");
    }

    @Test
    @SneakyThrows
    void manualExpiration() {
        final Student student = Student.dummy();

        final ExpiringShelf<Student> store = Shelf.<Student>builder()
            .expiring()
            .usePolicy(TimedExpiringPolicy.create(100, TimeUnit.MILLISECONDS, true))
            .build();
        initBaseIndexes(store);

        store.add(student);

        TimeUnit.MILLISECONDS.sleep(200);

        store.invalidate();

        assertTrue(store.isEmpty(), "Store not empty");
    }
}