package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime
import com.threedrd.quanomous.Quanomous
import java.io.IOException

@Autonomous(name = "Quanomous BioBuzz Kotlin switch demo")
class QuanomousStateMachineOpMode : LinearOpMode() {
    override fun runOpMode() {
        val routine = try {
            val json = hardwareMap.appContext.assets.open("biobuzz-route.json")
                .bufferedReader(Charsets.UTF_8).use { it.readText() }
            Quanomous.parse(json)
        } catch (failure: IOException) {
            telemetry.addData("Cannot load routine", failure.message)
            telemetry.update()
            return
        } catch (failure: RuntimeException) {
            telemetry.addData("Cannot load routine", failure.message)
            telemetry.update()
            return
        }

        telemetry.addLine("Ready: Start, then press A for each simulated hardware step")
        telemetry.update()
        waitForStart()
        if (isStopRequested) return

        var stepIndex = 0
        var entering = true
        var previousA = gamepad1.a
        val stepTime = ElapsedTime()
        try {
            while (opModeIsActive() && stepIndex < routine.size()) {
                val step = routine[stepIndex].asJsonObject
                val command = step["cmd"].asString
                val pressedA = gamepad1.a
                var freshA = pressedA && !previousA
                previousA = pressedA
                if (entering) {
                    stepTime.reset()
                    // A held A button must not immediately complete a new step.
                    freshA = false
                }

                val complete = when (command) {
                    "delay" -> stepTime.seconds() >= step["seconds"].asDouble
                    "drive" -> {
                        if (entering) {
                            // Set target once using tx, ty, h, axial, lateral, ao and lo.
                        }
                        // Update your drive controller each loop.
                        val finished = freshA // DEMO: replace with drive-at-target condition.
                        if (finished) {
                            // Stop drivetrain outputs before advancing.
                        }
                        finished
                    }
                    "intake" -> {
                        if (entering) {
                            // Begin intake using place and side.
                        }
                        // Update intake/navigation state each loop.
                        val finished = freshA // DEMO: replace with piece-acquired condition.
                        if (finished) {
                            // Finish intake and release this step's outputs.
                        }
                        finished
                    }
                    "deposit" -> {
                        if (entering) {
                            // Begin deposit using side, ao and lo.
                        }
                        // Update scoring mechanism state each loop.
                        val finished = freshA // DEMO: replace with deposit-finished condition.
                        if (finished) {
                            // Finish scoring and release this step's outputs.
                        }
                        finished
                    }
                    "chase" -> {
                        if (entering) {
                            // Reset completed-cycle count; target is step["cycles"].asInt.
                        }
                        // Update your collection/scoring cycle state each loop.
                        val finished = freshA // DEMO: replace with completedCycles >= targetCycles.
                        if (finished) {
                            // Stop cycle outputs.
                        }
                        finished
                    }
                    "park" -> {
                        if (entering) {
                            // Set your park target using side.
                        }
                        // Update navigation each loop.
                        val finished = freshA // DEMO: replace with parked condition.
                        if (finished) {
                            // Stop drivetrain outputs.
                        }
                        finished
                    }
                    else -> throw IllegalArgumentException("Unknown BioBuzz instruction: $command")
                }

                telemetry.addData("Step", "%d / %d", stepIndex + 1, routine.size())
                telemetry.addData("Instruction and parameters", step.toString())
                telemetry.addData("Step seconds", stepTime.seconds())
                telemetry.addLine("Hardware steps are simulated: release then press A to finish")
                telemetry.update()
                if (complete) {
                    stepIndex++
                    entering = true
                } else {
                    entering = false
                }
                idle()
            }
        } finally {
            // Real robot: stop ALL drivetrain/mechanism outputs here, including on Stop/error.
        }
    }
}
