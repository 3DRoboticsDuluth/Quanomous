# Java and Kotlin consumers

These independent Android modules consume the locally published AAR by Maven coordinates. They do not use `project(':library')` or copied library source. The Java example applies no Kotlin plugin.

Publish the library first, then run from the repository root:

```powershell
./gradlew.bat -p examples :java:testReleaseUnitTest :java:assembleRelease :kotlin:testReleaseUnitTest :kotlin:assembleRelease --refresh-dependencies
```

Use `./gradlew` on macOS/Linux. Set `ANDROID_HOME` for both builds, or give each build its own untracked `local.properties`.

- [Java](java/src/main/java/org/threedrd/examples/JavaExample.java) registers `CommandFactory<Runnable>` handlers.
- [Kotlin](kotlin/src/main/java/org/threedrd/examples/KotlinExample.kt) uses the existing map of JSON handlers returning `() -> Unit`.

Both define a custom `signal` instruction. Tests confirm compilation does not execute actions, then run them and verify order. This demonstrates that the library does not require the BioBuzz or Decode contract.

These are compiling API examples, not installable robot applications. Runnable/function examples model immediate actions only. Asynchronous state machines must wait for completion; command integrations must use framework sequencing, requirements and cancellation. Complete SolversLib, NextFTC and robot-loop examples remain follow-up work.
