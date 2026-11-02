# BioBuzz command-based integration

Choose the Java or Kotlin **QuanomousCommandOpMode** in the
[examples table](../examples/README.md). Both are complete NextFTC 1.1.0 OpModes
using the same shared BioBuzz asset as the builder and plain LinearOpMode examples.

During Init, the handler map converts each JSON instruction into a `Command`.
`delay` creates NextFTC's `Delay`; the other five handlers create simulated
commands. `SequentialGroup` preserves the routine's order. Nothing is scheduled
while reading JSON. At Start, the group is scheduled once; NextFTC updates it and
advances when the active command finishes. Stop cancels the group.

The simulated hardware commands show the full JSON instruction in telemetry.
They begin in `start()`, check for a fresh A press in `update()`, expose completion
through `isDone`, and release outputs in `stop(interrupted)`. Replace each
`simulated(step)` factory with your team's corresponding drive, intake, deposit,
chase or park command. Extract its parameters from `step`, preserving the
[BioBuzz units and fields](biobuzz-example.md).

For a drive command, initialize the target in `start()`, update navigation in
`update()`, report at-target completion through `isDone`, and stop the drivetrain
in `stop(interrupted)`. Declare the command's subsystem requirements with
`requires(...)`. Use the same lifecycle for mechanisms and cycle sequences.
Factories construct commands; they must not start hardware or schedule commands.
Keep your team's normal autonomous deadline and robot-wide shutdown.

To use camera output instead of the asset, decode the barcode text during Init
and compile the resulting JSON, or use `compiler.load(filename)` for a saved
routine. Schedule the resulting group at Auto start, never in a repeated scan
callback. Team-specific instructions can be added by changing the handler map
and builder template together. Quanomous does not impose this BioBuzz contract.

The examples pin [NextFTC v1.1.0](https://github.com/NextFTC/NextFTC/tree/v1.1.0)
to match the source team project; v2 has a different API. A SolversLib integration
would use its command type, scheduler and sequence group in the same places.
