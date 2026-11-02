package org.firstinspires.ftc.teamcode

/** A complete first-use adapter: report the BioBuzz steps without moving hardware. */
fun bioBuzzDryRun(report: (String) -> Unit): KotlinExample<() -> Unit> =
    KotlinExample(object : KotlinExample.Auto<() -> Unit> {
        private fun message(text: String): () -> Unit = { report(text) }
        override fun delay(seconds: Double) = message("Delay $seconds seconds")
        override fun chase(cycles: Int) = message("Chase $cycles cycles")
        override fun intake(place: KotlinExample.Place, side: KotlinExample.Side) = message("Intake $place on $side")
        override fun deposit(side: KotlinExample.Side, ao: Double, lo: Double) =
            message("Deposit $side: offsets in tiles ao=$ao lo=$lo")
        override fun park(side: KotlinExample.Side) = message("Park $side")
        override fun drive(tx: Double, ty: Double, h: Double, axial: String, lateral: String, ao: Double, lo: Double) =
            message("Drive target in tiles x=$tx y=$ty, heading=$h degrees, alignment=$axial/$lateral, offsets=$ao/$lo")
    })
