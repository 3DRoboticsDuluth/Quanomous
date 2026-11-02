package org.firstinspires.ftc.teamcode

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime
import com.threedrd.quanomous.Quanomous

@Autonomous(name = "Quanomous BioBuzz Kotlin LinearOpMode")
class QuanomousStateMachineOpMode : LinearOpMode() {
    override fun runOpMode() {
        val routine = readRoutine() ?: return
        waitForStart()
        var index = 0
        var entering = true
        val timer = ElapsedTime()
        while (opModeIsActive() && index < routine.size()) {
            val step = routine[index].asJsonObject
            if (entering) timer.reset()
            val complete = when (step["cmd"].asString) {
                "delay" -> timer.seconds() >= step["seconds"].asDouble
                "drive" -> drive(step, entering)
                "intake" -> intake(step, entering)
                "deposit" -> deposit(step, entering)
                "chase" -> chase(step, entering)
                "park" -> park(step, entering)
                else -> throw IllegalArgumentException("Unknown instruction: $step")
            }
            if (complete) index++
            entering = complete
            idle()
        }
    }

    // Team step methods: do one nonblocking update and return true when complete.
    // entering is true only on the first loop of this instruction. Read parameters from step.
    private fun drive(step: JsonObject, entering: Boolean): Boolean = TODO("Supply your team's drive behavior")
    private fun intake(step: JsonObject, entering: Boolean): Boolean = TODO("Supply your team's intake behavior")
    private fun deposit(step: JsonObject, entering: Boolean): Boolean = TODO("Supply your team's deposit behavior")
    private fun chase(step: JsonObject, entering: Boolean): Boolean = TODO("Supply your team's chase behavior")
    private fun park(step: JsonObject, entering: Boolean): Boolean = TODO("Supply your team's park behavior")

    private fun readRoutine(): JsonArray? = try {
        hardwareMap.appContext.assets.open("biobuzz-route.json")
            .bufferedReader(Charsets.UTF_8).use { Quanomous.parse(it.readText()) }
    } catch (failure: java.io.IOException) {
        null // Add your team's load-error handling here.
    } catch (failure: RuntimeException) {
        null
    }
}
