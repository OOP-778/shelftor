package net.manga.core.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import net.manga.api.expiring.policy.implementation.TimedExpiringPolicy;
import net.manga.api.query.Query;
import net.manga.api.store.MangaStore;
import net.manga.api.store.expiring.ExpiringMangaStore;
import net.manga.core.test.helper.TestBase;
import net.manga.core.test.helper.data.Student;
import org.junit.jupiter.api.Test;

class ExpiringTest extends TestBase {

    @Test
    @SneakyThrows
    void automaticExpiration() {
        final Student student = Student.dummy();

        final MangaStore<Student> store = MangaStore.<Student>builder()
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

        final ExpiringMangaStore<Student> store = MangaStore.<Student>builder()
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
