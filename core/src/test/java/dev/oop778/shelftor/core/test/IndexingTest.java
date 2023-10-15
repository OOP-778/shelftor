package dev.oop778.shelftor.core.test;

import static org.junit.jupiter.api.Assertions.assertFalse;

import dev.oop778.shelftor.core.test.helper.TestBase;
import dev.oop778.shelftor.core.test.helper.data.Student;
import java.util.List;
import org.junit.jupiter.api.Test;

public class IndexingTest extends TestBase {

    @Test
    public void run() {
        final Student student = Student.dummy();
        BASIC_STORE.add(student);

        final List<Integer> grades = student.getGrades();
        final Integer grade = grades.get(0);

        grades.remove(grade);
        BASIC_STORE.reindex(student);

        assertFalse(BASIC_STORE.get("grades", grade).contains(student), "Still contains");
    }
}
