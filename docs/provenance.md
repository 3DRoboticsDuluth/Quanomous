# Source provenance

This branch preserves the history of `3DRoboticsDuluth/Quanomous`, starting at `4278602e9e56075432701b52ac5aebb6e0e3c986`. Existing web files and the Pages entry remain at the repository root.

The three library classes and original tests were copied from the local BioBuzz `3drdQuanomous` module at `693615fea9fc4d9f61b48cd4d2ecf05bf549ed10`, descended from [LeastOne/NextFTC](https://github.com/LeastOne/NextFTC/tree/main/3drdQuanomous). That module had no local modifications at extraction. Preserve Ian Mulcahy's and other 3D Robotics Duluth contributors' attribution.

Extraction adds independent Android/Maven configuration, timestamp filenames, a Java handler interface and related tests. The shared configuration, dynamic Gson map, direct writes and last-routine hash are retained.

Consolidated sources and tests use `com.threedrd.quanomous` rather than the source
module's FIRST-prefixed package. Maven coordinates use `com.threedrd:quanomous`.

The BioBuzz template follows the uncommitted `TeamCode/.../subsystems/Quanomous.kt` and `docs/robot/autonomous-strategy.md` on local `devildogs/main`, observed November 1, 2026. It is a working template contract, not a physically validated routine implementation. That checkout was not changed.

The library's BSD 3-Clause license comes from `Quanomous/Quanomous` at `5a2f721dc7a1a85004e763f691b603959b5269b4`, copyright 2025 3D Robotics Duluth. See [library/LICENSE](../library/LICENSE). Existing web assets have not been relicensed. Retain Blockly, Pako and Kjua notices; confirm artwork redistribution before distributing a complete builder package. The old repositories remain unchanged.
