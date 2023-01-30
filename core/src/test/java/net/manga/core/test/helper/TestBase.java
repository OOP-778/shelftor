package net.manga.core.test.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.manga.api.index.IndexDefinition;
import net.manga.api.store.MangaStore;
import net.manga.core.MangaCore;
import net.manga.core.test.helper.data.DataGenerator;
import net.manga.core.test.helper.data.Student;
import net.manga.core.util.log.LogDebug;

public class TestBase {

    protected static final List<Student> RAW_STUDENTS;
    protected static final MangaStore<Student> BASIC_STORE;
    protected static final Map<String, Map<Object, Integer>> DATA_COUNT;

    static {
        new MangaCore();
        LogDebug.DEBUG = true;

        BASIC_STORE = MangaStore.<Student>builder()
            .build();

        initBaseIndexes(BASIC_STORE);

        RAW_STUDENTS = DataGenerator.generateData(2);
        RAW_STUDENTS.forEach(BASIC_STORE::add);

        DATA_COUNT = new HashMap<>();

        for (final Student rawStudent : RAW_STUDENTS) {
            DATA_COUNT
                .computeIfAbsent("grade", ($) -> new HashMap<>())
                .merge(rawStudent.getGrade(), 1, Integer::sum);
        }
    }

    protected static void initBaseIndexes(MangaStore<Student> store) {
        store.index("id", Student::getId);
        store.index("grade", Student::getGrade);
        store.index("grades", IndexDefinition.withKeyMappings(Student::getGrades));
    }
}
