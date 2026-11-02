# Verification and release

## Local checks

Use JDK 17, Android SDK platform 34 and Node.js. Set `JAVA_HOME` and `ANDROID_HOME`. Dependencies need network access on the first build.

```shell
node builder/test-templates.cjs
./gradlew :library:check :library:assembleRelease :library:publishReleasePublicationToReviewRepository --max-workers=2
./gradlew -p examples :java:assembleRelease :kotlin:assembleRelease --refresh-dependencies --max-workers=2
```

On Windows use `gradlew.bat` or `scripts/verify.ps1`. Library check includes debug/release tests and Android lint.

The Node check uses bundled Blockly/Pako, creates all six BioBuzz blocks, calls the application's export function and compares `fixtures/biobuzz.json`. Library tests decode that compressed fixture. Independent Java/Kotlin consumers check custom handlers against the published AAR.

Both consumers use the Devil Dogs BioBuzz contract. Consumer verification compiles and assembles the examples against the published AAR; the examples contain no tests. Builder and library tests remain in their respective modules.

Regenerate fixtures deliberately with `node fixtures/create-fixture.cjs` only when the example contract changes, review the JSON, then rerun both checks.

## Browser acceptance before deployment

- Confirm the root page, Blockly, template selector and artwork load over localhost or HTTPS.
- Generate a BioBuzz QR using fractional delay, signed drive coordinates, side/place choices and offsets; return to the editor.
- Switch to Decode, confirm clearing, and verify legacy blocks.
- Check phone layout and offline refresh after service-worker installation.
- Scan with the intended robot and confirm decoded values before motion.

These checks are separate from headless/Gradle checks. The single-file bundler is
unsupported and no longer offered in the UI. Legacy preview fidelity requires
follow-up; this milestone does not certify tournament use.

## Public cutover

Keep the repository name and Pages root. This branch adds CI and a manual Central staging workflow, changes no Pages settings, and has published no remote artifact. Pages currently publishes main at the root, so merging would deploy the builder. Review the new default template before merging. Choose public hosting/final Maven coordinates and verify a clean FTC consumer. Coordinate website links and migration notices before archiving old repositories.

## Local review result

November 1, 2026: library checks and Android lint passed; local AAR publication and both independent consumer builds passed. Node template/export, connected-block clearing and Start registration regression checks passed. Documentation links and Git whitespace checks passed.

Desktop browser inspection confirmed Blockly/BioBuzz artwork, action blocks, a
connected delay routine, QR rendering and return to the editor. Both directions
of template switching were verified after fixing duplicate Start registration
and clearing.

The onboarding review added a shared example loader, live JSON inspection and
Java/Kotlin OpMode examples. Builder tests cover block export failures, disconnected actions and duplicate Start roots. Legacy
Drive axial selection and fractional Delay export were corrected. Phone-sized
layout now scrolls through editor and preview instead of compressing both into
half a screen. A 390-by-844 browser viewport loaded the nine-step example and
rendered its QR. QR output includes a four-module white quiet zone. Preview
timing explicitly excludes unknown team action durations.

Physical phone/camera scanning remains deferred. Local Chrome offline refresh
and same-origin old-cache upgrade passed on November 2, 2026; physical-device
coverage is not claimed. Public Maven hosting remains a release prerequisite, not a task
completed by the local artifact checks.

## Current example scope

Four framework-neutral integration templates compile: command mapping and plain
LinearOpMode in both Java and Kotlin. They contain no command-framework dependency.
Command examples use local placeholder Command types, named factories and a
sequence/schedule integration point. Plain examples use an iterator and switch/when,
advancing after a team step method returns true. Delay is a team placeholder too.
There is no timer, entry flag, button simulation or telemetry scaffolding.

The Java and Kotlin modules compile against the local Quanomous AAR and compile-only
FTC RobotCore 12.0.0. Both package the exact canonical asset from
examples/java/src/main/assets; Kotlin references that directory directly.
Source compilation does not verify robot behavior or framework scheduling: teams
must implement the placeholders and integrate their scheduler/lifecycle. The
command drive mapping intentionally uses only tx, ty and h. No tests are included
in example modules; library and builder regression checks remain.

## CI and release preparation

See [release operations](release.md) for verified Pages settings, namespace
requirements, environment/secrets setup and the manual staging workflow.
Local verification now also checks POM metadata, sources/API documentation and
packaged example artifacts with `python scripts/check-artifacts.py`.

November 2, 2026 release-access setup: all four environment secrets are installed;
main is the sole permitted branch, required reviewers are disabled by maintainer
choice, and admin bypass is disabled. Local release signing and independent
verification of five signatures passed. No remote staging or publication ran.
See release operations for the public key fingerprint and recovery location.

The [final audit and acceptance checklist](acceptance.md) records the reviewed
head and passing hosted CI, with physical camera/offline/recovery results left
unchecked pending evidence.

November 2, 2026 maintainer decision: actual camera scanning and repeated camera
ingestion are deferred because hardware is unavailable. They are follow-up work,
not prerequisites for the initial release, and have not been marked passed.

Local offline acceptance passed with the network disabled and HTTP cache disabled
by no-store responses. The origin/main v1 worker upgraded via ordinary reloads
to review-6; all 18 cached responses matched candidate bytes. Offline sample/JSON,
QR, return and template switching passed with no page errors or failed resources.
See [acceptance evidence](acceptance.md) for the exact scope and remaining work.
