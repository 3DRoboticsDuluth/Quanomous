// Teams may add a template and command generators here. The Kotlin library
// dispatches by cmd and does not prescribe these fields or their meaning.
(function (root) {
  const side = [['Left', 'left'], ['Right', 'right']];
  const number = (key, label, value = 0, min) => ({ key, label, value, min });
  const choice = (key, label, options) => ({ key, label, options });
  const templates = {
    biobuzz: {
      title: 'BioBuzz',
      commands: [
        { cmd: 'delay', label: 'Delay', fields: [number('seconds', 'Seconds', 1, 0)] },
        { cmd: 'chase', label: 'Chase', fields: [number('cycles', 'Cycles', 1, 1)] },
        { cmd: 'intake', label: 'Intake', fields: [
          choice('place', 'Place', [['Garden', 'garden'], ['Flower', 'flower'], ['Loading Zone', 'loading_zone']]),
          choice('side', 'Side', side)
        ] },
        { cmd: 'deposit', label: 'Deposit', fields: [
          choice('side', 'Side', side), number('ao', 'Axial offset (tiles)'), number('lo', 'Lateral offset (tiles)')
        ] },
        { cmd: 'park', label: 'Park', fields: [choice('side', 'Side', side)] },
        { cmd: 'drive', label: 'Drive', fields: [
          number('tx', 'Field X (tiles)'), number('ty', 'Field Y (tiles)'), number('h', 'Heading (degrees)'),
          choice('axial', 'Align axial', [['Center', 'center'], ['Front', 'front'], ['Back', 'back']]),
          choice('lateral', 'Align lateral', [['Center', 'center'], ['Left', 'left'], ['Right', 'right']]),
          number('ao', 'Axial offset (tiles)'), number('lo', 'Lateral offset (tiles)')
        ] }
      ]
    }
  };

  for (const [name, template] of Object.entries(templates)) {
    for (const command of template.commands) {
      const type = `${name}_${command.cmd}`;
      Blockly.Blocks[type] = {
        init() {
          this.appendDummyInput().appendField(command.label);
          for (const field of command.fields) {
            this.appendDummyInput().appendField(field.label).appendField(
              field.options ? new Blockly.FieldDropdown(field.options) :
                new Blockly.FieldNumber(field.value, field.min), field.key);
          }
          this.setPreviousStatement(true);
          this.setNextStatement(true);
          this.setColour('#43aa8b');
        }
      };
      Blockly.JavaScript[type] = block => JSON.stringify(Object.fromEntries([
        ['cmd', command.cmd],
        ...command.fields.map(field => [field.key, field.options ? block.getFieldValue(field.key) : Number(block.getFieldValue(field.key))])
      ]));
    }
  }

  const api = root.QuanomousTemplates = {
    templates,
    active: 'biobuzz',
    // Only explicit drive and delay steps can be previewed without a team's
    // navigation implementation. Named actions remain at the last known pose.
    preview(startBlock, startPose, tileWidth) {
      const path = [];
      let pose = { ...startPose };
      for (let block = startBlock.getNextBlock(); block; block = block.getNextBlock()) {
        const generate = Blockly.JavaScript[block.type];
        if (typeof generate !== 'function') continue;
        const step = JSON.parse(generate(block));
        if (step.cmd === 'drive') pose = { x: step.tx * tileWidth, y: step.ty * tileWidth, heading: step.h * Math.PI / 180 };
        path.push({ ...pose, type: step.cmd === 'delay' ? 'delay' : step.cmd === 'drive' ? 'drive' : 'unmodeled',
          delayTime: step.seconds || 0, label: step.cmd === 'delay' ? `Wait ${step.seconds}s` : step.cmd });
      }
      return path;
    }
  };

  if (typeof document === 'undefined') return;
  const toolbox = document.getElementById('toolbox');
  const decodeToolbox = toolbox.innerHTML;
  const select = document.getElementById('template');
  const note = document.getElementById('template-note');
  select.replaceChildren();
  for (const [key, template] of Object.entries(templates)) select.add(new Option(template.title, key));
  select.add(new Option('Decode (legacy)', 'decode'));
  select.value = api.active;
  function applyTemplate() {
    if (api.active === 'decode') {
      toolbox.innerHTML = decodeToolbox;
      note.textContent = 'Legacy Decode commands and approximate robot-specific preview.';
    } else {
      toolbox.innerHTML = `<category name="Start" colour="#f9c74f"><block type="start"></block></category>
        <category name="Partner" colour="#ff6b9d"><block type="partner_start"></block></category>
        <category name="Actions" colour="#43aa8b">${templates[api.active].commands.map(c => `<block type="${api.active}_${c.cmd}"></block>`).join('')}</category>`;
      note.textContent = `${templates[api.active].title} template: preview shows explicit drive targets and delays only. Team actions, alignment and offsets are not simulated. Start positions and timing are illustrative.`;
    }
  }
  applyTemplate();
  select.addEventListener('change', () => {
    if (!root.confirm('Switching templates clears both routines. Continue?')) { select.value = api.active; return; }
    api.active = select.value;
    applyTemplate();
    workspace.updateToolbox(toolbox);
    document.getElementById('clearBtn').click();
  });
})(typeof window === 'undefined' ? globalThis : window);
