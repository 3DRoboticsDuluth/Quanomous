// You will need each of the blocks to have a javascript function that returns a JSON string for the command.
// The start block can most likely stay the same throughout seasons, but the other blocks will need to be updated to ensure that they work in the new season.
// The blocks are defined in this file using the Blockly.Blocks object, and the javascript functions are defined using the Blockly.JavaScript object.
// Each block has a unique name, and each javascript function has a unique name that corresponds to that block name.

// START BLOCK
// NOTE: The generators in this file return JSON-encoded strings for each command.
// The decoding implementation (Android/FTC) expects an outer JSON array of these
// strings. Keep keys/names consistent with the decoder here:
// https://github.com/3DRoboticsDuluth/11206-2025-Decode.git  <-- review command schema there.
Blockly.Blocks['start'] = {
  init: function() {
    this.appendDummyInput().appendField("▶ Start");
    this.setNextStatement(true);
    this.setColour("#f9c74f");
    this.setDeletable(true);
  }
};

// PARTNER START BLOCK
Blockly.Blocks['partner_start'] = {
  init: function() {
    this.appendDummyInput().appendField("▶ Partner Start");
    this.setNextStatement(true);
    this.setColour("#ff6b9d");
    this.setDeletable(true);
    this.setTooltip("Start block for your alliance partner's robot");
  }
};

// Debug: confirm partner_start block is defined
if (typeof console !== 'undefined') {
  console.info('Partner start block defined:', typeof Blockly.Blocks['partner_start']);
}

// Start generator: walk next blocks and call their registered generator functions directly.
Blockly.JavaScript['start'] = function(block) {
  let next = block.getNextBlock();
  const plan = [];
  while (next) {
    try {
      const gen = Blockly.JavaScript[next.type];
      if (typeof gen === 'function') {
        const code = gen(next);
        if (code && code !== 'undefined') {
          // Parse the JSON string to get the actual object
          try {
            const obj = JSON.parse(code);
            plan.push(obj);
          } catch (e) {
            console.warn('Failed to parse JSON for', next.type, ':', code);
            plan.push({cmd: next.type, error: 'parse_failed'});
          }
        }
      } else {
        // fallback minimal serialization
        plan.push({cmd: next.type});
      }
    } catch (e) {
      console.warn('Generator error for', next.type, e);
      plan.push({cmd: next.type, error: 'generator_failed'});
    }
    next = next.getNextBlock();
  }
  return JSON.stringify(plan);
};

// DRIVE TO (tile-based)
Blockly.Blocks['drive_to'] = {
  init: function() {
    this.appendDummyInput()
      .appendField("Drive to tile X:")
      .appendField(new Blockly.FieldNumber(0), "tx")
      .appendField("Y:")
      .appendField(new Blockly.FieldNumber(0), "ty")
      .appendField("heading:")
      .appendField(new Blockly.FieldNumber(0,0,360), "h")
      .appendField("Axial:")
      .appendField(new Blockly.FieldDropdown(
        [["Center","center"],["Front","front"], ["Back","back"]]
      ))
      .appendField("Lateral:")
      .appendField(new Blockly.FieldDropdown(
        [["Center","center"],["Left","left"], ["Right","right"]]
      ), "Lateral");
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setColour("#43aa8b");
    this.setTooltip("Drive to a tile position with heading");
  }
};
Blockly.JavaScript['drive_to'] = function(block){
  const tx = Number(block.getFieldValue('tx'))||0;
  const ty = Number(block.getFieldValue('ty'))||0;
  const h = Number(block.getFieldValue('h'))||0;
  const axial = block.getFieldValue('Axial');
  const lateral = block.getFieldValue('Lateral');
  return JSON.stringify({cmd:'drive', tx, ty, h, axial, lateral});
};

// INTAKE ROW (0-3, where 0 = human)
Blockly.Blocks['intake_row'] = {
  init: function() {
    this.appendDummyInput()
      .appendField("Intake")
      .appendField(new Blockly.FieldDropdown([
        ["Human (0)","0"],
        ["Spike 1","1"],
        ["Spike 2","2"],
        ["Spike 3","3"]
      ]),"spike");
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setColour("#577590");
  }
};
Blockly.JavaScript['intake_row'] = function(block){
  const spike = Number(block.getFieldValue('spike'))||0;
  return JSON.stringify({cmd:'intake', spike});
};

// INTAKE HUMAN (shortcut for spike 0)
Blockly.Blocks['intake_human'] = {
  init: function() {
    this.appendDummyInput().appendField("Intake Human");
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setColour("#577590");
  }
};
Blockly.JavaScript['intake_human'] = function(block){
  return JSON.stringify({cmd:'intake', spike:0});
};

// GATE INTAKE (open gate to get artifacts off of the classifier)
Blockly.Blocks['intake_gate'] = {
  init: function(){
    this.appendDummyInput().appendField("Intake gate");
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setColour("#577590");
  }
};
Blockly.JavaScript['intake_gate'] = function(block){
  return JSON.stringify({cmd:'intake_gate'});
};

// DEPOSIT (unified block with locale dropdown and optional offsets)
Blockly.Blocks['deposit'] = {
  init: function(){
    this.appendDummyInput()
      .appendField("Deposit at")
      .appendField(new Blockly.FieldDropdown([
        ["Near","near"],
        ["Far","far"]
      ]), "locale")
      .appendField("sorted?")
      .appendField(new Blockly.FieldDropdown([
        ["No","false"],
        ["Yes","true"]
      ]), "sorted");
    this.appendDummyInput()
      .appendField("Tile offset X:")
      .appendField(new Blockly.FieldNumber(0), "txo")
      .appendField("Y:")
      .appendField(new Blockly.FieldNumber(0), "tyo");
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setColour("#277da1");
    this.setTooltip("Deposit at near/far location with optional tile offsets");
  }
};
Blockly.JavaScript['deposit'] = function(block){
  const locale = block.getFieldValue('locale');
  const sorted = block.getFieldValue('sorted') === 'true';
  const txo = Number(block.getFieldValue('txo'))||0;
  const tyo = Number(block.getFieldValue('tyo'))||0;
  return JSON.stringify({cmd:'deposit', locale, sorted, txo, tyo});
};

// DELAY (milliseconds output)
Blockly.Blocks['delay_s'] = {
  init: function(){
    this.appendDummyInput()
      .appendField("Delay for")
      .appendField(new Blockly.FieldNumber(1,0),"s")
      .appendField("s");
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setColour("#f94144");
  }
};
Blockly.JavaScript['delay_s'] = function(block){
  const s = Number(block.getFieldValue('s'))||0;
  const seconds = Math.round(s);
  return JSON.stringify({cmd:'delay', seconds});
};

// RELEASE GATE
Blockly.Blocks['release_gate'] = {
  init: function(){
    this.appendDummyInput().appendField("Release Gate");
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setColour("#b5179e");
  }
};
Blockly.JavaScript['release_gate'] = function(block){
  return JSON.stringify({cmd:'release'});
};

// CHASE ARTIFACTS
Blockly.Blocks['chase'] = {
  init: function(){
    this.appendDummyInput()
      .appendField("Chase")
      .appendField(new Blockly.FieldNumber(1), "cycles")
      .appendField("cycles");
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setColour("#277da1");
    this.setTooltip("Chase artifacts for a number of cycles)");
  }
};
Blockly.JavaScript['chase'] = function(block){
  const cycles = Number(block.getFieldValue('cycles'))||0;
  return JSON.stringify({cmd:'chase', cycles});
};    

// PARK BLOCK
Blockly.Blocks['park'] = {
  init: function() {
    this.appendDummyInput()
      .appendField("Park Axial:")
      .appendField(new Blockly.FieldDropdown(
        [["Center","center"],["Front","front"], ["Back","back"]]
      ), "Axial")
      .appendField("Lateral:")
      .appendField(new Blockly.FieldDropdown(
        [["Center","center"],["Left","left"], ["Right","right"]]
      ), "Lateral")
      .appendField("Gate")
      .appendField(new Blockly.FieldDropdown(
        [["false", "false"],["true", "true"]]
      ), "Gate");
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setColour("#41006c");
    this.setTooltip("Park with axial and lateral alignment");
  }
};
Blockly.JavaScript['park'] = function(block){
  const axial = block.getFieldValue('Axial');
  const lateral = block.getFieldValue('Lateral');
  const gate = block.getFieldValue('Gate') === 'true';
  return JSON.stringify({cmd:'park', axial, lateral, gate});
};

// Debug: log which generators are present after definitions
if (typeof console !== 'undefined' && Blockly && Blockly.JavaScript) {
  try {
    const names = ['start','drive_to','intake_row','intake_human','delay_s','deposit','release_gate', 'chase', 'park', 'gate_intake'];
    names.forEach(n => console.info('blocks_custom: generator present ->', n, typeof Blockly.JavaScript[n] === 'function'));
  } catch (e) { /* ignore */ }
}