# Four BioBuzz examples

Choose your language and the way your robot runs autonomous:

| | Java | Kotlin |
| --- | --- | --- |
| Plain LinearOpMode: while loop and switch/when | [QuanomousStateMachineOpMode.java](java/src/main/java/org/firstinspires/ftc/teamcode/QuanomousStateMachineOpMode.java) | [QuanomousStateMachineOpMode.kt](kotlin/src/main/java/org/firstinspires/ftc/teamcode/QuanomousStateMachineOpMode.kt) |
| Command mapping: bring your command library | [QuanomousCommandOpMode.java](java/src/main/java/org/firstinspires/ftc/teamcode/QuanomousCommandOpMode.java) | [QuanomousCommandOpMode.kt](kotlin/src/main/java/org/firstinspires/ftc/teamcode/QuanomousCommandOpMode.kt) |

All four load the same [BioBuzz JSON asset](java/src/main/assets/biobuzz-route.json),
which the builder loads with **Load BioBuzz example**. They focus on reading and
interpreting the six instructions. The robot-specific
methods are explicit placeholders to replace with your team's code before use.
The command examples include a local placeholder Command type; they have no dependency on a command framework.

## Copy into TeamCode

1. [Install the Quanomous library](../docs/library.md).
2. Copy ONE source file from the table into
   `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/`. These are alternatives;
   Java/Kotlin versions share class names. Kotlin requires your project's normal
   Kotlin configuration. No other helper source files are needed.
3. Copy the JSON asset into `TeamCode/src/main/assets/biobuzz-route.json`.
4. For the command example, replace the local `Command` placeholder and its
   sequence/schedule calls with your preferred library's types and APIs.
5. Implement all marked team methods, including delay, then build/install your robot app and select
   the example in the Autonomous list. Unimplemented methods throw rather than
   silently skipping robot actions. For command integration, also use your
   library's normal OpMode/scheduler lifecycle; the placeholder schedule call
   alone does not provide a running scheduler.

For hardware integration, see the [LinearOpMode walkthrough](../docs/state-machine-example.md)
or [command walkthrough](../docs/command-example.md). Instruction fields and units
are described in the [BioBuzz contract](../docs/biobuzz-example.md).

## Build these examples here

These independent Android library modules compile the actual OpModes against
the locally published Quanomous AAR and FTC RobotCore 12.0.0.
The FTC dependency is compile-only; your robot project supplies it.
The Java build uses no Kotlin plugin. These modules are compilation checks, not
standalone robot apps; do not copy their Gradle files into TeamCode.

After publishing locally, run from the repository root:

```powershell
./gradlew.bat -p examples :java:assembleRelease :kotlin:assembleRelease
```

Use `./gradlew` on macOS/Linux and set `ANDROID_HOME`. Both modules package the
same canonical asset; Kotlin references the Java assets directory directly.
There are no tests in the example modules.
