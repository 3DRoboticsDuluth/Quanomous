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
