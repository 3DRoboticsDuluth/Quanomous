package org.firstinspires.ftc.teamcode;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.threedrd.quanomous.Quanomous;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class JavaExampleTest {
    private final List<String> events = new ArrayList<>();
    private final JavaExample<Runnable> adapter = new JavaExample<>(new RecordingAuto());

    @After public void resetDecoder() {
        Quanomous.INSTANCE.setDecoder(data -> android.util.Base64.decode(data, android.util.Base64.NO_WRAP));
    }

    @Test public void builderQrMapsEveryBioBuzzFieldWithoutExecutingCommands() throws Exception {
        JsonObject fixture = resource("biobuzz.json").getAsJsonObject();
        Quanomous.INSTANCE.setDecoder(data -> Base64.getDecoder().decode(data));
        List<Runnable> commands = adapter.decode(fixture.get("encoded").getAsString());
        assertTrue(events.isEmpty());
        commands.forEach(Runnable::run);
        assertEquals(Arrays.asList(
            "delay:0.4", "chase:2", "intake:LOADING_ZONE:RIGHT",
            "deposit:LEFT:0.5:-0.25", "park:RIGHT",
            "drive:1.0:-2.0:90.0:front:left:0.25:-0.5"), events);
    }

    @Test public void compilesTheIllustrativeBioBuzzRoute() throws Exception {
        List<Runnable> commands = adapter.compile(resource("biobuzz-route.json").toString());
        assertTrue(events.isEmpty());
        commands.forEach(Runnable::run);
        List<String> names = new ArrayList<>();
        for (String event : events) names.add(event.substring(0, event.indexOf(':')));
        assertEquals(Arrays.asList("delay", "drive", "deposit", "intake", "deposit", "intake", "deposit", "chase", "park"), names);
    }

    @Test public void preservesDepositAliasesZeroPrecedenceAndSignedDriveDefaults() {
        List<Runnable> commands = adapter.compile("[{\"cmd\":\"deposit\",\"side\":\"LEFT\",\"ao\":0,\"txo\":9,\"tyo\":2},"
            + "{\"cmd\":\"drive\",\"tx\":-1,\"ty\":2,\"h\":-90}]");
        commands.forEach(Runnable::run);
        assertEquals(Arrays.asList("deposit:LEFT:0.0:2.0", "drive:-1.0:2.0:-90.0:center:center:0.0:0.0"), events);
        assertThrows(IllegalArgumentException.class, () -> adapter.compile("[{\"cmd\":\"release\"}]"));
    }

    private JsonElement resource(String name) throws Exception {
        try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("/" + name), StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader);
        }
    }

    private class RecordingAuto implements JavaExample.Auto<Runnable> {
        private Runnable record(String value) { return () -> events.add(value); }
        public Runnable delay(double seconds) { return record("delay:" + seconds); }
        public Runnable chase(int cycles) { return record("chase:" + cycles); }
        public Runnable intake(JavaExample.Place place, JavaExample.Side side) { return record("intake:" + place + ":" + side); }
        public Runnable deposit(JavaExample.Side side, double ao, double lo) { return record("deposit:" + side + ":" + ao + ":" + lo); }
        public Runnable park(JavaExample.Side side) { return record("park:" + side); }
        public Runnable drive(double tx, double ty, double h, String axial, String lateral, double ao, double lo) {
            return record("drive:" + tx + ":" + ty + ":" + h + ":" + axial + ":" + lateral + ":" + ao + ":" + lo);
        }
    }
}
