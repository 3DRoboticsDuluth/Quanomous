package org.firstinspires.ftc.teamcode

import com.google.gson.JsonArray
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.threedrd.quanomous.Quanomous
import com.threedrd.quanomous.QuanomousCompiler

@Autonomous(name = "Quanomous BioBuzz Kotlin commands")
class QuanomousCommandOpMode : LinearOpMode() {
    override fun runOpMode() {
        val steps = readRoutine()
        val routine = Command.sequence(QuanomousCompiler<Command>(mapOf(
            "delay" to { step -> delay(step["seconds"].asDouble) },
            "drive" to { step -> drive(step["tx"].asDouble, step["ty"].asDouble, step["h"].asDouble) },
            "intake" to { step -> intake(step["place"].asString, step["side"].asString) },
            "deposit" to { step -> deposit(step["side"].asString, step["ao"].asDouble, step["lo"].asDouble) },
            "chase" to { step -> chase(step["cycles"].asInt) },
            "park" to { step -> park(step["side"].asString) }
        )).compile(steps))
        waitForStart()
        routine.schedule()
    }

    /** Placeholder for your preferred command library: NextFTC, SolversLib, WPILib, etc.
     * Replace this type and its sequence/schedule calls with your library's equivalents. */
    private class Command {
        companion object {
            fun sequence(commands: List<Command>): Command = TODO("Use your library's sequential command group")
        }

        fun schedule(): Unit = TODO("Use your library's scheduler")
    }

    // Replace these factory bodies with your team's commands; construct here, schedule above.
    private fun delay(seconds: Double): Command = TODO("Supply your team's delay command")

    private fun drive(tx: Double, ty: Double, h: Double): Command = TODO("Supply your team's drive command")

    private fun intake(place: String, side: String): Command = TODO("Supply your team's intake command")

    private fun deposit(side: String, ao: Double, lo: Double): Command = TODO("Supply your team's deposit command")

    private fun chase(cycles: Int): Command = TODO("Supply your team's chase command")

    private fun park(side: String): Command = TODO("Supply your team's park command")

    private fun readRoutine(): JsonArray = try {
        hardwareMap.appContext.assets.open("biobuzz-route.json")
            .bufferedReader(Charsets.UTF_8).use { Quanomous.parse(it.readText()) }
    } catch (failure: java.io.IOException) {
        JsonArray() // Add your team's load-error handling here.
    } catch (failure: RuntimeException) {
        JsonArray()
    }
}
