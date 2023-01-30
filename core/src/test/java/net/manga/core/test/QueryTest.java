package net.manga.core.test;

import static net.manga.core.test.helper.util.RandomUtil.getRandom;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import net.manga.api.query.Query;
import net.manga.core.test.helper.TestBase;
import net.manga.core.test.helper.data.Student;
import org.junit.jupiter.api.Test;

class QueryTest extends TestBase {

    @Test
    void gradeQueryTest() {
        final Student student = getRandom(RAW_STUDENTS);
        final Collection<Student> grade = BASIC_STORE.get(Query.where("grade", student.getGrade()));

        assertTrue(grade.contains(student), "Not contains");
    }

    @Test
    void idAndGradeQueryTest() {
        final Student student = getRandom(RAW_STUDENTS);
        final Collection<Student> grade = BASIC_STORE.get(Query.where("grade", student.getGrade()).and(Query.where("id", student.getId())));

        assertTrue(grade.contains(student), "Not contains");
    }

    @Test
    void multiStudentQueryTest() {
        final Student studentA = getRandom(RAW_STUDENTS);
        final Student studentB = getRandom(RAW_STUDENTS);

        final Query query = Query.create()
            .or(Query.where("grade", studentA.getGrade()).and(Query.where("id", studentA.getId())))
            .or(Query.where("grade", studentB.getGrade()).and(Query.where("id", studentB.getId())));

        final Collection<Student> students = BASIC_STORE.get(query);

        assertTrue(students.contains(studentA), "Not contains student A");
        assertTrue(students.contains(studentB), "Not contains student B");
    }

    @Test
    void multiGradeQueryTest() {
        final Student randomStudent = getRandom(RAW_STUDENTS);
        final int randomGrade = getRandom(randomStudent.getGrades());

        final Query query = Query.where("grades", randomGrade);
        final Collection<Student> students = BASIC_STORE.get(query);

        assertTrue(students.contains(randomStudent), String.format("Not contains by grade %s", randomGrade));
    }
}
