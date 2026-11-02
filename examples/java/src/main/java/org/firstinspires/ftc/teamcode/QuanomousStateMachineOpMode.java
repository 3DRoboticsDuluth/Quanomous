package org.firstinspires.ftc.teamcode;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import java.util.Iterator;

@Autonomous(name = "Quanomous BioBuzz Java LinearOpMode")
public class QuanomousStateMachineOpMode extends LinearOpMode {
    @Override public void runOpMode() throws InterruptedException {
        JsonArray routine = readRoutine();
        Iterator<JsonElement> steps = routine.iterator();
        waitForStart();
        JsonObject step = steps.hasNext() ? steps.next().getAsJsonObject() : null;
        while (opModeIsActive() && step != null) {
            boolean complete;
            switch (step.get("cmd").getAsString()) {
                case "delay": complete = delay(step); break;
                case "drive": complete = drive(step); break;
                case "intake": complete = intake(step); break;
                case "deposit": complete = deposit(step); break;
                case "chase": complete = chase(step); break;
                case "park": complete = park(step); break;
                default: throw new IllegalArgumentException("Unknown instruction: " + step);
            }
            if (complete) step = steps.hasNext() ? steps.next().getAsJsonObject() : null;
            idle();
        }
    }

    // Team step methods: do one nonblocking update and return true when complete.
    private boolean delay(JsonObject step) {
        throw new UnsupportedOperationException("Supply your team's delay behavior");
    }

    private boolean drive(JsonObject step) {
        throw new UnsupportedOperationException("Supply your team's drive behavior");
    }

    private boolean intake(JsonObject step) {
        throw new UnsupportedOperationException("Supply your team's intake behavior");
    }

    private boolean deposit(JsonObject step) {
        throw new UnsupportedOperationException("Supply your team's deposit behavior");
    }

    private boolean chase(JsonObject step) {
        throw new UnsupportedOperationException("Supply your team's chase behavior");
    }

    private boolean park(JsonObject step) {
        throw new UnsupportedOperationException("Supply your team's park behavior");
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
