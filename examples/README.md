# Java and Kotlin consumers

These independent Android modules consume the locally published AAR by Maven coordinates. They do not use `project(':library')` or copied library source. The Java example applies no Kotlin plugin.

Example source and tests use the standard FTC `org.firstinspires.ftc.teamcode`
package and matching folders. Copy example code into that package in your own
TeamCode module (or adjust the package if your team has customized it). The
library remains under `com.threedrd.quanomous`; keep those imports unchanged.
The two examples are separate alternatives, not modules to install together.

Publish the library first, then run from the repository root:

```powershell
./gradlew.bat -p examples :java:testReleaseUnitTest :java:assembleRelease :kotlin:testReleaseUnitTest :kotlin:assembleRelease --refresh-dependencies
```

Use `./gradlew` on macOS/Linux. Set `ANDROID_HOME` for both builds, or give each build its own untracked `local.properties`.

- [JavaExample<T>](java/src/main/java/org/firstinspires/ftc/teamcode/JavaExample.java) registers team factories through `CommandFactory<T>`.
- [KotlinExample<T>](kotlin/src/main/java/org/firstinspires/ftc/teamcode/KotlinExample.kt) registers the same fields through the map API.
- [JavaDryRun](java/src/main/java/org/firstinspires/ftc/teamcode/JavaDryRun.java) and [KotlinDryRun](kotlin/src/main/java/org/firstinspires/ftc/teamcode/KotlinDryRun.kt) provide complete reporting-only factories to try first.

Choose one language. For Java, copy `JavaExample.java` and `JavaDryRun.java` into
your FTC project's `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/` folder.
For Kotlin, copy `KotlinExample.kt` and `KotlinDryRun.kt` into that same package in
a project already configured for Kotlin. Add the library dependency as described
in [installation](../docs/library.md). Do not copy these modules' Gradle files into
TeamCode or expect an OpMode to appear: these are helpers called by your OpMode.

Both implement Devil Dogs' BioBuzz instruction contract: `delay`, `chase`,
`intake`, `deposit`, `park`, and `drive`. The builder's default BioBuzz blocks
produce the same fields. See [the example routine](routines/biobuzz-route.json)
and [the integration walkthrough](../docs/biobuzz-example.md).

Construct `JavaExample<T>` or `KotlinExample<T>` with your implementation of its
`Auto<T>` interface. Each factory returns your command/action type. Then call
`decode(barcodeData)`, `compile(json)`, or `load(filename)` and compose the returned
list using your robot's framework. Both preserve signed drive coordinates,
alignment defaults, tile offsets and deposit's legacy aliases.

Tests decode the actual builder QR fixture and check every field passed to the
robot factories. They also compile the example route, check ordering, confirm
that compilation does not execute actions, and check offset defaults/aliases.
The headless Blockly test reconstructs that same route from the default blocks.

These are compiling robot-adapter examples, not installable robot applications.
Hardware, localization and command implementations belong to the team. The tests
use recording actions to verify the adapter without moving a robot. Asynchronous
state machines must wait for completion; command integrations must use framework
sequencing, requirements and cancellation. Complete framework project scaffolds
remain follow-up work.
