# BioBuzz command mapping

Choose the Java or Kotlin **QuanomousCommandOpMode** in the
[examples table](../examples/README.md). Each reads the shared JSON, maps its
instructions to private command factories, composes a sequence and schedules
after Start. Java nests an anonymous HashMap inside the compiler; Kotlin uses mapOf.

The local `Command` class is a placeholder for your preferred command library
(NextFTC, SolversLib, WPILib, etc.). Replace it and its sequence/schedule calls
with that library's equivalents. Implement the private factories with your team's
commands. These are compiling integration templates, not runnable robot solutions.

The drive factory deliberately shows only tx, ty and heading. The shared JSON's
alignment/offset fields are ignored in this minimal mapping; add them back when
needed by your navigation code. Other fields follow the
[BioBuzz contract](biobuzz-example.md). Factories construct commands; execution
and completion belong to your command library.

File/parsing errors stay in readRoutine(), which returns an empty array on failure.
Add your team's reporting/error policy as needed. These examples focus on JSON
mapping and omit telemetry and lifecycle policy.
