package net.manga.jmh;


import com.oop.memorystore.api.Store;
import com.oop.memorystore.implementation.memory.MemoryStore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.manga.core.store.MangaCoreStore;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

public class ConcurrencyTest {
    private static final AtomicInteger integer = new AtomicInteger();

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Measurement(time = 1)
    @Warmup(time = 1, iterations = 1)
    @Threads(20)
    @Fork(1)
    public void testManga(MangaState state) {
        state.store.add(this.createTestObject());
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Warmup(time = 1, iterations = 1)
    @Fork(1)
    @Measurement(time = 1)
    @Threads(20)
    public void testMemoryStore(MemoryStoreState state) {
        state.store.add(this.createTestObject());
    }

    private TestObject createTestObject() {
        return new TestObject(
            integer.incrementAndGet() + "",
            integer.incrementAndGet() + ""
        );
    }

    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class TestObject {
        private String objectA;
        private String objectB;
    }

    @State(Scope.Benchmark)
    public static class MangaState {
        private final MangaCoreStore<TestObject> store = MangaCoreStore.<TestObject>builder()
            .concurrent()
            .build();
    }


    @State(Scope.Benchmark)
    public static class MemoryStoreState {
        private final Store<TestObject> store = new MemoryStore<TestObject>()
            .synchronizedStore();
    }
}
