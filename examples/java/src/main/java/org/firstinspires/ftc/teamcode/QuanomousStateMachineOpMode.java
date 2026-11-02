package org.firstinspires.ftc.teamcode;

import android.content.res.AssetManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.threedrd.quanomous.Quanomous;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Autonomous(name = "Quanomous BioBuzz switch demo")
public class QuanomousStateMachineOpMode extends LinearOpMode {
    @Override public void runOpMode() throws InterruptedException {
        JsonArray routine;
        try {
            String json = readAsset(hardwareMap.appContext.getAssets(), "biobuzz-route.json");
            routine = Quanomous.INSTANCE.parse(json);
        } catch (IOException | RuntimeException failure) {
            telemetry.addData("Cannot load routine", failure.getMessage());
            telemetry.update();
            return;
        }

        telemetry.addLine("Ready: Start, then press A for each simulated hardware step");
        telemetry.update();
        waitForStart();
        if (isStopRequested()) return;

        int stepIndex = 0;
        boolean entering = true;
        boolean previousA = gamepad1.a;
        ElapsedTime stepTime = new ElapsedTime();
        try {
            while (opModeIsActive() && stepIndex < routine.size()) {
                JsonObject step = routine.get(stepIndex).getAsJsonObject();
                String command = step.get("cmd").getAsString();
                boolean pressedA = gamepad1.a;
                boolean freshA = pressedA && !previousA;
                previousA = pressedA;
                boolean complete = false;
                if (entering) {
                    stepTime.reset();
                    // A held A button must not immediately complete a new step.
                    freshA = false;
                }

                switch (command) {
                    case "delay":
                        complete = stepTime.seconds() >= step.get("seconds").getAsDouble();
                        break;
                    case "drive":
                        if (entering) {
                            // Set target once using tx, ty, h, axial, lateral, ao and lo.
                        }
                        // Update your drive controller each loop.
                        complete = freshA; // DEMO: replace with drive-at-target condition.
                        if (complete) {
                            // Stop drivetrain outputs before advancing.
                        }
                        break;
                    case "intake":
                        if (entering) {
                            // Begin intake using place and side.
                        }
                        // Update intake/navigation state each loop.
                        complete = freshA; // DEMO: replace with piece-acquired condition.
                        if (complete) {
                            // Finish intake and release this step's outputs.
                        }
                        break;
                    case "deposit":
                        if (entering) {
                            // Begin deposit using side, ao and lo.
                        }
                        // Update scoring mechanism state each loop.
                        complete = freshA; // DEMO: replace with deposit-finished condition.
                        if (complete) {
                            // Finish scoring and release this step's outputs.
                        }
                        break;
                    case "chase":
                        if (entering) {
                            // Reset completed-cycle count; target is step.get("cycles").getAsInt().
                        }
                        // Update your collection/scoring cycle state each loop.
                        complete = freshA; // DEMO: replace with completedCycles >= targetCycles.
                        if (complete) {
                            // Stop cycle outputs.
                        }
                        break;
                    case "park":
                        if (entering) {
                            // Set your park target using side.
                        }
                        // Update navigation each loop.
                        complete = freshA; // DEMO: replace with parked condition.
                        if (complete) {
                            // Stop drivetrain outputs.
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown BioBuzz instruction: " + command);
                }

                telemetry.addData("Step", "%d / %d", stepIndex + 1, routine.size());
                telemetry.addData("Instruction and parameters", step.toString());
                telemetry.addData("Step seconds", stepTime.seconds());
                telemetry.addLine("Hardware steps are simulated: release then press A to finish");
                telemetry.update();
                if (complete) {
                    stepIndex++;
                    entering = true;
                } else {
                    entering = false;
                }
                idle();
            }
        } finally {
            // Real robot: stop ALL drivetrain/mechanism outputs here, including on Stop/error.
        }
    }

    private static String readAsset(AssetManager assets, String name) throws IOException {
        try (InputStream input = assets.open(name);
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int count;
            while ((count = input.read(buffer)) != -1) output.write(buffer, 0, count);
            return output.toString("UTF-8");
        }
    }
}
