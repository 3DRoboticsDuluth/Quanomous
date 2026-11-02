# Verification and release

## Local checks

Use JDK 17, Android SDK platform 34 and Node.js. Set `JAVA_HOME` and `ANDROID_HOME`. Dependencies need network access on the first build.

```shell
node builder/test-templates.cjs
./gradlew :library:check :library:assembleRelease :library:publishReleasePublicationToReviewRepository --max-workers=2
./gradlew -p examples :java:testReleaseUnitTest :java:assembleRelease :kotlin:testReleaseUnitTest :kotlin:assembleRelease --refresh-dependencies --max-workers=2
```

On Windows use `gradlew.bat` or `scripts/verify.ps1`. Library check includes debug/release tests and Android lint.

The Node check uses bundled Blockly/Pako, creates all six BioBuzz blocks, calls the application's export function and compares `fixtures/biobuzz.json`. Library tests decode that compressed fixture. Independent Java/Kotlin consumers check custom handlers against the published AAR.

Regenerate fixtures deliberately with `node fixtures/create-fixture.cjs` only when the example contract changes, review the JSON, then rerun both checks.

## Browser acceptance before deployment

- Confirm the root page, Blockly, template selector and artwork load over localhost or HTTPS.
- Generate a BioBuzz QR using fractional delay, signed drive coordinates, side/place choices and offsets; return to the editor.
- Switch to Decode, confirm clearing, and verify legacy blocks.
- Check phone layout and offline refresh after service-worker installation.
- Scan with the intended robot and confirm decoded values before motion.

These checks are separate from headless/Gradle checks. Inherited single-file bundling and legacy export/preview issues require follow-up; this milestone does not certify tournament use.

## Public cutover

Keep the repository name and Pages root. This branch adds no deployment workflow, changes no Pages settings, and publishes no remote artifact. Review the new default template before merging. Choose public hosting/final Maven coordinates and verify a clean FTC consumer. Coordinate website links and migration notices before archiving old repositories.

## Local review result

November 1, 2026: library checks and Android lint passed; local AAR publication and both independent consumer checks passed. Node template/export, connected-block clearing and Start registration regression checks passed. Documentation links and Git whitespace checks passed.

Desktop browser inspection confirmed Blockly/BioBuzz artwork, action blocks, a connected delay routine, QR rendering and return to the editor. Both directions of template switching were verified after fixing duplicate Start registration and clearing. No new console errors occurred in the corrected template switches. Phone layout, real camera scanning and offline-device acceptance remain outstanding.
