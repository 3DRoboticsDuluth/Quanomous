# Kotlin library installation and API

The library is an Android AAR with minimum API 24, Java 8 bytecode, and transitive Kotlin runtime and Gson dependencies. It does not require an FTC SDK or command framework. Development uses JDK 17, Gradle 9.1.0, Android Gradle Plugin 8.13.2 and Kotlin 2.4.20.

## Install locally

Run from the repository root:

```powershell
./gradlew.bat :library:check :library:assembleRelease :library:publishReleasePublicationToReviewRepository
```

Use `./gradlew` on macOS/Linux. Configure `ANDROID_HOME` or an untracked `local.properties` with `sdk.dir`.

The AAR, sources, POM and Gradle metadata are written beneath `build/repository/org/threedrd/quanomous/0.1.0-SNAPSHOT/`. These are provisional local coordinates, not a publicly available release.

In a consuming Android project's dependency repository configuration, add:

```groovy
maven { url = uri('/absolute/path/to/Quanomous/build/repository') }
```

Then add to TeamCode's dependencies:

```groovy
implementation 'org.threedrd:quanomous:0.1.0-SNAPSHOT'
```

Java consumers do not need a Kotlin plugin to use the compiled library. Kotlin consumers use their normal Kotlin plugin. The tested consumers use Kotlin 2.4.20; check other compiler versions before advertising compatibility.

## Dynamic instructions

`Quanomous.parse(json)` returns a Gson JSON array. `Quanomous.decode(payload)` decodes Base64 followed by GZIP into UTF-8 JSON. A compiler maps each step's `cmd` to a team handler and returns an ordered list of values of type `T`. It never schedules them.

```kotlin
val compiler = QuanomousCompiler<MyAction>(mapOf(
    "my_action" to { step -> MyAction(step["target"].asString) }
))
val actions = compiler.compile(Quanomous.parse(json))
```

Java uses `QuanomousCompiler.fromHandlers(...)` with a `Map<String, CommandFactory<T>>`; Java lambdas implement `create(step)`. Access the shared object as `Quanomous.INSTANCE`. See [compiling examples](../examples/README.md).

Teams own fields, defaults, validation and execution. Unknown names fail; malformed parameters fail in the team's handler. No season schema is imposed. Construct commands/actions without moving hardware, and schedule only after compilation returns. Frameworks compose the list into their native sequence; state machines advance only after active actions complete. No scheduler, cancellation engine or automatic timeout is added.

## Camera ingestion and storage

On the Robot Controller:

```kotlin
val filename = Quanomous.process(barcodeData)
val actions = compiler.load(filename)
```

`process` decodes, parses, suppresses an immediately repeated routine using the last hash, saves JSON, and returns its filename. `load` reads stored JSON; `options` lists names. Storage defaults to `/sdcard/FIRST/quanomous`; replace `Quanomous.storage` with `QuanomousStorage(directory)` as needed.

Names use `MM-dd-HHmm-ssSSS.json`, in local time; `ssSSS` is milliseconds within the minute. Distinct routines saved in the same millisecond can overwrite, accepted for the single-loop scan workflow. Existing filenames remain loadable.

The singleton and mutable `storage`, `decoder`, `now`, `lastHash` and `lastName` remain. Processing is unsynchronized, saves are direct writes, and decoding has no decompressed-size cap. Remembered state updates only after saving succeeds. These are deliberate scope choices. Handle errors at the robot integration boundary rather than scheduling a fallback routine.
