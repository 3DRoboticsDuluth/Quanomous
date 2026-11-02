# Quanomous

Quanomous lets FTC teams create autonomous routines with Blockly, transfer them as compressed QR codes, and map JSON instructions to robot actions. This repository contains the builder, a Kotlin Android library, and Java/Kotlin consumer examples. Teams define their own instructions and execution behavior.

This branch is a local consolidation candidate. Public publishing and deployment have not changed. The existing hosted builder remains at [3droboticsduluth.github.io/Quanomous](https://3droboticsduluth.github.io/Quanomous/).

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

Open http://127.0.0.1:5500/. BioBuzz is the default; Decode retains legacy blocks. Connect actions below Start and generate QR. Partner Start affects preview only. Switching templates asks before clearing both routines.

The BioBuzz preview shows explicit drive targets and delays. It does not simulate a team's intake/deposit paths, alignment or offsets. Starting poses and timing are illustrative. Workspaces are not restored after reload. The last payload is stored, but there is no recovery UI.

The service worker caches scripts and selected artwork over localhost/HTTPS. Offline upgrade and real camera use need acceptance checks. The inherited Bundle feature and PowerShell bundler have not been repaired or certified; use a complete checkout for local serving.

## Verify the branch

Use JDK 17, Android SDK platform 34 and Node.js, with JAVA_HOME and ANDROID_HOME configured. On Windows:

```powershell
./scripts/verify.ps1
```

Other platforms can use the commands in [verification](docs/verification.md). Checks publish only into build/repository and consume that artifact from an independent Gradle build. They do not publish remotely or deploy Pages.

## Licensing

The imported library is BSD 3-Clause; see [library/LICENSE](library/LICENSE). Existing web assets retain their original provenance and third-party notices. This branch does not assign a blanket license to artwork or vendored code. See [provenance](docs/provenance.md).
