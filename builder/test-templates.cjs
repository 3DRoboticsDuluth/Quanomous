const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');
const root = path.resolve(__dirname, '..');
const context = { console: { info() {}, warn() {} }, setTimeout, clearTimeout };
vm.createContext(context);
for (const file of ['blockly.min.js', 'blocks_custom.js', 'pako.min.js', 'builder/templates.js']) {
  vm.runInContext(fs.readFileSync(path.join(root, file), 'utf8'), context);
}
context.Blockly.Events.disable();
const workspace = new context.Blockly.Workspace();
const fixture = JSON.parse(fs.readFileSync(path.join(root, 'fixtures/biobuzz.json'), 'utf8'));
const start = workspace.newBlock('start');
let previous = start;
const blocks = [];
for (const expected of fixture.steps) {
  const block = workspace.newBlock('biobuzz_' + expected.cmd);
  for (const [key, value] of Object.entries(expected)) {
    if (key !== 'cmd') block.setFieldValue(String(value), key);
  }
  previous.nextConnection.connect(block.previousConnection);
  blocks.push(block);
  previous = block;
}
// Exercise the application's actual export function, including its init/finish calls.
const app = fs.readFileSync(path.join(root, 'app.js'), 'utf8');
context.workspace = workspace;
vm.runInContext(app.slice(app.indexOf('function generatePlanJSON()'), app.indexOf('// WINDOW RESIZE & ORIENTATION HANDLERS')), context);
const actual = JSON.parse(JSON.stringify(context.generatePlanJSON()));
assert.deepEqual(actual, fixture.steps);
const encoded = Buffer.from(context.pako.gzip(JSON.stringify(actual))).toString('base64');
assert.equal(encoded, fixture.encoded);
assert.deepEqual(JSON.parse(context.pako.ungzip(Buffer.from(encoded, 'base64'), { to: 'string' })), actual);
const preview = context.QuanomousTemplates.preview(start, { x: 0, y: 0, heading: 0 }, 23.5);
const drive = preview.find(w => w.type === 'drive');
assert.equal(drive.x, 23.5);
assert.equal(drive.y, -47);
assert.equal(preview[0].delayTime, 0.4);
assert.ok(preview.some(w => w.type === 'unmodeled'));
// The old Decode definitions remain available after the new template registers.
assert.equal(typeof context.Blockly.JavaScript.intake_gate, 'function');
assert.equal(typeof context.Blockly.JavaScript.park, 'function');
const legacyWorkspace = new context.Blockly.Workspace();
const legacyDrive = legacyWorkspace.newBlock('drive_to');
legacyDrive.setFieldValue('front', 'Axial');
assert.equal(JSON.parse(context.Blockly.JavaScript.drive_to(legacyDrive)).axial, 'front');
const legacyDelay = legacyWorkspace.newBlock('delay_s');
legacyDelay.setFieldValue('0.4', 's');
assert.equal(JSON.parse(context.Blockly.JavaScript.delay_s(legacyDelay)).seconds, 0.4);
legacyWorkspace.dispose();
// The documented consumer example must be expressible by the same default blocks.
const route = JSON.parse(fs.readFileSync(path.join(root, 'examples/java/src/main/assets/biobuzz-route.json'), 'utf8'));
const routeWorkspace = new context.Blockly.Workspace();
context.QuanomousTemplates.loadExample(routeWorkspace, route);
context.workspace = routeWorkspace;
assert.deepEqual(JSON.parse(JSON.stringify(context.generatePlanJSON())), route);
context.workspace = workspace;
routeWorkspace.dispose();
const extraStart = workspace.newBlock('start');
assert.throws(() => context.generatePlanJSON(), /one Start block/);
extraStart.dispose();
const loose = workspace.newBlock('biobuzz_delay');
assert.throws(() => context.generatePlanJSON(), /Connect every action/);
loose.dispose();
const delayGenerator = context.Blockly.JavaScript.biobuzz_delay;
context.Blockly.JavaScript.biobuzz_delay = () => { throw new Error('Broken team generator'); };
assert.throws(() => context.generatePlanJSON(), /Broken team generator/);
context.Blockly.JavaScript.biobuzz_delay = delayGenerator;
// The actual Clear handler must clear a connected graph without disposing its
// children twice. UI-only collaborators are irrelevant to this regression.
let clearHandler;
const clearInfo = { textContent: 'previous payload' };
const clearQr = { innerHTML: 'previous QR' };
context.document = { getElementById(id) { return {
  clearBtn: { addEventListener(_, callback) { clearHandler = callback; } },
  info: clearInfo, qr: clearQr, 'routine-json': { value: 'stale JSON' }, 'json-details': { open: true }
}[id]; } };
context.stopAnimation = () => {};
context.ensureStartBlock = () => {};
context.updateVisualization = () => {};
context.safeLocalStorageRemove = () => {};
vm.runInContext(app.slice(app.indexOf("document.getElementById('clearBtn').addEventListener"),
  app.indexOf('// PLAN JSON GENERATION')), context);
clearHandler();
assert.equal(workspace.getAllBlocks(false).length, 0);
assert.equal(clearInfo.textContent, '');
assert.equal(clearQr.innerHTML, '');
// newBlock already registers top-level blocks. The application's initializer
// must not register them a second time (which breaks browser disposal).
const originalNewBlock = workspace.newBlock.bind(workspace);
workspace.newBlock = function(type) {
  const block = originalNewBlock(type);
  block.initSvg = block.render = block.moveBy = () => {};
  return block;
};
const ensureStart = app.indexOf('function ensureStartBlock()');
vm.runInContext(app.slice(ensureStart, app.indexOf('\n}', ensureStart) + 2), context);
context.ensureStartBlock();
context.ensureStartBlock();
assert.equal(workspace.getTopBlocks(false).length, 2);
workspace.clear();
assert.equal(workspace.getAllBlocks(false).length, 0);
// Exercise selector initialization and clearing without a browser DOM.
const selector = { value: '', options: [], replaceChildren() { this.options = []; },
  add(option) { this.options.push(option); }, addEventListener(_, callback) { this.change = callback; } };
const toolbox = { innerHTML: '<legacy-toolbox />' };
const note = { textContent: '' };
let clears = 0;
let updates = 0;
context.document = { getElementById(id) {
  return { template: selector, toolbox, 'template-note': note,
    clearBtn: { click() { clears++; } } }[id];
} };
context.Option = function(text, value) { this.text = text; this.value = value; };
context.workspace = { updateToolbox() { updates++; } };
context.confirm = () => false;
vm.runInContext(fs.readFileSync(path.join(root, 'builder/templates.js'), 'utf8'), context);
assert.equal(selector.value, 'biobuzz');
assert.equal(selector.options.length, 2);
assert.match(toolbox.innerHTML, /biobuzz_drive/);
selector.value = 'decode';
selector.change();
assert.equal(selector.value, 'biobuzz');
assert.equal(clears, 0);
context.confirm = () => true;
selector.value = 'decode';
selector.change();
assert.equal(context.QuanomousTemplates.active, 'decode');
assert.equal(toolbox.innerHTML, '<legacy-toolbox />');
assert.equal(clears, 1);
assert.equal(updates, 1);
console.log('PASS: all six BioBuzz commands, signed coordinates, fractional delay, offsets, legacy registration and compressed fixture');
console.log('PASS: template selector, switch cancellation and confirmed clearing');
console.log('PASS: Clear handles a connected block graph and removes old output');
console.log('PASS: documented BioBuzz routine matches builder export');
console.log('PASS: invalid generators, duplicate starts and disconnected actions block export');

// Every local script loaded by the shell must be available after an offline refresh.
const shellHtml = fs.readFileSync(path.join(root, 'index.html'), 'utf8');
const workerSource = fs.readFileSync(path.join(root, 'sw.js'), 'utf8');
const cachedFiles = vm.runInNewContext(workerSource.match(/const FILES_TO_CACHE = (\[[\s\S]*?\]);/)[1]);
for (const match of shellHtml.matchAll(/<script[^>]+src="([^"]+)"/g)) {
  if (!/^(https?:)?\/\//.test(match[1])) {
    assert.ok(cachedFiles.includes('./' + match[1].replace(/^\.\//, '')), 'Uncached shell script: ' + match[1]);
  }
}
for (const file of cachedFiles) assert.ok(fs.existsSync(path.join(root, file)), 'Missing cached file: ' + file);
console.log('PASS: offline cache includes every shell script and references existing files');
