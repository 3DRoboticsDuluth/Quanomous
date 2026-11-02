package org.firstinspires.ftc.teamcode

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.threedrd.quanomous.Quanomous

@Autonomous(name = "Quanomous BioBuzz Kotlin LinearOpMode")
class QuanomousStateMachineOpMode : LinearOpMode() {
    override fun runOpMode() {
        val steps = readRoutine().iterator()
        waitForStart()
        var step = if (steps.hasNext()) steps.next().asJsonObject else null
        while (opModeIsActive() && step != null) {
            val complete = when (step["cmd"].asString) {
                "delay" -> delay(step)
                "drive" -> drive(step)
                "intake" -> intake(step)
                "deposit" -> deposit(step)
                "chase" -> chase(step)
                "park" -> park(step)
                else -> throw IllegalArgumentException("Unknown instruction: $step")
            }
            if (complete) step = if (steps.hasNext()) steps.next().asJsonObject else null
            idle()
        }
    }

    // Team step methods: do one nonblocking update and return true when complete.

    private fun delay(step: JsonObject): Boolean = TODO("Supply your team's delay behavior")


    private fun drive(step: JsonObject): Boolean = TODO("Supply your team's drive behavior")

    private fun intake(step: JsonObject): Boolean = TODO("Supply your team's intake behavior")

    private fun deposit(step: JsonObject): Boolean = TODO("Supply your team's deposit behavior")

    private fun chase(step: JsonObject): Boolean = TODO("Supply your team's chase behavior")

    private fun park(step: JsonObject): Boolean = TODO("Supply your team's park behavior")


    private fun readRoutine(): JsonArray = try {
        hardwareMap.appContext.assets.open("biobuzz-route.json")
            .bufferedReader(Charsets.UTF_8).use { Quanomous.parse(it.readText()) }
    } catch (failure: java.io.IOException) {
        JsonArray() // Add your team's load-error handling here.
    } catch (failure: RuntimeException) {
        JsonArray()
    }
}
