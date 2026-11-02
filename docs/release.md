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

## Maven Central namespace: prerequisite still open

The intended coordinates remain com.threedrd:quanomous. Sonatype verifies the
exact reverse-DNS namespace: com.threedrd requires control of threedrd.com and a
TXT record containing the Central Portal-issued verification key. Control of
3droboticsduluth.com does not satisfy that verification. The account's namespace
status and control of threedrd.com have NOT been established by this work.

Before release, an organization maintainer must register/verify that namespace
in Central Portal, or agree a Maven group based on an organization-controlled
domain/verified code-hosting namespace. Source package com.threedrd.quanomous
can remain unchanged if the Maven group changes. Do not publish with a guessed
or unauthorized namespace. No account, DNS or namespace changes were made.

Sources: [namespace registration](https://central.sonatype.org/register/namespace/),
[publication requirements](https://central.sonatype.org/publish/requirements/).

## Configure release access before using the workflow

Create a GitHub environment named `maven-central`, configure required reviewers
and restrict deployment branches to main. Configure these environment secrets:

| Secret | Value |
| --- | --- |
| MAVEN_CENTRAL_USERNAME | Username from a Central Portal user token |
| MAVEN_CENTRAL_PASSWORD | Password from that token, not the web login password |
| MAVEN_SIGNING_KEY | Complete ASCII-armored private PGP signing key |
| MAVEN_SIGNING_PASSWORD | Signing-key passphrase, if the key has one |

Use an organization-maintained signing key and distribute its public key as
required by Central. Do not place credentials or private keys in Git or logs.
Environment protections/secrets are setup prerequisites, not settings created by
this PR. The workflow name alone does not enforce reviewer approval: the GitHub
environment must be configured. No secrets were inspected or installed.

## Release workflow: manual staging, then manual publication

.github/workflows/maven-central.yml has **workflow_dispatch only**. A merge,
push or tag creation cannot trigger it. It has read-only GitHub permissions and
creates neither tags nor releases. Preparation did not dispatch this workflow.

When a release is authorized:

1. Finish robot-camera/offline acceptance and confirm the namespace/account.
2. Review/merge the intended source, accounting for the existing Pages auto-deploy.
3. Create an immutable vX.Y.Z tag on the reviewed commit reachable from main.
4. Run **Stage Maven Central release** from main, entering X.Y.Z. The workflow
   validates the version and tag ancestry, checks out that exact commit and runs
   the full verification without publishing secrets.
5. Approve the maven-central environment job. It checks out the verified commit,
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
strict X.Y.Z releases; it is not needed for local builds. The Portal task was
checked locally using --dry-run only; signing/upload/Portal validation require
real credentials and have not been performed.

Implementation uses the [Vanniktech base publishing plugin](https://vanniktech.github.io/gradle-maven-publish-plugin/base/)
and its [manual Central publishing mode](https://vanniktech.github.io/gradle-maven-publish-plugin/central/).

## Builder acceptance after authorized deployment

Open the existing Pages URL and confirm the service worker upgrades old caches.
Load the shared BioBuzz sample, inspect JSON, generate/scan the QR, switch
between BioBuzz and legacy Decode, and check phone layout/offline refresh.
The example templates still require teams to implement robot behavior and their
command scheduler lifecycle; compilation is not a robot acceptance test.
