package net.manga.jmh;


import com.oop.memorystore.api.Store;
import com.oop.memorystore.implementation.memory.MemoryStore;
import com.oop.memorystore.implementation.query.Query;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.manga.core.MangaCore;
import net.manga.core.store.MangaCoreStore;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Measurement(time = 1)
@Warmup(time = 1, iterations = 3)
@Threads(50)
@Fork(1)
@State(org.openjdk.jmh.annotations.Scope.Benchmark)
public class ConcurrencyTest {
    private static final AtomicInteger integer = new AtomicInteger();
    private static final MangaState mangaState = new MangaState();
    private static final MemoryStoreState memoryState = new MemoryStoreState();

    @Setup
    public void setup() {
        new Thread(() -> {
            while (true) {

                mangaState.store.get(net.manga.api.query.Query.where("test", "test"));

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {

                memoryState.store.get(Query.where("test", "test"));

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    @Benchmark
    public void testManga() {
        mangaState.store.add(this.createTestObject());
    }

    @Benchmark
    public void testMemoryStore() {
        memoryState.store.add(this.createTestObject());
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

    public static class MangaState {
        private final MangaCoreStore<TestObject> store;

        public MangaState() {
            new MangaCore();

            this.store = MangaCoreStore.<TestObject>builder()
                .concurrent()
                .hashable()
                .build();

            this.store.index("test", TestObject::getObjectA);
        }
    }

    public static class MemoryStoreState {
        private final Store<TestObject> store = new MemoryStore<TestObject>().synchronizedStore();

        public MemoryStoreState() {
            this.store.index("test", TestObject::getObjectA);
        }
    }
}
