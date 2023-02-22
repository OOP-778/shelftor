package dev.oop778.shelftor.core.test.helper.util;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {

    public static <T> T getRandom(List<T> collection) {
        return collection.get(ThreadLocalRandom.current().nextInt(0, collection.size() - 1));
    }
}
