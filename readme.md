# Quanomous

Quanomous lets FTC teams create autonomous routines with Blockly, transfer them as compressed QR codes, and map JSON instructions to robot actions. This repository contains the builder, a Kotlin Android library, and Java/Kotlin consumer examples. Teams define their own instructions and execution behavior.

This is an unpublished consolidation candidate on `codex/consolidate-quanomous`.
The [public builder](https://3droboticsduluth.github.io/Quanomous/) still runs the
older version; use the local builder below to try this PR's BioBuzz template.
There is not yet a public Maven installation URL for this candidate.

## Start here

- [Library installation and API](docs/library.md)
- [Java and Kotlin examples](examples/README.md)
- [Devil Dogs BioBuzz integration walkthrough](docs/biobuzz-example.md)
- [BioBuzz and team-defined templates](docs/templates.md)
- [Architecture and scope](docs/strategy.md)
- [Provenance and attribution](docs/provenance.md)
- [Verification and release](docs/verification.md)

## Run the builder

From the repository root:

```shell
python -m http.server 5500 --bind 127.0.0.1
```

If you have not checked out this candidate, clone it first:

```shell
git clone --branch codex/consolidate-quanomous https://github.com/3DRoboticsDuluth/Quanomous.git
cd Quanomous
```

Run the Python command above in that folder and open http://127.0.0.1:5500/.
Keep the server running. Do not open `index.html` directly as a file: the shared
example loader needs HTTP. BioBuzz is the default; Decode retains legacy blocks.

1. Click **Load BioBuzz example** and confirm replacing the routines.
2. Click **View JSON**. It is the same routine used by both language examples.
3. Follow the [BioBuzz walkthrough](docs/biobuzz-example.md) to try it without hardware.
4. Edit connected blocks and generate QR for your team's scanner.

Only the chain below **Start** is exported. **Partner Start** is preview only.
Switching templates asks before clearing both routines. Disconnected actions or
multiple Start blocks must be resolved before export.

The BioBuzz preview shows explicit drive targets and delays. It does not simulate a team's intake/deposit paths, alignment or offsets. Starting poses and timing are illustrative. Workspaces are not restored after reload. The last payload is stored, but there is no recovery UI.

The service worker caches scripts, the shared example and selected artwork over
localhost/HTTPS. Offline upgrade and real camera use need device acceptance checks.
The broken Bundle control was removed; the old PowerShell bundler is historical
and unsupported. Use a complete checkout for local serving.

## Verify the branch

Use JDK 17, Android SDK platform 34 and Node.js, with JAVA_HOME and ANDROID_HOME configured. On Windows:

```powershell
./scripts/verify.ps1
```

Other platforms can use the commands in [verification](docs/verification.md). Checks publish only into build/repository and consume that artifact from an independent Gradle build. They do not publish remotely or deploy Pages.

## Licensing

The imported library is BSD 3-Clause; see [library/LICENSE](library/LICENSE). Existing web assets retain their original provenance and third-party notices. This branch does not assign a blanket license to artwork or vendored code. See [provenance](docs/provenance.md).
