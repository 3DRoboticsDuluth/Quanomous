package org.firstinspires.ftc.teamcode;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonParser;
import org.junit.Test;
import static org.junit.Assert.*;

public class BioBuzzStateMachineDemoTest {
    @Test public void sharedBuilderRoutineWaitsForEachActionAndNeverSkipsWhileAHeld() throws Exception {
        List<String> messages = new ArrayList<>();
        boolean[] pressed = {false};
        JavaExample<JavaStateMachine.Action> adapter = new JavaExample<>(
            new BioBuzzStateMachineDemo(messages::add, () -> pressed[0]));
        String json;
        try (InputStreamReader reader = new InputStreamReader(
                getClass().getResourceAsStream("/biobuzz-route.json"), StandardCharsets.UTF_8)) {
            json = JsonParser.parseReader(reader).toString();
        }
        List<JavaStateMachine.Action> actions = adapter.compile(json);
        assertEquals(9, actions.size());
        assertTrue(messages.isEmpty());
        // Test the real delay separately without imposing wall-clock timing on the test.
        actions.get(0).start();
        assertFalse(actions.get(0).update());
        actions.get(0).stop();
        JavaStateMachine sequence = new JavaStateMachine(actions.subList(1, actions.size()));
        for (int i = 0; i < 8; i++) {
            sequence.loop(); // starts active action; previous held press must not complete it
            assertEquals(i + 1, messages.size());
            sequence.loop();
            assertFalse(sequence.isFinished());
            pressed[0] = false;
            sequence.loop();
            pressed[0] = true;
            sequence.loop();
        }
        assertTrue(sequence.isFinished());
        sequence.loop();
        assertEquals(8, messages.size());
        assertTrue(messages.get(0).contains("x=1.0 y=-2.0"));
        assertTrue(messages.get(2).contains("GARDEN on RIGHT"));
        assertTrue(messages.get(3).contains("ao=0.5 lo=-0.25"));
        assertTrue(messages.get(6).startsWith("Chase 2 cycles"));
        assertTrue(messages.get(7).startsWith("Park RIGHT"));
    }
}
