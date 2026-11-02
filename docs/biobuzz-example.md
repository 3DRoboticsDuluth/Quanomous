# Devil Dogs BioBuzz example

The Java and Kotlin adapters follow the six instructions in Devil Dogs' local
BioBuzz `subsystems.Quanomous` adapter. This is a real team-shaped command
contract, offered as a starting point; teams can change commands, parameters and
robot behavior. The generic Quanomous library contains none of these definitions.

## Example routine

Start the local builder as described in the [README](../readme.md). Click
**Load BioBuzz example**, confirm replacement, and click **View JSON**. This loads
the exact file below; no manual reconstruction of the nine blocks is needed.
The public hosted builder remains the older version until this draft is deployed.

[biobuzz-route.json](../examples/routines/biobuzz-route.json) illustrates:

1. Wait 0.4 seconds for an alliance partner to clear.
2. Drive to a signed field pose.
3. Deposit preloaded pieces on the right.
4. Intake from the garden, then deposit on the left with a small offset.
5. Intake from the left flower, then deposit again.
6. Chase for two cycles, then park on the right.

This is an illustrative composition of the team's commands, not their measured
competition route. Coordinates, offsets, duration and physical clearance must be
chosen and checked on the team's robot. The routine starts by scoring preloads
before collection and finishes with park; the fixture used for field-coverage
testing separately exercises every parameter with nondefault values.

## First try without robot hardware

Install the local library and copy the two helper files for your chosen language
as described in the [examples README](../examples/README.md). Inside your existing
OpMode, this Java smoke test reports one delay to Driver Station telemetry:

```java
JavaExample<Runnable> adapter = JavaDryRun.create(message -> telemetry.addLine(message));
adapter.compile("[{\"cmd\":\"delay\",\"seconds\":0.4}]").forEach(Runnable::run);
telemetry.update();
```

The Kotlin equivalent is:

```kotlin
val adapter = bioBuzzDryRun { telemetry.addLine(it) }
adapter.compile("""[{"cmd":"delay","seconds":0.4}]""").forEach { it() }
telemetry.update()
```

Expected output is `Delay 0.4 seconds`. Replace the JSON string with the full
**View JSON** contents to report the nine-step example. Both helpers implement
all six commands and only report values; even delay does not sleep in dry run.
Their public entry points are exercised by the consumer tests.
If trying these outside an OpMode, use `System.out::println` in Java or `::println`
in Kotlin instead of telemetry. Place the smoke test in a one-time initialization
path so it reports once, rather than adding the messages on every loop iteration.

Use `compile` for JSON text, `decode` for the Base64 barcode text supplied by a
camera/scanner, and `load` for a filename already saved on the Robot Controller.
These are three different inputs, not interchangeable strings.

## Bind the factories to your robot

Both languages expose an `Auto<T>` interface mirroring the command contract. Set
`T` to your framework's command type, then implement its six methods with your
team's action factories. For Devil Dogs' NextFTC project the correspondences are:

| Example factory | Devil Dogs behavior to supply |
| --- | --- |
| delay(seconds) | `Auto.delay(seconds)` |
| chase(cycles) | `Auto.chase(cycles)` |
| intake(place, side) | Convert the example enums to team enums, then call `Auto.intake(place, side)` |
| deposit(side, ao, lo) | Call `Auto.deposit` with team side and offsets converted from tiles |
| park(side) | `Auto.park` with team side |
| drive(tx, ty, h, axial, lateral, ao, lo) | Build the team's `Nav.pose`, converting tiles/degrees and alignment strings, then call `Auto.drive` |

The example adapter preserves values rather than building a navigation system.
For Devil Dogs, one tile is 23.5 inches. Drive `tx`/`ty` are signed field
coordinates, not alliance-mirrored coordinates. Heading is degrees. `ao`/`lo`
are destination-relative axial/lateral offsets in tiles. The Nav implementation
applies alignment and offsets; do not apply them twice in an intermediate layer.

Garden, flower and loading_zone are intake places; sides are left and right.
Enums are case-insensitive. Missing/unrecognized alignment uses center. Missing
offsets are zero. Deposit accepts legacy `txo`/`tyo`, but `ao`/`lo` win even when
explicitly zero. Drive does not use those deposit-only legacy aliases. The
builder emits current field names. Chase's intended count is positive; zero is
not an unlimited sentinel. Each team decides validation inside its actions.

## Import and compile

The Java example uses `com.threedrd.quanomous.CommandFactory` and the Kotlin
example uses `QuanomousCompiler`'s original map of JSON handlers. Both live in the
standard `org.firstinspires.ftc.teamcode` package for easy adoption.

After constructing an example adapter with your robot's factories:

```kotlin
val commands = adapter.decode(barcodeData)
// In a NextFTC project, using Command as T:
SequentialGroup(*commands.toTypedArray()).endAfter(29.5).schedule()
```

The last line belongs in the robot lifecycle, with the normal NextFTC import;
the standalone example module does not depend on NextFTC. The 29.5-second cap
matches the team adapter's sequence limit. It is not imposed by the library.
Commands must retain subsystem requirements and handle cancellation in the
robot's existing framework. A state-machine consumer instead advances only
when its active action is complete.

Factories must construct actions without scheduling hardware during compilation.
Call `compile(json)` for text routines or `load(filename)` for stored ones. Camera
ingestion can first call `Quanomous.process(barcodeData)` and pass the returned
filename to `load`. The example tests use the shared builder-generated compressed
payload, so the Java/Kotlin API and default blocks are checked together.

During initialization, scan/store and select the intended routine. At Auto start,
load and schedule that selection once. Do not put scheduling in the repeated
camera callback: seeing the same QR repeatedly must not repeatedly start Auto.
This repository supplies barcode text decoding, not a Limelight/camera driver,
OpMode registration, hardware map or complete navigation implementation.

## Customize

Change the builder template and your robot handler map together. The examples'
small enums and six method interfaces are team-level conveniences, not a closed
library schema. A team using different actions may replace them entirely. See
[template configuration](templates.md). Physical action implementations and
field measurements are supplied by the team; no mock action in these examples
is presented as a working motor or navigation implementation.

For traditional iterative OpModes, see the [state-machine integration](state-machine-example.md) instead of scheduling a command group.
