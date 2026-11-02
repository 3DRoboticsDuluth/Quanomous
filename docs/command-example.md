# BioBuzz command mapping

Choose the Java or Kotlin **QuanomousCommandOpMode** in the
[examples table](../examples/README.md). Both load the shared JSON during Init,
map each instruction to a named private command factory, build a sequential group,
and schedule it once at Start.

Java nests an anonymous `HashMap` with `put` registrations inside the compiler.
Kotlin uses `mapOf`. Each handler extracts the instruction's parameters and passes
them to `delay`, `drive`, `intake`, `deposit`, `chase` or `park`.

Delay returns the framework's delay command. Replace the other factory bodies
with your team's commands. Those commands own execution, completion, subsystem
requirements and cancellation. Factories construct commands; they do not schedule
or start hardware. The example uses NextFTC 1.1.0 so it compiles, but the same
mapping pattern works with your preferred command library: use its command type,
sequence group and lifecycle. SolversLib and WPILib APIs are not interchangeable
with NextFTC imports.

`readRoutine()` contains file/parsing error handling and returns null on failure,
so Init can return without creating a partial sequence. Add your team's reporting
and runtime handling as appropriate. These examples intentionally concentrate on
JSON mapping rather than telemetry, controls or robot lifecycle policy.

For scanned input, decode the barcode text and compile the resulting JSON array;
for a saved routine use `compiler.load(filename)`. The
[BioBuzz contract](biobuzz-example.md) describes fields and units.
