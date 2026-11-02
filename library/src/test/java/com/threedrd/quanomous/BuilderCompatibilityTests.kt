package com.threedrd.quanomous

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.util.Base64
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

class BuilderCompatibilityTests {
    @After fun resetDecoder() {
        Quanomous.decoder = { android.util.Base64.decode(it, android.util.Base64.NO_WRAP) }
    }

    @Test fun decodesAndDispatchesTheActualBuilderFixtureWithoutOwningItsSchema() {
        val fixture = javaClass.getResourceAsStream("/biobuzz.json")!!.bufferedReader().use {
            JsonParser.parseString(it.readText()).asJsonObject
        }
        Quanomous.decoder = { Base64.getDecoder().decode(it) }
        val steps = Quanomous.parse(Quanomous.decode(fixture["encoded"].asString))
        assertEquals(fixture["steps"], steps)
        val handlers = steps.map { it.asJsonObject["cmd"].asString }.associateWith {
            { step: JsonObject -> step }
        }
        assertEquals(steps.toList(), QuanomousCompiler(handlers).compile(steps))
        assertEquals(0.4, steps[0].asJsonObject["seconds"].asDouble, 0.0)
        assertEquals(-2.0, steps.last().asJsonObject["ty"].asDouble, 0.0)
    }
}
