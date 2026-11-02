# Final acceptance and release decision

## Audit result â€” November 2, 2026

Reviewed source head: ea39ed315855ea82c3c1827d8b31d392e526e5bb.
Both hosted Ubuntu CI runs passed:
[PR CI](https://github.com/3DRoboticsDuluth/Quanomous/actions/runs/41410374970),
[branch CI](https://github.com/3DRoboticsDuluth/Quanomous/actions/runs/41410369995).
This document's follow-up commit must also have green CI before merge.

The final audit found no new code blocker within the agreed scope. It checked
library/handler boundaries, the four framework-neutral templates and canonical
asset, builder export/cache configuration, and CI/release workflow boundaries.
Previous browser checks and local signing results are recorded in
[verification](verification.md) and [release operations](release.md).
No speculative hardening or robot-repository changes are part of this release.

Live settings were rechecked: Pages publishes main at / with HTTPS; the
maven-central environment permits only main, has no required reviewers and no
admin bypass, and contains all four expected secret names. Values were not
printed. Maven coordinates are io.github.3droboticsduluth:quanomous; package and
Android namespace remain com.threedrd.quanomous.

## Builder acceptance and deferred camera follow-up

Use the consolidation branch served locally, following the root README. Do not
use the current production Pages app to judge the unpublished BioBuzz changes.
For a phone/robot on another device, serve the candidate at an address it can
reach. Service-worker/offline checks require a secure context: HTTPS, or
localhost on the same device. Plain HTTP to a LAN IP is not an offline test.
Do not change production Pages settings just to perform acceptance.

Record device/browser, camera model and candidate commit with the result.

Maintainer decision, November 2, 2026: no camera is currently available. The real
camera scan and repeated scan/store checks below are deferred until equipment is
available and are not prerequisites for the initial release. They remain unchecked;
CI/fixtures do not substitute for hardware acceptance.

- [ ] Load BioBuzz example, then View JSON. Compare the full output with
  [the canonical asset](../examples/java/src/main/assets/biobuzz-route.json).
  Expect nine instructions: delay, drive, deposit, intake, deposit, intake,
  deposit, chase, park. The first delay is 0.4; drive is tx=1, ty=-2, h=90;
  the second deposit has ao=0.5 and lo=-0.25; chase is 2; park is right.
- [ ] **Deferred — camera unavailable.** Generate QR on the intended display and scan using the team's actual
  camera/scanner. Decode the returned barcode text with Quanomous and compare
  the entire JSON array with the asset, including fractional/signed values.
  Inspect decoded data without scheduling robot motion. The supplied OpMode
  templates intentionally throw until team methods are implemented.
- [ ] **Deferred — camera unavailable.** Scan/store the same image repeatedly using the team's ingestion path.
  Confirm the same routine is selected rather than repeatedly starting Auto.
  Rescan a changed routine and verify it saves/loads with the changed values.
- [ ] On the intended phone screen, verify the editor, View JSON, QR and return
  to editor controls remain usable. The preview excludes team action durations;
  it is not a collision or complete runtime simulation.
- [ ] Switch BioBuzz â†’ Decode â†’ BioBuzz, testing cancellation and confirmation
  of clearing. Verify that the intended template is selected before QR export.
- [x] **Passed locally, November 2, 2026.** Test offline refresh after the service worker has installed and the page
  has been reopened online. Load the sample and render QR while offline.
- [x] **Passed locally, November 2, 2026.** Test an upgrade from the old builder to the candidate at the same test
  origin. Reopen/reload after worker activation and confirm new controls/sample
  are loaded, with no mixed old assets. Do not treat a new incognito session as
  a cache-upgrade test.

## Signing recovery

- [ ] **Deferred by maintainer, November 2, 2026; not blocking this deployment.** Arrange and test a portable organizational backup of the current signing
  key, passphrase and revocation material in organization-controlled secure
  storage. The existing DPAPI backup is tied to this Windows account/machine
  context and is not proof of recovery elsewhere. Do not commit backup material.

## Decision and authorized release sequence

1. Record acceptance results above; leave unresolved checks unchecked. The camera
   checks are deferred by maintainer decision and need not block the initial release.
2. Confirm final PR CI is green and approve the final code/documents.
3. Obtain explicit approval to merge AND deploy: current Pages settings make
   those the same action. This audit is not that approval.
4. After authorized merge, verify the public builder at its existing URL,
   especially service-worker upgrade and the real QR scan.
5. When library staging is authorized, create v0.1.0 on the reviewed main commit
   and manually dispatch Stage Maven Central release from main with 0.1.0.
   There is no required-reviewer gate: dispatch proceeds after verification.
6. Review Central Portal validation. Only explicitly publish when authorized.
7. Resolve io.github.3droboticsduluth:quanomous:0.1.0 from mavenCentral() in a clean
   FTC project, compile Java/Kotlin consumers and update public install docs.

No merge, deployment, tag creation, staging/upload or publication was performed
as part of this audit. The remaining hardware/offline/recovery checks are not
claimed complete by successful CI or compilation.

## Local offline/cache evidence — November 2, 2026

Tested in isolated headless Chrome on localhost under /Quanomous/, the same
path shape as production. The server sent Cache-Control: no-store to prevent
HTTP caching from hiding missing service-worker entries. A fresh context first
installed origin/main's ftc-qr-cache-v1. After switching served files to the
candidate, ordinary reload/registration activated ftc-qr-cache-review-6; no
forced update(), storage clearing or new browser context was used for the upgrade.
The old cache was removed and SHA-256 comparisons confirmed all 18 cached
responses matched the candidate files, including HTML, scripts, artwork and JSON.

With network disabled (navigator.onLine=false), refresh, exact nine-step sample
JSON, SVG QR rendering, return to editor, Decode/BioBuzz switching and sample
reload passed. No page errors or failed resource requests occurred in that phase.
The test found and fixed the missing sw_register.js precache entry, bumped the
cache version and added a headless Node regression check covering every shell
script and the existence of all precached files.

This is local Chrome evidence, not a physical-device/camera result. The public
Pages cache upgrade still needs a smoke check after authorized deployment.

Maintainer authorization, November 2, 2026: defer portable signing-key backup,
merge the consolidation PR and deploy the builder. Library staging/publication
is explicitly excluded and remains a separate authorization step.
