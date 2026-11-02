package org.firstinspires.ftc.teamcode

import com.google.gson.JsonParser
import com.threedrd.quanomous.Quanomous
import java.util.Base64
import org.junit.After
import org.junit.Assert.*
import org.junit.Test

class KotlinExampleTest {
    private val events = mutableListOf<String>()
    private val adapter = KotlinExample(object : KotlinExample.Auto<() -> Unit> {
        private fun record(value: String): () -> Unit = { events.add(value) }
        override fun delay(seconds: Double) = record("delay:$seconds")
        override fun chase(cycles: Int) = record("chase:$cycles")
        override fun intake(place: KotlinExample.Place, side: KotlinExample.Side) = record("intake:$place:$side")
        override fun deposit(side: KotlinExample.Side, ao: Double, lo: Double) = record("deposit:$side:$ao:$lo")
        override fun park(side: KotlinExample.Side) = record("park:$side")
        override fun drive(tx: Double, ty: Double, h: Double, axial: String, lateral: String, ao: Double, lo: Double) =
            record("drive:$tx:$ty:$h:$axial:$lateral:$ao:$lo")
    })

    @After fun resetDecoder() {
        Quanomous.decoder = { android.util.Base64.decode(it, android.util.Base64.NO_WRAP) }
    }

    @Test fun builderQrMapsEveryBioBuzzFieldWithoutExecutingCommands() {
        val fixture = JsonParser.parseString(resource("biobuzz.json")).asJsonObject
        Quanomous.decoder = { Base64.getDecoder().decode(it) }
        val commands = adapter.decode(fixture["encoded"].asString)
        assertTrue(events.isEmpty())
        commands.forEach { it() }
        assertEquals(listOf(
            "delay:0.4", "chase:2", "intake:LOADING_ZONE:RIGHT",
            "deposit:LEFT:0.5:-0.25", "park:RIGHT",
            "drive:1.0:-2.0:90.0:front:left:0.25:-0.5"), events)
    }

    @Test fun compilesTheIllustrativeBioBuzzRoute() {
        val commands = adapter.compile(resource("biobuzz-route.json"))
        assertTrue(events.isEmpty())
        commands.forEach { it() }
        assertEquals(listOf("delay", "drive", "deposit", "intake", "deposit", "intake", "deposit", "chase", "park"),
            events.map { it.substringBefore(':') })
    }

    @Test fun preservesDepositAliasesZeroPrecedenceAndSignedDriveDefaults() {
        val commands = adapter.compile("""[{"cmd":"deposit","side":"LEFT","ao":0,"txo":9,"tyo":2},
            {"cmd":"drive","tx":-1,"ty":2,"h":-90}]""")
        commands.forEach { it() }
        assertEquals(listOf("deposit:LEFT:0.0:2.0", "drive:-1.0:2.0:-90.0:center:center:0.0:0.0"), events)
        assertThrows(IllegalArgumentException::class.java) { adapter.compile("""[{"cmd":"release"}]""") }
    }

    private fun resource(name: String) = javaClass.getResourceAsStream("/$name")!!.bufferedReader().use { it.readText() }
}
