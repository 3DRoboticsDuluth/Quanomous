# BioBuzz in a plain iterative OpMode

This example uses the same six BioBuzz instructions and the exact
[shared nine-step routine](../examples/routines/biobuzz-route.json) loaded by the
builder's **Load BioBuzz example** button. No command framework is required.
The wait uses real elapsed time. The five hardware action types are explicitly
simulated: telemetry shows their parameters and a fresh press of gamepad A marks
the active action complete. Nothing moves hardware.

## Copy and run

Install the library following [installation](library.md). Copy these files into
`TeamCode/src/main/java/org/firstinspires/ftc/teamcode/`:

- `JavaExample.java`: the same BioBuzz JSON-to-factory adapter used elsewhere.
- `JavaStateMachine.java`: the loop-driven sequence runner.
- `BioBuzzStateMachineDemo.java`: all six factories, with interactive hardware placeholders.

Find them in [the Java source folder](../examples/java/src/main/java/org/firstinspires/ftc/teamcode/).
Copy `examples/routines/biobuzz-route.json` unchanged into your robot project's
`TeamCode/src/main/assets/biobuzz-route.json` (create the assets folder if needed).
This packages the shared routine in the robot app; it is not a saved scan in
Quanomous storage. The builder can load the same file through its example button.

Create `QuanomousStateMachineOpMode.java` in the same TeamCode package:

```java
package org.firstinspires.ftc.teamcode;

import android.content.res.AssetManager;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Autonomous(name = "Quanomous BioBuzz state machine demo")
public class QuanomousStateMachineOpMode extends OpMode {
    private JavaStateMachine sequence;
    private String current = "Ready";
    private String error;

    @Override public void init() {
        try {
            JavaExample<JavaStateMachine.Action> adapter = new JavaExample<>(
                new BioBuzzStateMachineDemo(message -> current = message, () -> gamepad1.a));
            String json = readAsset(hardwareMap.appContext.getAssets(), "biobuzz-route.json");
            sequence = new JavaStateMachine(adapter.compile(json));
            telemetry.addLine("Ready: Start, then press A for each simulated action");
        } catch (IOException | RuntimeException failure) {
            error = "Cannot load BioBuzz routine: " + failure.getMessage();
            telemetry.addLine(error);
        }
    }

    @Override public void loop() {
        if (sequence == null) {
            telemetry.addLine(error);
        } else {
            sequence.loop();
            telemetry.addData("Action", current);
            telemetry.addData("Finished", sequence.isFinished());
        }
        telemetry.update();
    }

    @Override public void stop() {
        if (sequence != null) sequence.stop();
        // A real robot also applies its normal robot-wide output shutdown here.
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

After Init, nothing has executed. Press Start: the first step waits 0.4 seconds
without blocking the loop, then the drive target appears. Release and press A to
complete it. Repeat for deposit, intake, deposit, intake, deposit, chase and park.
Holding A never skips subsequent steps. After park completes, Finished stays true.
Stop prevents further actions from starting.

The OpMode above uses the FTC SDK supplied by your robot project. The independent
Java consumer compiles the adapter, demo factories and runner; its tests execute
the shared routine and verify completion/cancellation. The SDK-specific OpMode
itself is integration code to copy into an FTC project.

## Replace the simulated actions with robot behavior

Use `BioBuzzStateMachineDemo` as the shape of your own
`JavaExample.Auto<JavaStateMachine.Action>` implementation. Keep the real
nonblocking delay. For each other action, replace the gamepad completion logic:

- `start()` initializes the target and outputs once.
- `update()` runs a little control logic each OpMode loop and returns true only
  when the action is complete, according to sensors, elapsed time or controller state.
- `stop()` releases that action's outputs on completion or cancellation.

For drive, pass all pose/alignment/offset fields into your navigation logic;
for intake, deposit, chase and park, use your team's corresponding state logic.
The [BioBuzz walkthrough](biobuzz-example.md) explains units and conversions.
Do not sleep, busy-wait or wrap a motor-start call in `once`: that would either
block the loop or mark the action complete before the robot finishes.

For a routine from the actual builder QR, keep the same adapter and runner and
replace `adapter.compile(json)` with `adapter.decode(barcodeText)`, or use
`adapter.load(filename)` for a previously scanned/saved routine. Scan/select during
Init and call the runner only from `loop()` after Start, rather than repeatedly
starting a sequence in a camera callback.

The runner is single use. Create a new one for another execution. Keep the team's
normal autonomous time limit, runtime error handling and robot-wide shutdown in
the OpMode when adding hardware. No background threads or scheduler are involved.
