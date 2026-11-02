package org.firstinspires.ftc.teamcode;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Quanomous BioBuzz Java LinearOpMode")
public class QuanomousStateMachineOpMode extends LinearOpMode {
    @Override public void runOpMode() throws InterruptedException {
        JsonArray routine = readRoutine();
        if (routine == null) return;
        waitForStart();
        int index = 0;
        boolean entering = true;
        ElapsedTime timer = new ElapsedTime();
        while (opModeIsActive() && index < routine.size()) {
            JsonObject step = routine.get(index).getAsJsonObject();
            if (entering) timer.reset();
            boolean complete;
            switch (step.get("cmd").getAsString()) {
                case "delay": complete = timer.seconds() >= step.get("seconds").getAsDouble(); break;
                case "drive": complete = drive(step, entering); break;
                case "intake": complete = intake(step, entering); break;
                case "deposit": complete = deposit(step, entering); break;
                case "chase": complete = chase(step, entering); break;
                case "park": complete = park(step, entering); break;
                default: throw new IllegalArgumentException("Unknown instruction: " + step);
            }
            if (complete) index++;
            entering = complete;
            idle();
        }
    }

    // Team step methods: do one nonblocking update and return true when complete.
    // entering is true only on the first loop of this instruction. Read parameters from step.
    private boolean drive(JsonObject step, boolean entering) {
        throw new UnsupportedOperationException("Supply your team's drive behavior");
    }
    private boolean intake(JsonObject step, boolean entering) {
        throw new UnsupportedOperationException("Supply your team's intake behavior");
    }
    private boolean deposit(JsonObject step, boolean entering) {
        throw new UnsupportedOperationException("Supply your team's deposit behavior");
    }
    private boolean chase(JsonObject step, boolean entering) {
        throw new UnsupportedOperationException("Supply your team's chase behavior");
    }
    private boolean park(JsonObject step, boolean entering) {
        throw new UnsupportedOperationException("Supply your team's park behavior");
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
