# BioBuzz with a LinearOpMode and switch statement

This is the traditional `runOpMode()` pattern: read the routine during Init,
then repeatedly examine its current instruction inside
`while (opModeIsActive())`. A Java switch or Kotlin when selects the behavior. A step index advances
only when that behavior's completion condition is true. No command framework,
action interface, or sequence-runner helper is needed.

Use the builder's **Load BioBuzz example** and **View JSON** to see the exact
[shared nine-step routine](../examples/java/src/main/assets/biobuzz-route.json) used here.
Install the library following [installation](library.md), then copy that JSON
file unchanged into your robot project's
`TeamCode/src/main/assets/biobuzz-route.json` (create the assets folder).
Choose one language. Copy [QuanomousStateMachineOpMode.java](../examples/java/src/main/java/org/firstinspires/ftc/teamcode/QuanomousStateMachineOpMode.java) into
`TeamCode/src/main/java/org/firstinspires/ftc/teamcode/`.
For Kotlin, copy [QuanomousStateMachineOpMode.kt](../examples/kotlin/src/main/java/org/firstinspires/ftc/teamcode/QuanomousStateMachineOpMode.kt) into that same package folder in a TeamCode project already configured for Kotlin. Use either language, not both: the alternatives intentionally have the same class name. No other example helper files are required.

Both versions use the JSON asset linked above. The Kotlin build includes the Java example's assets directory directly, so there is one canonical file rather than two copies.

The example runs without robot hardware: delay waits for real elapsed time;
the five hardware instruction types display their parameters and wait for a
fresh gamepad A press to simulate completion. Those completion conditions and
entry/exit comments are the places to substitute your team's robot logic.


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
In Kotlin, those alternatives are `val json = Quanomous.decode(barcodeText)`
and `val routine = Quanomous.storage.load(filename)`.
The library handles decoding/storage; this example deliberately interprets JSON
itself instead of constructing commands through `QuanomousCompiler`.

Both example builds compile their actual OpModes against FTC RobotCore 12.0.0
and package the shared JSON asset. The SDK is a compile-only dependency: your
FTC robot project supplies it. The example modules contain no tests; core library
and builder regression tests remain.
