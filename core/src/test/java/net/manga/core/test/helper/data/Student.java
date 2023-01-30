package net.manga.core.test.helper.data;

import static net.manga.core.test.helper.data.DataGenerator.FAKER;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Student {
    private int id;
    private String firstName;
    private String lastName;
    private int grade;
    private List<Integer> grades;

    public static Student dummy() {
        return new Student(
            ThreadLocalRandom.current().nextInt(10000),
            FAKER.name().firstName(),
            FAKER.name().lastName(),
            ThreadLocalRandom.current().nextInt(0, 10),
            IntStream.of(0, 10)
                .map(($) -> ThreadLocalRandom.current().nextInt(0, 10))
                .boxed()
                .collect(Collectors.toList())
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final Student student = (Student) o;
        return this.id == student.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }
}
