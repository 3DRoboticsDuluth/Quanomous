# Builder templates and team instructions

`builder/templates.js` registers BioBuzz defaults and retains the original Decode blocks. Templates supply command names, labels and number/dropdown fields. A generator produces one JSON object per block; export produces a JSON array, GZIP compressed and Base64 encoded.

## BioBuzz default

| Command | Fields | Meaning in the example adapter |
| --- | --- | --- |
| delay | seconds | Fractional seconds, minimum zero |
| chase | cycles | Positive cycle count; zero does not mean unlimited |
| intake | place, side | garden, flower or loading_zone; left or right |
| deposit | side, ao, lo | Destination-relative axial/lateral offsets in tiles |
| park | side | Left or right |
| drive | tx, ty, h, axial, lateral, ao, lo | Signed field coordinates in tiles; degrees; reference alignment and destination-relative offsets |

Offsets default to zero. Axial reference is center/front/back; lateral reference is center/left/right. The example robot uses 23.5 inches per tile and no automatic alliance mirroring for drive coordinates. These are template choices, not library rules.

Deposit emits `ao`/`lo`; the example robot adapter accepts deprecated `txo`/`tyo`
aliases. The example adapters require team-supplied physical actions. The preview
shows only explicit drive targets and delays with illustrative start positions
and timing. Team action paths, alignment and offsets are not simulated or counted
toward the displayed known travel/delay estimate.

Use **Load BioBuzz example** to load the shared nine-step routine and **View JSON**
to inspect exactly what Start exports. Both the examples and the headless builder
check use this same file. Chase's number field accepts whole positive cycles.

## Add a team template

Add an entry to the `templates` object in `builder/templates.js`:

```javascript
myteam: {
  title: 'My team',
  commands: [
    { cmd: 'lift', label: 'Lift', fields: [number('height', 'Height', 1, 0)] }
  ]
}
```

The selector includes it automatically. Register a matching `lift` handler in the robot project; the library needs no changes. For more elaborate field types, extend the helper or use normal Blockly definitions and generators. The helper is not a complete schema language.

The generic preview recognizes the example `drive` and `delay` names. Teams using those names with different fields must adapt or disable the preview; other actions remain at the last known position.

This is developer-authored configuration. A visual custom-block editor and routine import/export remain future features.
