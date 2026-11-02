// Run deliberately when the BioBuzz example contract changes; commit the result.
const fs = require('node:fs');
const path = require('node:path');
const pako = require('../pako.min.js');
const steps = [
  {cmd:'delay', seconds:0.4},
  {cmd:'chase', cycles:2},
  {cmd:'intake', place:'loading_zone', side:'right'},
  {cmd:'deposit', side:'left', ao:0.5, lo:-0.25},
  {cmd:'park', side:'right'},
  {cmd:'drive', tx:1, ty:-2, h:90, axial:'front', lateral:'left', ao:0.25, lo:-0.5}
];
const encoded = Buffer.from(pako.gzip(JSON.stringify(steps))).toString('base64');
fs.writeFileSync(path.join(__dirname, 'biobuzz.json'), JSON.stringify({steps, encoded}, null, 2) + '\n');
