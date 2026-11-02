$ErrorActionPreference = 'Stop'
Push-Location (Split-Path $PSScriptRoot -Parent)
try {
    node builder/test-templates.cjs
    if ($LASTEXITCODE -ne 0) { throw 'Builder checks failed.' }
    & ./gradlew.bat :library:check :library:assembleRelease :library:publishReleasePublicationToReviewRepository --max-workers=2 --console=plain
    if ($LASTEXITCODE -ne 0) { throw 'Library checks or local publication failed.' }
    & ./gradlew.bat -p examples :java:assembleRelease :kotlin:assembleRelease --refresh-dependencies --max-workers=2 --console=plain
    if ($LASTEXITCODE -ne 0) { throw 'Consumer checks failed.' }
} finally {
    Pop-Location
}
