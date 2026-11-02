# Four BioBuzz examples

Choose your language and the way your robot runs autonomous:

| | Java | Kotlin |
| --- | --- | --- |
| Plain LinearOpMode: while loop and switch/when | [QuanomousStateMachineOpMode.java](java/src/main/java/org/firstinspires/ftc/teamcode/QuanomousStateMachineOpMode.java) | [QuanomousStateMachineOpMode.kt](kotlin/src/main/java/org/firstinspires/ftc/teamcode/QuanomousStateMachineOpMode.kt) |
| Command-based: NextFTC 1.1.0 | [QuanomousCommandOpMode.java](java/src/main/java/org/firstinspires/ftc/teamcode/QuanomousCommandOpMode.java) | [QuanomousCommandOpMode.kt](kotlin/src/main/java/org/firstinspires/ftc/teamcode/QuanomousCommandOpMode.kt) |

All four load the same [BioBuzz JSON asset](java/src/main/assets/biobuzz-route.json),
which the builder loads with **Load BioBuzz example**. Each supports delay,
drive, intake, deposit, chase and park. Delay waits for real elapsed time.
Hardware actions display their parameters and wait for a fresh A-button press
instead of moving a robot. Replace those marked sections with your team's controls
and completion conditions. There is no separate preliminary example to run.

## Copy into TeamCode

1. [Install the Quanomous library](../docs/library.md).
2. Copy ONE source file from the table into
   `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/`. These are alternatives;
   Java/Kotlin versions share class names. Kotlin requires your project's normal
   Kotlin configuration. No other helper source files are needed.
3. Copy the JSON asset into `TeamCode/src/main/assets/biobuzz-route.json`.
4. For the command example, use a NextFTC 1.1.0 project and add
   `implementation 'dev.nextftc:ftc:1.1.0'` to TeamCode if it is not present.
   These examples target v1, matching BioBuzz, rather than the different v2 API.
   The plain LinearOpMode source does not use NextFTC.
5. Build/install your robot app and select the example in the Autonomous list.
   Press Start; after the initial delay, release and press A for each hardware
   step. Holding A does not skip later steps.

For hardware integration, see the [LinearOpMode walkthrough](../docs/state-machine-example.md)
or [command walkthrough](../docs/command-example.md). Instruction fields and units
are described in the [BioBuzz contract](../docs/biobuzz-example.md).

## Build these examples here

These independent Android library modules compile the actual OpModes against
the locally published Quanomous AAR, FTC RobotCore 12.0.0 and NextFTC 1.1.0.
The FTC/NextFTC dependencies are compile-only; your robot project supplies them.
The Java build uses no Kotlin plugin. These modules are compilation checks, not
standalone robot apps; do not copy their Gradle files into TeamCode.

After publishing locally, run from the repository root:

```powershell
./gradlew.bat -p examples :java:assembleRelease :kotlin:assembleRelease
```

Use `./gradlew` on macOS/Linux and set `ANDROID_HOME`. Both modules package the
same canonical asset; Kotlin references the Java assets directory directly.
There are no tests in the example modules.
