package dev.oop778.shelftor.core.test.helper;

import dev.oop778.shelftor.api.index.IndexDefinition;
import dev.oop778.shelftor.api.shelf.Shelf;
import dev.oop778.shelftor.core.test.helper.data.DataGenerator;
import dev.oop778.shelftor.core.test.helper.data.Student;
import dev.oop778.shelftor.core.util.log.LogDebug;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestBase {

    protected static final List<Student> RAW_STUDENTS;
    protected static final Shelf<Student> BASIC_STORE;
    protected static final Map<String, Map<Object, Integer>> DATA_COUNT;

    static {
        LogDebug.DEBUG = true;

        BASIC_STORE = Shelf.<Student>builder()
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

    protected static void initBaseIndexes(Shelf<Student> store) {
        store.index("id", Student::getId);
        store.index("grade", Student::getGrade);
        store.index("grades", IndexDefinition.withKeyMappings(Student::getGrades));
    }
}
