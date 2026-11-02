package org.firstinspires.ftc.teamcode;

import com.google.gson.JsonArray;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.threedrd.quanomous.CommandFactory;
import com.threedrd.quanomous.QuanomousCompiler;
// Use your preferred command library (NextFTC, SolversLib, WPILib, etc.).
// This compiling example uses NextFTC 1.1.0's Command, group and OpMode types.
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.ftc.NextFTCOpMode;
import java.util.HashMap;

@Autonomous(name = "Quanomous BioBuzz Java commands")
public class QuanomousCommandOpMode extends NextFTCOpMode {
    private Command routine;

    @Override public void onInit() {
        JsonArray steps = readRoutine();
        if (steps == null) return;
        routine = new SequentialGroup(QuanomousCompiler.fromHandlers(
            new HashMap<String, CommandFactory<Command>>() {{
                put("delay", step -> delay(step.get("seconds").getAsDouble()));
                put("drive", step -> drive(step.get("tx").getAsDouble(), step.get("ty").getAsDouble(), step.get("h").getAsDouble(), step.get("axial").getAsString(), step.get("lateral").getAsString(), step.get("ao").getAsDouble(), step.get("lo").getAsDouble()));
                put("intake", step -> intake(step.get("place").getAsString(), step.get("side").getAsString()));
                put("deposit", step -> deposit(step.get("side").getAsString(), step.get("ao").getAsDouble(), step.get("lo").getAsDouble()));
                put("chase", step -> chase(step.get("cycles").getAsInt()));
                put("park", step -> park(step.get("side").getAsString()));
            }}
        ).compile(steps).toArray(new Command[0]));
    }

    @Override public void onStartButtonPressed() {
        if (routine != null) routine.schedule();
    }

    // Replace these factory bodies with your team's commands; construct here, schedule above.
    private Command delay(double seconds) {
        return new Delay(seconds);
    }
    private Command drive(double tx, double ty, double h, String axial, String lateral, double ao, double lo) {
        throw new UnsupportedOperationException("Supply your team's drive command");
    }
    private Command intake(String place, String side) {
        throw new UnsupportedOperationException("Supply your team's intake command");
    }
    private Command deposit(String side, double ao, double lo) {
        throw new UnsupportedOperationException("Supply your team's deposit command");
    }
    private Command chase(int cycles) {
        throw new UnsupportedOperationException("Supply your team's chase command");
    }
    private Command park(String side) {
        throw new UnsupportedOperationException("Supply your team's park command");
    }

    private JsonArray readRoutine() {
        try (java.io.InputStream input = hardwareMap.appContext.getAssets().open("biobuzz-route.json");
             java.io.Reader reader = new java.io.InputStreamReader(input, java.nio.charset.StandardCharsets.UTF_8)) {
            return new com.google.gson.JsonParser().parse(reader).getAsJsonArray();
        } catch (java.io.IOException | RuntimeException failure) {
            // Add your team's load-error handling here; do not start a partial routine.
            return null;
        }
    }
}
