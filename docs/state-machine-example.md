# BioBuzz with a LinearOpMode and switch statement

This is the traditional `runOpMode()` pattern: read the routine during Init,
then repeatedly examine its current instruction inside
`while (opModeIsActive())`. A switch selects the behavior. A step index advances
only when that behavior's completion condition is true. No command framework,
action interface, or sequence-runner helper is needed.

Use the builder's **Load BioBuzz example** and **View JSON** to see the exact
[shared nine-step routine](../examples/routines/biobuzz-route.json) used here.
Install the library following [installation](library.md), then copy that JSON
file unchanged into your robot project's
`TeamCode/src/main/assets/biobuzz-route.json` (create the assets folder).
Create `QuanomousStateMachineOpMode.java` in
`TeamCode/src/main/java/org/firstinspires/ftc/teamcode/` with the following code.
No other example helper files are required.

The example runs without robot hardware: delay waits for real elapsed time;
the five hardware instruction types display their parameters and wait for a
fresh gamepad A press to simulate completion. Those completion conditions and
entry/exit comments are the places to substitute your team's robot logic.

```java
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
```

## How the state advances

The routine index is the outer state: delay, drive, deposit, intake, deposit,
intake, deposit, chase, then park. `entering` distinguishes the first loop of a
step from subsequent loops. Set motor targets and reset counters only on entry;
update controllers and examine sensors on every loop. Set `complete` when the
current instruction finishes, then increment the index once. Resetting the timer
on entry means a delay is measured from execution, not from JSON loading.

For more complex instructions, keep a second enum or switch for internal states.
For example, chase might move through SEEK, COLLECT and SCORE, increment a cycle
counter after SCORE, and complete only after the requested number of cycles.
The outer routine index stays on chase throughout those internal transitions.

Keep all cases nonblocking: do not put a long-running inner while loop or sleep
inside one case. That lets the outer loop continue updating telemetry, handling
Stop and running the team's normal controller updates. Finish each step's
outputs before advancing, and put robot-wide shutdown in the `finally` block.
Add your team's normal autonomous time limit when wiring in hardware.

## Read the BioBuzz parameters

The example displays the complete current JSON object. Real hardware cases read
its fields directly, for example:

```java
double tx = step.get("tx").getAsDouble();
double ty = step.get("ty").getAsDouble();
double heading = step.get("h").getAsDouble();
String axial = step.has("axial") ? step.get("axial").getAsString() : "center";
String lateral = step.has("lateral") ? step.get("lateral").getAsString() : "center";
double ao = step.has("ao") ? step.get("ao").getAsDouble() : 0;
double lo = step.has("lo") ? step.get("lo").getAsDouble() : 0;
```

Drive uses all seven values above. Intake uses `place` and `side`; deposit uses
`side`, `ao`, `lo`; chase uses integer `cycles`; park uses `side`; delay uses
`seconds`. Side/place strings in the builder output are lowercase. See the
[BioBuzz walkthrough](biobuzz-example.md) for coordinate units and alignment
semantics. This direct-switch example consumes current builder output; teams
needing legacy aliases/defaults can copy that handling from `JavaExample`.

For a scanned QR, replace the asset-read line with
`String json = Quanomous.INSTANCE.decode(barcodeText);` using barcode text from
your scanner during Init. For a saved routine, obtain the stored JSON array with
`Quanomous.INSTANCE.getStorage().load(filename)`. Keep the switch loop unchanged.
The library handles decoding/storage; this example deliberately interprets JSON
itself instead of constructing commands through `QuanomousCompiler`.

This is FTC integration code to copy into a robot project; the standalone
Java/Kotlin consumer builds do not compile the SDK-specific OpMode. The example
modules contain no tests. Core library and builder regression tests remain.
