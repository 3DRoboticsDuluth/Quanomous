package org.firstinspires.ftc.teamcode

import com.google.gson.JsonObject
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.threedrd.quanomous.Quanomous
import com.threedrd.quanomous.QuanomousCompiler
import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.delays.Delay
import dev.nextftc.core.commands.groups.SequentialGroup
import dev.nextftc.ftc.NextFTCOpMode
import java.io.IOException

/** NextFTC 1.1.0 example. Replace simulated commands with your team's hardware commands. */
@Autonomous(name = "Quanomous BioBuzz Kotlin commands")
class QuanomousCommandOpMode : NextFTCOpMode() {
    private var routine: Command? = null
    private var current = "Ready"

    override fun onInit() {
        val compiler = QuanomousCompiler<Command>(mapOf(
            "delay" to { step -> Delay(step["seconds"].asDouble) },
            "drive" to { step -> simulated(step) }, // tx, ty, h, axial, lateral, ao, lo
            "intake" to { step -> simulated(step) }, // place, side
            "deposit" to { step -> simulated(step) }, // side, ao, lo
            "chase" to { step -> simulated(step) }, // cycles
            "park" to { step -> simulated(step) } // side
        ))
        try {
            val json = hardwareMap.appContext.assets.open("biobuzz-route.json")
                .bufferedReader(Charsets.UTF_8).use { it.readText() }
            val commands = compiler.compile(Quanomous.parse(json))
            routine = SequentialGroup(*commands.toTypedArray())
            telemetry.addLine("Ready: Start, then press A for each simulated hardware command")
        } catch (failure: IOException) {
            telemetry.addData("Cannot load routine", failure.message)
        } catch (failure: RuntimeException) {
            telemetry.addData("Cannot load routine", failure.message)
        }
        telemetry.update()
    }

    override fun onStartButtonPressed() {
        routine?.schedule()
    }

    override fun onUpdate() {
        telemetry.addData("Command", current)
        telemetry.addLine("Hardware commands are simulated: release then press A to finish")
        telemetry.update()
    }

    override fun onStop() {
        routine?.cancel()
        // Real robot: also stop ALL drivetrain/mechanism outputs here.
    }

    private fun simulated(step: JsonObject): Command = object : Command() {
        private var previousA = false
        private var finished = false
        override fun start() {
            previousA = gamepad1.a
            finished = false
            current = step.toString()
            // Real command: initialize targets/outputs using the instruction fields.
        }
        override fun update() {
            val pressed = gamepad1.a
            finished = pressed && !previousA
            previousA = pressed
            // Real command: update control logic; set finished from sensors/controller state.
        }
        override val isDone: Boolean get() = finished
        override fun stop(interrupted: Boolean) {
            // Real command: release its outputs on completion or cancellation.
        }
    }
    // Hardware commands must declare their subsystem requirements with requires(...).
}
