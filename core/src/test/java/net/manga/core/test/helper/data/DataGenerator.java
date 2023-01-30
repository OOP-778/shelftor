package net.manga.core.test.helper.data;

import com.github.javafaker.Faker;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataGenerator {

    public static final Faker FAKER = new Faker();

    public static List<Student> generateData(int amount) {
        final List<Student> students = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            students.add(new Student(
                amount,
                FAKER.name().firstName(),
                FAKER.name().lastName(),
                ThreadLocalRandom.current().nextInt(0, 10),
                IntStream.of(0, 10)
                    .map(($) -> ThreadLocalRandom.current().nextInt(0, 10))
                    .boxed()
                    .collect(Collectors.toList())
            ));
        }

        return students;
    }
}
