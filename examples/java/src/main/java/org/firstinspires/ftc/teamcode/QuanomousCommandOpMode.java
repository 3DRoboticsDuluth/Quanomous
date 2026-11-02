package org.firstinspires.ftc.teamcode;

import com.google.gson.JsonArray;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.threedrd.quanomous.CommandFactory;
import com.threedrd.quanomous.QuanomousCompiler;
import java.util.HashMap;
import java.util.List;

@Autonomous(name = "Quanomous BioBuzz Java commands")
public class QuanomousCommandOpMode extends LinearOpMode {
    @Override public void runOpMode() throws InterruptedException {
        JsonArray steps = readRoutine();
        Command routine = Command.sequence(QuanomousCompiler.fromHandlers(
            new HashMap<String, CommandFactory<Command>>() {{
                put("delay", step -> delay(step.get("seconds").getAsDouble()));
                put("drive", step -> drive(step.get("tx").getAsDouble(), step.get("ty").getAsDouble(), step.get("h").getAsDouble()));
                put("intake", step -> intake(step.get("place").getAsString(), step.get("side").getAsString()));
                put("deposit", step -> deposit(step.get("side").getAsString(), step.get("ao").getAsDouble(), step.get("lo").getAsDouble()));
                put("chase", step -> chase(step.get("cycles").getAsInt()));
                put("park", step -> park(step.get("side").getAsString()));
            }}
        ).compile(steps));
        waitForStart();
        routine.schedule();
    }

    /** Placeholder for your preferred command library: NextFTC, SolversLib, WPILib, etc.
     * Replace this type and its sequence/schedule calls with your library's equivalents. */
    private static class Command {
        static Command sequence(List<Command> commands) {
            throw new UnsupportedOperationException("Use your library's sequential command group");
        }

        void schedule() {
            throw new UnsupportedOperationException("Use your library's scheduler");
        }
    }

    // Replace these factory bodies with your team's commands; construct here, schedule above.
    private Command delay(double seconds) {
        throw new UnsupportedOperationException("Supply your team's delay command");
    }

    private Command drive(double tx, double ty, double h) {
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
            return new JsonArray();
        }
    }
}
