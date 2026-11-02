# Release preparation and operations

## Verified hosting state (November 2, 2026)

An authenticated, read-only GitHub Pages API check returned:

- URL: https://3droboticsduluth.github.io/Quanomous/
- Build type: legacy (branch publishing), status: built.
- Source: main, repository root (/).
- HTTPS enforced; no custom domain.

**Merging into main currently deploys the builder.** The new workflows do not
change that setting or deploy Pages. Before approving a merge, either accept that
it is also the builder deployment, or explicitly arrange a separate deployment
process in Pages settings. No settings were changed during this preparation.

## CI

.github/workflows/ci.yml runs on pull requests, main/consolidation pushes and
manual dispatch. It uses JDK 17, Android SDK 34, Node 22 and Python, then:

1. Runs builder regression checks.
2. Runs library unit tests/lint, assembles the AAR and publishes locally.
3. Compiles all four example templates against that local publication.
4. Checks POM metadata, AAR/sources/API documentation, both example classes per
   language and the exact shared asset packaged in both AARs.
5. Retains reports and local artifacts for review.

It has read-only repository permission and no publishing credentials. Add the
`verify` check to the main branch protection/ruleset after its first successful
run, if required checks are desired. No rule changes have been made.

## Maven Central namespace: verified

The chosen coordinates are io.github.3droboticsduluth:quanomous. Source packages
and the Android namespace remain com.threedrd.quanomous. DNS verification is not
needed for this GitHub-based namespace.

On November 2, 2026, the maintainer confirmed Central Portal verified
io.github.3droboticsduluth using the public challenge repository
3DRoboticsDuluth/j8wbelyqi9. The maintainer deleted the temporary repository after verification.
This records maintainer confirmation; no artifact was staged or published.

Sources: [namespace registration](https://central.sonatype.org/register/namespace/),
[publication requirements](https://central.sonatype.org/publish/requirements/).

## Configure release access before using the workflow

The `maven-central` GitHub environment is configured with only the `main` branch
allowed. Per maintainer choice, required reviewers are disabled; workflow dispatch
remains manual, and final publication is manual in Central Portal. Administrator
bypass is disabled. These environment secrets are installed:

| Secret | Value |
| --- | --- |
| MAVEN_CENTRAL_USERNAME | Username from a Central Portal user token |
| MAVEN_CENTRAL_PASSWORD | Password from that token, not the web login password |
| MAVEN_SIGNING_KEY | Complete ASCII-armored private PGP signing key |
| MAVEN_SIGNING_PASSWORD | Signing-key passphrase, if the key has one |

Use an organization-maintained signing key and distribute its public key as
required by Central. Do not place credentials or private keys in Git or logs.
Setup completed November 2, 2026. The Central token expires November 2, 2126,
as requested by the maintainer. The dedicated RSA-4096 release signing key has
public fingerprint `9F5F830976CBD1ECC1200E93019896ECAC344EC8`; its public key was
sent to keyserver.ubuntu.com. Signing-key expiry is two years from creation.

The current recovery backup is encrypted with Windows DPAPI at
`%LOCALAPPDATA%/3DRoboticsDuluth/Quanomous/release-signing/recovery.dpapi`.
It contains the exported key and passphrase and can be decrypted only in the
originating Windows user's context. It is not a portable organizational backup;
arrange secure organizational recovery storage before relying on another machine.
Secret values are not stored in the repository.

Required reviewers can be added later if an additional GitHub approval gate is
wanted. The five existing organization teams were given read access and together
cover all 18 current members, but none is a required release reviewer now.

## Release workflow: manual staging, then manual publication

.github/workflows/maven-central.yml has **workflow_dispatch only**. A merge,
push or tag creation cannot trigger it. It has read-only GitHub permissions and
creates neither tags nor releases. Preparation did not dispatch this workflow.

When a release is authorized:

1. Confirm the namespace/account and record acceptance results. Real camera
   acceptance is deferred by maintainer decision until equipment is available;
   do not report it as passed.
2. Review/merge the intended source, accounting for the existing Pages auto-deploy.
3. Create an immutable vX.Y.Z tag on the reviewed commit reachable from main.
4. Run **Stage Maven Central release** from main, entering X.Y.Z. The workflow
   validates the version and tag ancestry, checks out that exact commit and runs
   the full verification without publishing secrets.
5. The main-only maven-central environment job checks out the verified commit,
   signs its AAR, sources, Dokka HTML documentation JAR and metadata, and calls
   `:library:publishToMavenCentral -PcentralRelease=true -PreleaseVersion=X.Y.Z`.
6. This uploads a deployment for Portal validation only; automatic final release
   is disabled. Review it in Central Portal, then explicitly choose Publish when
   authorized. Do not use publishAndReleaseToMavenCentral.
7. After availability, verify the released dependency from a clean FTC project
   with mavenCentral(), update installation docs from local-only to the released
   version, and create release notes linking the builder and four templates.

Ordinary builds remain 0.1.0-SNAPSHOT and publish only to build/repository. The
releaseVersion property changes the version without changing the normal snapshot
consumer checks. centralRelease=true enables remote staging and signing only for
strict X.Y.Z releases; it is not needed for local builds. The Portal task graph was checked using --dry-run. The release publication was
also signed locally and all five detached signatures independently verified
using GnuPG. No upload task or Portal validation/publication was performed.

Implementation uses the [Vanniktech base publishing plugin](https://vanniktech.github.io/gradle-maven-publish-plugin/base/)
and its [manual Central publishing mode](https://vanniktech.github.io/gradle-maven-publish-plugin/central/).

## Builder acceptance after authorized deployment

Open the existing Pages URL and confirm the service worker upgrades old caches.
Load the shared BioBuzz sample, inspect JSON, generate/scan the QR, switch
between BioBuzz and legacy Decode, and check phone layout/offline refresh.
The example templates still require teams to implement robot behavior and their
command scheduler lifecycle; compilation is not a robot acceptance test.

The [final acceptance checklist](acceptance.md) separates verified software checks
from remaining camera/offline/recovery evidence and the explicit release decision.

Local offline refresh and old-cache upgrade are now verified; see
[acceptance evidence](acceptance.md). Repeat a public-site smoke check after an
authorized deployment. Portable organizational signing recovery remains open;
camera acceptance remains deferred by maintainer choice.
