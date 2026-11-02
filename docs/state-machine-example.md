# Plain iterative OpMode / state-machine integration

Use this alternative when your team does not use a command framework. Copy
`JavaExample.java` and `JavaStateMachine.java` into your TeamCode package and
install the library as described in [installation](library.md). The runner is
example code, not a scheduler built into Quanomous.

The important difference from the reporting-only dry run is completion:
`start()` initializes an action once, `update()` does a little work on each
OpMode loop and returns true when done, and `stop()` releases that action's
outputs. The runner advances only after completion. Do not sleep, busy-wait or
run an entire drive operation inside any of these methods.

Here is a complete FTC OpMode for a timed wait followed by a telemetry message.
Create `QuanomousStateMachineOpMode.java` in the same TeamCode package. This uses
the FTC SDK supplied by your robot project; the independent consumer build tests
the runner, not the SDK-specific OpMode below.

```java
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.threedrd.quanomous.CommandFactory;
import com.threedrd.quanomous.Quanomous;
import com.threedrd.quanomous.QuanomousCompiler;
import java.util.HashMap;
import java.util.Map;

@Autonomous(name = "Quanomous state machine demo")
public class QuanomousStateMachineOpMode extends OpMode {
    private JavaStateMachine sequence;

    @Override public void init() {
        Map<String, CommandFactory<JavaStateMachine.Action>> handlers = new HashMap<>();
        handlers.put("delay", step -> JavaStateMachine.delay(step.get("seconds").getAsDouble()));
        handlers.put("report", step -> JavaStateMachine.once(() ->
            telemetry.addLine(step.get("text").getAsString())));
        QuanomousCompiler<JavaStateMachine.Action> compiler =
            QuanomousCompiler.fromHandlers(handlers);
        String json = "[{\"cmd\":\"delay\",\"seconds\":0.4},"
            + "{\"cmd\":\"report\",\"text\":\"Wait finished\"}]";
        sequence = new JavaStateMachine(compiler.compile(Quanomous.INSTANCE.parse(json)));
        telemetry.addLine("Ready; actions begin after Start");
    }

    @Override public void loop() {
        sequence.loop();
        telemetry.addData("Sequence finished", sequence.isFinished());
        telemetry.update();
    }

    @Override public void stop() {
        if (sequence != null) sequence.stop();
        // Also apply your team's normal robot-wide stop of all outputs.
    }
}
```

`report` is a custom demonstration instruction; the BioBuzz builder does not
emit it. This small routine deliberately shows that the library accepts a team's
own instruction map. It waits for real elapsed time without blocking the loop.
The reporting-only `JavaDryRun` instead reports a delay immediately.

## Use the BioBuzz builder's six instructions

For the actual builder routine, use `new JavaExample<JavaStateMachine.Action>(auto)`
where `auto` implements `JavaExample.Auto<JavaStateMachine.Action>`. Then pass
`adapter.compile(json)`, `adapter.decode(barcodeText)` or `adapter.load(filename)`
to `new JavaStateMachine(...)`. Use the shared
[BioBuzz routine](../examples/routines/biobuzz-route.json) unchanged. `delay`
returns `JavaStateMachine.delay(seconds)`; the other five factories return your
robot's stateful actions. This uses the same fields and conversions described in
the [BioBuzz walkthrough](biobuzz-example.md).

For example, a drive factory should return an action whose `start()` sets its
target, whose `update()` updates the team's controller and returns its
at-target result, and whose `stop()` stops the drivetrain. Intake, deposit,
chase and park likewise finish according to sensors, elapsed time or the team's
state logic. Do not wrap a method that merely starts a motor in `once`: that
would mark it complete immediately and begin the next step on the next loop.
`once` is appropriate for instantaneous changes or reporting only.

Compile/select during Init, then call the runner only from `loop()` after Start.
Stopping cancels the current action and prevents later ones from starting; a
completed sequence stays complete. Create a new runner for another execution.
Keep your usual global autonomous time limit, error handling and robot-wide
output shutdown in the OpMode. These helpers add no background thread and do
not know your hardware or navigation implementation.
