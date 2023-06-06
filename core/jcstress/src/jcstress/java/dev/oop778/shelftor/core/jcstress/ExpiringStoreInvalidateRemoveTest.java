package dev.oop778.shelftor.core.jcstress;

import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE;
import static org.openjdk.jcstress.annotations.Expect.FORBIDDEN;

import dev.oop778.shelftor.api.expiring.policy.implementation.TimedExpiringPolicy;
import dev.oop778.shelftor.api.shelf.Shelf;
import dev.oop778.shelftor.api.shelf.expiring.ExpiringShelf;
import dev.oop778.shelftor.core.CoreShelftor;
import dev.oop778.shelftor.core.shelf.expiring.CoreExpiringShelf;
import java.util.concurrent.TimeUnit;
import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.ZZ_Result;

@JCStressTest
@Outcome(id = "true, false", expect = ACCEPTABLE, desc = "Yes")
@Outcome(id = "false, true", expect = ACCEPTABLE, desc = "Yes")
@Outcome(id = "true, true", expect = FORBIDDEN, desc = "Both actors managed to remove?")
public class ExpiringStoreInvalidateRemoveTest {

    @State
    public static class StoreState {
        private final CoreExpiringShelf<TestObject> store;

        public StoreState() {
            new CoreShelftor();

            this.store = (CoreExpiringShelf<TestObject>) Shelf.<TestObject>builder()
                .hashable()
                .concurrent()
                .expiring()
                .usePolicy(TimedExpiringPolicy.create(0, TimeUnit.MILLISECONDS, true))
                .build();

            this.store.add(new TestObject("A", "B"));
        }
    }

    @Actor
    public void actor1(StoreState state, ZZ_Result result) {
        result.r1 = state.store.invalidate() == 1;
    }

    @Actor
    public void actor2(StoreState state, ZZ_Result result) {
        result.r2 = state.store.remove(new TestObject("A", "B"));
    }
}
