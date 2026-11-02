package org.firstinspires.ftc.teamcode

import com.google.gson.JsonArray
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.threedrd.quanomous.Quanomous
import com.threedrd.quanomous.QuanomousCompiler
// Use your preferred command library (NextFTC, SolversLib, WPILib, etc.).
// This compiling example uses NextFTC 1.1.0's Command, group and OpMode types.
import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.delays.Delay
import dev.nextftc.core.commands.groups.SequentialGroup
import dev.nextftc.ftc.NextFTCOpMode

@Autonomous(name = "Quanomous BioBuzz Kotlin commands")
class QuanomousCommandOpMode : NextFTCOpMode() {
    private var routine: Command? = null

    override fun onInit() {
        val steps = readRoutine() ?: return
        routine = SequentialGroup(*QuanomousCompiler<Command>(mapOf(
            "delay" to { step -> delay(step["seconds"].asDouble) },
            "drive" to { step -> drive(step["tx"].asDouble, step["ty"].asDouble, step["h"].asDouble, step["axial"].asString, step["lateral"].asString, step["ao"].asDouble, step["lo"].asDouble) },
            "intake" to { step -> intake(step["place"].asString, step["side"].asString) },
            "deposit" to { step -> deposit(step["side"].asString, step["ao"].asDouble, step["lo"].asDouble) },
            "chase" to { step -> chase(step["cycles"].asInt) },
            "park" to { step -> park(step["side"].asString) }
        )).compile(steps).toTypedArray())
    }

    override fun onStartButtonPressed() { routine?.schedule() }

    // Replace these factory bodies with your team's commands; construct here, schedule above.
    private fun delay(seconds: Double): Command = Delay(seconds)
    private fun drive(tx: Double, ty: Double, h: Double, axial: String, lateral: String, ao: Double, lo: Double): Command = TODO("Supply your team's drive command")
    private fun intake(place: String, side: String): Command = TODO("Supply your team's intake command")
    private fun deposit(side: String, ao: Double, lo: Double): Command = TODO("Supply your team's deposit command")
    private fun chase(cycles: Int): Command = TODO("Supply your team's chase command")
    private fun park(side: String): Command = TODO("Supply your team's park command")

    private fun readRoutine(): JsonArray? = try {
        hardwareMap.appContext.assets.open("biobuzz-route.json")
            .bufferedReader(Charsets.UTF_8).use { Quanomous.parse(it.readText()) }
    } catch (failure: java.io.IOException) {
        null // Add your team's load-error handling here.
    } catch (failure: RuntimeException) {
        null
    }
}
