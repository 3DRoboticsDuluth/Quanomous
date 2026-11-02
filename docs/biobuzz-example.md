# BioBuzz instruction contract

All [four examples](../examples/README.md) use the builder's default BioBuzz
instructions, based on Devil Dogs' team contract. Choose a plain LinearOpMode or
a command-based OpMode in Java or Kotlin. Each loads the same
[nine-step routine](../examples/java/src/main/assets/biobuzz-route.json) used by
**Load BioBuzz example** in the builder. Use **View JSON** to inspect it.

The routine waits 0.4 seconds, drives to a signed pose, deposits preloads, intakes
from the garden and deposits with an offset, intakes from the flower and deposits
again, chases for two cycles, then parks. It is an illustrative composition,
not a measured competition route. Team hardware and navigation are represented by private methods that teams
implement; the examples focus on JSON interpretation.

| Instruction | Fields |
| --- | --- |
| delay | seconds |
| drive | tx, ty, h, axial, lateral, ao, lo |
| intake | place, side |
| deposit | side, ao, lo |
| chase | cycles |
| park | side |

For Devil Dogs, a tile is 23.5 inches. Drive tx/ty are signed field coordinates;
heading h is degrees. Axial alignment is front/center/back; lateral alignment
is left/center/right. Offsets ao/lo are destination-relative axial/lateral
values in tiles. Apply alignment/offsets in the team's navigation code once.
Intake places are garden, flower and loading_zone; sides are left and right.
Chase uses a positive integer cycle count.

The builder emits current field names. Teams supporting older routines can add
legacy deposit txo/tyo aliases, with ao/lo taking precedence even when zero.
The examples consume current builder output; they do not provide a legacy
compatibility layer. Each team owns parameter validation and defaults.

Follow the [LinearOpMode walkthrough](state-machine-example.md) for direct JSON
interpretation in a while loop, or the [command walkthrough](command-example.md)
for JSON-to-command factories. Both compile/load during Init and execute after
Start. The library decodes/stores JSON and offers a generic handler map; it does
not know a team's hardware, instructions or command framework.

Change the builder template and robot interpretation together when customizing
instructions. See [template configuration](templates.md). The same contract can
be executed by different frameworks; no command framework is required by the library.
