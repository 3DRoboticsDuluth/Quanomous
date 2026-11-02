package org.firstinspires.ftc.teamcode;

import com.google.gson.JsonObject;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.threedrd.quanomous.CommandFactory;
import com.threedrd.quanomous.Quanomous;
import com.threedrd.quanomous.QuanomousCompiler;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.ftc.NextFTCOpMode;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** NextFTC 1.1.0 example. Replace simulated commands with your team's hardware commands. */
@Autonomous(name = "Quanomous BioBuzz Java commands")
public class QuanomousCommandOpMode extends NextFTCOpMode {
    private Command routine;
    private String current = "Ready";

    @Override public void onInit() {
        Map<String, CommandFactory<Command>> handlers = new HashMap<>();
        handlers.put("delay", step -> new Delay(step.get("seconds").getAsDouble()));
        handlers.put("drive", step -> simulated(step)); // tx, ty, h, axial, lateral, ao, lo
        handlers.put("intake", step -> simulated(step)); // place, side
        handlers.put("deposit", step -> simulated(step)); // side, ao, lo
        handlers.put("chase", step -> simulated(step)); // cycles
        handlers.put("park", step -> simulated(step)); // side
        QuanomousCompiler<Command> compiler = QuanomousCompiler.fromHandlers(handlers);
        try {
            String json = readRoutine();
            List<Command> commands = compiler.compile(Quanomous.INSTANCE.parse(json));
            routine = new SequentialGroup(commands.toArray(new Command[0]));
            telemetry.addLine("Ready: Start, then press A for each simulated hardware command");
        } catch (IOException | RuntimeException failure) {
            telemetry.addData("Cannot load routine", failure.getMessage());
        }
        telemetry.update();
    }

    @Override public void onStartButtonPressed() {
        if (routine != null) routine.schedule();
    }

    @Override public void onUpdate() {
        telemetry.addData("Command", current);
        telemetry.addLine("Hardware commands are simulated: release then press A to finish");
        telemetry.update();
    }

    @Override public void onStop() {
        if (routine != null) routine.cancel();
        // Real robot: also stop ALL drivetrain/mechanism outputs here.
    }

    private Command simulated(JsonObject step) {
        return new Command() {
            private boolean previousA;
            private boolean finished;
            @Override public void start() {
                previousA = gamepad1.a;
                finished = false;
                current = step.toString();
                // Real command: initialize targets/outputs using the instruction fields.
            }
            @Override public void update() {
                boolean pressed = gamepad1.a;
                finished = pressed && !previousA;
                previousA = pressed;
                // Real command: update control logic; set finished from sensors/controller state.
            }
            @Override public boolean isDone() { return finished; }
            @Override public void stop(boolean interrupted) {
                // Real command: release its outputs on completion or cancellation.
            }
        };
        // Hardware commands must declare their subsystem requirements with requires(...).
    }

    private String readRoutine() throws IOException {
        try (InputStream input = hardwareMap.appContext.getAssets().open("biobuzz-route.json");
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int count;
            while ((count = input.read(buffer)) != -1) output.write(buffer, 0, count);
            return output.toString("UTF-8");
        }
    }
}
