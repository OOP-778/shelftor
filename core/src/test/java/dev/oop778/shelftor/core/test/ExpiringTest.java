package dev.oop778.shelftor.core.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.oop778.shelftor.api.expiring.policy.implementation.TimedExpiringPolicy;
import dev.oop778.shelftor.api.query.Query;
import dev.oop778.shelftor.api.shelf.Shelf;
import dev.oop778.shelftor.api.shelf.expiring.ExpiringShelf;
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

        final ExpiringShelf<Student> store = Shelf.<Student>builder()
            .expiring()
            .usePolicy(TimedExpiringPolicy.create(100, TimeUnit.MILLISECONDS, true))
            .build();
        initBaseIndexes(store);
        store.onExpire((object) -> System.out.println("Expired: " + object));

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

    @Test
    @SneakyThrows
    void resettingTimedPolicyAndThenExpiring() {
        final Student student = Student.dummy();

        final ExpiringShelf<Student> store = Shelf.<Student>builder()
            .expiring()
            .usePolicy(TimedExpiringPolicy.create(50, TimeUnit.MILLISECONDS, true))
            .usePolicy(value -> true)
            .build();
        initBaseIndexes(store);

        store.add(student);

        TimeUnit.MILLISECONDS.sleep(30);
        final Student id = store.getFirst(Query.where("id", student.getId()));
        System.out.println(id);

        TimeUnit.MILLISECONDS.sleep(50);
        assertFalse(store.isEmpty(), "Store not supposed to be empty empty");

        TimeUnit.MILLISECONDS.sleep(90);
        final Student id2 = store.getFirst(Query.where("id", student.getId()));
        System.out.println(id2);
        assertTrue(store.isEmpty(), "Store not empty");
    }
}
