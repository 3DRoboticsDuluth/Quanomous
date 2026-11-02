package org.firstinspires.ftc.teamcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class JavaStateMachineTest {
    @Test public void waitsForCompletionAndStopsWithoutStartingLaterActions() {
        List<String> events = new ArrayList<>();
        boolean[] done = {false};
        JavaStateMachine.Action drive = new JavaStateMachine.Action() {
            public void start() { events.add("start"); }
            public boolean update() { events.add("update"); return done[0]; }
            public void stop() { events.add("stop"); }
        };
        JavaStateMachine sequence = new JavaStateMachine(Arrays.asList(drive,
            JavaStateMachine.once(() -> events.add("next"))));
        assertTrue(events.isEmpty());
        sequence.loop();
        sequence.loop();
        assertEquals(Arrays.asList("start", "update", "update"), events);
        done[0] = true;
        sequence.loop();
        assertEquals(Arrays.asList("start", "update", "update", "update", "stop"), events);
        assertFalse(sequence.isFinished());
        sequence.stop();
        sequence.loop();
        assertTrue(sequence.isFinished());
        assertFalse(events.contains("next"));
    }

    @Test public void completionRunsEachActionOnceAndCancellationCleansUpOnce() {
        List<String> events = new ArrayList<>();
        JavaStateMachine sequence = new JavaStateMachine(Arrays.asList(
            JavaStateMachine.delay(0), JavaStateMachine.once(() -> events.add("done"))));
        sequence.loop();
        assertTrue(events.isEmpty());
        sequence.loop();
        sequence.loop();
        assertEquals(Arrays.asList("done"), events);
        assertTrue(sequence.isFinished());
        JavaStateMachine active = new JavaStateMachine(Arrays.asList(new JavaStateMachine.Action() {
            public void start() { events.add("start"); }
            public boolean update() { return false; }
            public void stop() { events.add("stop"); }
        }));
        active.loop();
        active.stop();
        active.stop();
        active.loop();
        assertEquals(Arrays.asList("done", "start", "stop"), events);
    }
}
