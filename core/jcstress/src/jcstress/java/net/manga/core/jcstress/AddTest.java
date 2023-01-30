package net.manga.core.jcstress;

import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE;

import net.manga.api.store.MangaStore;
import net.manga.core.MangaCore;
import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.ZZ_Result;

@Outcome(id = "true, false", expect = ACCEPTABLE, desc = "Yes")
@Outcome(id = "false, true", expect = ACCEPTABLE, desc = "Yes")
public class AddTest {

    @State
    public static class StoreState {
        private final MangaStore<TestObject> store;

        public StoreState() {
            new MangaCore();

            this.store = MangaStore.<TestObject>builder()
                .concurrent()
                .hashable()
                .build();

            this.store.index("test", TestObject::getObjectB);
        }
    }

    @Actor
    public void actor1(StoreState state, ZZ_Result result) {
        result.r1 = state.store.add(new TestObject("A", "B"));
    }

    @Actor
    public void actor2(StoreState state, ZZ_Result result) {
        result.r2 = state.store.add(new TestObject("A", "B"));
    }
}
