"""Offline validation of local Maven output and the two example AARs."""
from pathlib import Path
from zipfile import ZipFile
import io
import xml.etree.ElementTree as ET

root = Path(__file__).resolve().parents[1]
# Pages/Jekyll requires UTF-8 even when a Windows editor accepts local encoding.
for document in [root / 'readme.md', * (root / 'docs').glob('*.md'), root / 'examples/README.md']:
    document.read_text(encoding='utf-8')
repo = root / 'build/repository/io/github/3droboticsduluth/quanomous'
versions = list(repo.glob('*/'))
assert versions, 'Missing local Maven publication'
version = max(versions, key=lambda p: p.stat().st_mtime)
pom = max(version.glob('*.pom'), key=lambda p: p.stat().st_mtime)
ns = {'m': 'http://maven.apache.org/POM/4.0.0'}
model = ET.parse(pom).getroot()
assert model.findtext('m:groupId', namespaces=ns) == 'io.github.3droboticsduluth'
assert model.findtext('m:artifactId', namespaces=ns) == 'quanomous'
for field in ['groupId', 'artifactId', 'version', 'name', 'description', 'url',
              'licenses/license/name', 'developers/developer/name',
              'scm/connection', 'scm/developerConnection']:
    assert model.findtext('m:' + field.replace('/', '/m:'), namespaces=ns), field
for suffix in ['-sources.jar', '-javadoc.jar', '.aar']:
    artifact = max(version.glob('*' + suffix), key=lambda p: p.stat().st_mtime)
    with ZipFile(artifact) as archive:
        assert archive.namelist(), f'Empty {artifact.name}'
        if suffix == '-javadoc.jar':
            assert any(name.endswith('.html') for name in archive.namelist()), 'Missing API HTML'
asset = (root / 'examples/java/src/main/assets/biobuzz-route.json').read_bytes()
for language in ['java', 'kotlin']:
    with ZipFile(root / f'examples/{language}/build/outputs/aar/{language}-release.aar') as archive:
        assert archive.read('assets/biobuzz-route.json') == asset
        with ZipFile(io.BytesIO(archive.read('classes.jar'))) as classes:
            for name in ['QuanomousCommandOpMode', 'QuanomousStateMachineOpMode']:
                assert f'org/firstinspires/ftc/teamcode/{name}.class' in classes.namelist()
print('PASS: Maven metadata, AAR/sources/API docs, four templates and canonical assets')
