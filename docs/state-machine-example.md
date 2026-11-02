# BioBuzz with a LinearOpMode and switch statement

This is the traditional `runOpMode()` pattern: read the routine during Init,
then repeatedly examine its current instruction inside
`while (opModeIsActive())`. A Java switch or Kotlin when selects the behavior. An iterator advances
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

Implement the private step methods with your team's behavior. Each receives the
current JSON instruction and returns true when complete. The loop uses an iterator
to advance only after completion; it needs no index, entry flag or example timer.
Delay is also a team method, so the example imposes no timing implementation.
Keep each call nonblocking so the outer loop can continue checking OpMode activity.

## Read the BioBuzz parameters

Each private step method receives the current JSON object. Read its fields
directly, for example:

```java
double tx = step.get("tx").getAsDouble();
double ty = step.get("ty").getAsDouble();
double heading = step.get("h").getAsDouble();
```

This is the same minimal drive mapping used by the command examples. The builder
also emits axial/lateral alignment and ao/lo offsets, available in the JSON if
your navigation code needs them. Intake uses `place` and `side`; deposit uses
`side`, `ao`, `lo`; chase uses integer `cycles`; park uses `side`; delay uses
`seconds`. Side/place strings in the builder output are lowercase. See the
[BioBuzz walkthrough](biobuzz-example.md) for coordinate units and alignment
semantics. This direct-switch example consumes current builder output; teams
needing legacy aliases/defaults can add that handling in their switch cases.

For a scanned QR, replace the body of readRoutine() with decoding/parsing of
barcode text provided during Init: `Quanomous.INSTANCE.parse(Quanomous.INSTANCE.decode(barcodeText))`
in Java or `Quanomous.parse(Quanomous.decode(barcodeText))` in Kotlin.
For saved routines use `Quanomous.INSTANCE.load(filename)` or
`Quanomous.load(filename)`. Each returns the array consumed by the same loop.

File/parsing errors currently return an empty array, making the loop empty.
Add your team's error reporting policy where marked. The library handles
JSON decoding/storage; these examples deliberately interpret JSON directly
instead of constructing commands through QuanomousCompiler.

Both example builds compile against FTC RobotCore 12.0.0 and package the shared JSON asset. The SDK is a compile-only dependency: your
FTC robot project supplies it. The example modules contain no tests; core library
and builder regression tests remain.
