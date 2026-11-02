package org.firstinspires.ftc.teamcode

import com.google.gson.JsonObject
import com.threedrd.quanomous.Quanomous
import com.threedrd.quanomous.QuanomousCompiler
import java.util.Locale

/** Devil Dogs' BioBuzz JSON contract. T is your framework's command/action type. */
class KotlinExample<T>(auto: Auto<T>) {
    enum class Side { LEFT, RIGHT }
    enum class Place { GARDEN, FLOWER, LOADING_ZONE }

    /** Factories bind to team Auto/Nav methods. Construct commands here; schedule later. */
    interface Auto<T> {
        fun delay(seconds: Double): T
        fun chase(cycles: Int): T
        fun intake(place: Place, side: Side): T
        fun deposit(side: Side, ao: Double, lo: Double): T
        fun park(side: Side): T
        fun drive(tx: Double, ty: Double, h: Double, axial: String, lateral: String, ao: Double, lo: Double): T
    }

    private val compiler = QuanomousCompiler<T>(mapOf(
        "delay" to { step -> auto.delay(step["seconds"].asDouble) },
        "chase" to { step -> auto.chase(step["cycles"].asInt) },
        "intake" to { step -> auto.intake(Place.valueOf(step["place"].asString.uppercase(Locale.ROOT)), step.side()) },
        "deposit" to { step -> auto.deposit(step.side(), step.offset("ao", "txo"), step.offset("lo", "tyo")) },
        "park" to { step -> auto.park(step.side()) },
        "drive" to { step -> auto.drive(
            step["tx"].asDouble, step["ty"].asDouble, step["h"].asDouble,
            step.reference("axial", "front", "back"), step.reference("lateral", "left", "right"),
            step.number("ao"), step.number("lo"))
        }
    ))

    fun compile(json: String): List<T> = compiler.compile(Quanomous.parse(json))
    fun decode(barcodeData: String): List<T> = compile(Quanomous.decode(barcodeData))
    fun load(filename: String): List<T> = compiler.load(filename)

    private fun JsonObject.side() = Side.valueOf(get("side").asString.uppercase(Locale.ROOT))
    private fun JsonObject.number(name: String) = get(name)?.asDouble ?: 0.0
    private fun JsonObject.offset(name: String, legacy: String) = get(name)?.asDouble ?: number(legacy)
    private fun JsonObject.reference(name: String, first: String, second: String): String {
        val value = get(name)?.asString?.lowercase(Locale.ROOT) ?: "center"
        return if (value == first || value == second) value else "center"
    }
}
