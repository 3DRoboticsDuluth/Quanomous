Quanomous will be maintained in the existing `3DRoboticsDuluth/Quanomous` repository. It will contain the QR builder, a publishable Kotlin library, integration examples and documentation. Keep the repository name and existing Pages URL.

Decision updated November 1, 2026. This replaces the earlier two-repository and Java-core proposal. Implementation starts on a local review branch; publishing, deployment and archival remain later cutover work.

# Repository layout

| Location | Responsibility |
| --- | --- |
| Web files at repository root | Existing builder and Pages entry point; preserve hosting layout during the initial consolidation |
| `builder/` | Replaceable team and season templates, starting with BioBuzz |
| `library/` | Kotlin Android library for Base64/GZIP decoding, JSON storage and dynamic instruction compilation |
| `examples/` | Independent Java and Kotlin consumers of the locally published artifact |
| `docs/` | Architecture, installation, template contract, provenance and release steps |

Move web files only when deployment can publish them at the same site root. A repository rename is unnecessary. The repository can publish both a library artifact and a web application without giving them separate source repositories.

# Dynamic instructions

Retain the existing `QuanomousCompiler<T>` approach: teams register command names and JSON handlers that construct their own values. Those values can be NextFTC commands, SolversLib commands, state-machine actions or another representation.

The library handles decoding, parsing, storage, instruction dispatch and unknown-command errors. Teams own fields, defaults, validation and behavior. There is no universal built-in drive, intake or deposit contract.

Handler callbacks construct actions; robot execution happens afterward. Keep complete list construction before scheduling. Do not add a general scheduler or a mandatory typed-instruction hierarchy.

The initial builder template follows the local BioBuzz adapter: delay, chase, intake, deposit, park and drive; signed field coordinates; ao/lo offsets. This is a replaceable template, not a library requirement. Retain the Decode template for existing routines. Adding a visual custom-block editor is subsequent work; the first template mechanism must allow team-defined JSON handlers without core changes.

# Kotlin and Java

Use the organization-owned `com.threedrd` convention: Quanomous source packages
and Android namespace are `com.threedrd.quanomous`, and Maven coordinates are
`com.threedrd:quanomous`. The existing robot repositories are outside this migration.

Keep implementation in Kotlin. Publish an Android AAR with the existing minimum Android API 24 and Java 8 bytecode target. This preserves Android Base64 support without adding an FTC SDK or command framework dependency.

Keep the current Kotlin map API. Add a small Java-friendly handler registration interface and verify both language consumers against the published artifact. The Kotlin runtime and Gson are normal transitive dependencies. Maven coordinates used for local verification are provisional until public hosting is chosen.

# Processing and storage decisions

Preserve the singleton, exposed storage/decoder/clock and duplicate-tracking configuration. Keep processing unsynchronized, direct file writes and decoding without a decompressed-size limit. These deliberate scope choices favor the actual single-loop QR workflow over hypothetical general-purpose hardening.

Duplicate suppression remembers the last routine in memory. Do not add a filesystem-wide deduplication index. Preserve updating remembered state only after a successful save.

Replace the file-count suffix with seconds and milliseconds: `MM-dd-HHmm-ssSSS.json`. Repeated identical QR observations are filtered by the hash. Same-millisecond collisions for different routines are an accepted practical limitation.

# Consolidation sources

Preserve builder history in its current repository. Extract the three Kotlin classes and their tests from `LeastOne/NextFTC` lineage; record the exact source revision. Use the current BioBuzz handler contract as the builder default without copying or modifying that robot’s unfinished action implementations.

Keep attribution and applicable licenses with imported code. The old library’s BSD 3-Clause license names 3D Robotics Duluth; Quickstart also has a BSD-licensed website copy. Do not assume that settles licensing for every existing web asset or third-party dependency.

The Java Decode and Quickstart implementations are compatibility references and sources for later integration examples. Preserve legacy stored routines when adapting their consumers.

# First milestone and verification

Produce a local branch that builds the library independently, publishes it into a repository-local Maven directory, and compiles/runs Java and Kotlin consumer checks against that artifact. Test QR-compatible compressed payloads, team-owned handlers, storage and filename behavior.

Use headless Blockly checks to verify that builder templates emit the intended JSON and round-trip compressed output through the library. Test the actual BioBuzz field names and retain the legacy template.

Keep the hosted builder unchanged until its new default, preview limitations and deployment are reviewed. Full physical robot, camera and tournament-offline acceptance remain follow-up work.

# Migration and release sequence

1. Complete and review the local consolidation branch.
2. Choose public artifact hosting and final coordinates; verify installation from a clean FTC consumer.
3. Coordinate public documentation and the organization website links.
4. Verify Pages publishing at the existing URL before promoting the builder.
5. Add migration notices to the old Quanomous organization repositories when source admin access is available.
6. Archive superseded repositories only after replacement examples and installation work.

No transfer into the builder’s occupied repository name is needed. Source-side permissions are still needed to edit or archive the old repositories, but they do not block preparing the maintained implementation.

# Ownership and follow-up scope

3DRoboticsDuluth owns maintenance, hosting and publishing. Assign maintainers and preserve contributor attribution. Add framework-specific examples and a cooperative state-machine example after the independent library baseline.

A team-facing custom-block authoring interface, detailed BioBuzz simulation and physical execution validation are distinct follow-up features. Keep current limitations explicit rather than adding speculative runtime infrastructure.
