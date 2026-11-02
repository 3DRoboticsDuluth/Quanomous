package org.firstinspires.ftc.teamcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JavaExampleTest {
    @Test public void teamDefinedActionsAreConstructedBeforeExecution() {
        List<String> events = new ArrayList<>();
        List<Runnable> actions = JavaExample.compile(
            "[{\"cmd\":\"signal\",\"label\":\"first\"},{\"cmd\":\"signal\",\"label\":\"second\"}]", events);
        assertTrue(events.isEmpty());
        for (Runnable action : actions) action.run();
        assertEquals(Arrays.asList("first", "second"), events);
    }
}
