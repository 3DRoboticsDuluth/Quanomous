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

Keep the repository name and Pages root. This branch adds no deployment workflow, changes no Pages settings, and publishes no remote artifact. Review the new default template before merging. Choose public hosting/final Maven coordinates and verify a clean FTC consumer. Coordinate website links and migration notices before archiving old repositories.

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

Physical phone/camera scanning and real offline-device acceptance remain
outstanding. Public Maven hosting remains a release prerequisite, not a task
completed by the local artifact checks.

The state-machine walkthrough now uses a traditional LinearOpMode while loop and switch over the current JSON instruction. The previous action-runner/demo helpers and all example-module tests were removed. The Java consumer now compiles the actual SDK-specific LinearOpMode using compile-only FTC RobotCore 12.0.0 and packages the canonical shared routine from its assets folder. The builder loads that same file.

The Kotlin consumer also compiles its equivalent LinearOpMode against compile-only FTC RobotCore 12.0.0. Both AARs package the canonical BioBuzz asset from `examples/java/src/main/assets`; the Kotlin module references that directory instead of duplicating the routine.

The examples now consist of four complete OpModes: plain LinearOpMode and NextFTC 1.1.0 command-based alternatives in Java and Kotlin. The former generic adapters and reporting helpers were removed. All four compile; both modules package the shared asset. No example tests are included.
