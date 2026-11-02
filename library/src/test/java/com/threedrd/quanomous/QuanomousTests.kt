package com.threedrd.quanomous

import com.google.gson.JsonArray
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.util.Base64
import java.util.Date
import java.util.zip.GZIPOutputStream
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class QuanomousTests {
    lateinit var directory: java.io.File

    @Before
    fun setUp() {
        directory = Files.createTempDirectory("quanomous").toFile()
        Quanomous.storage = QuanomousStorage(directory)
        Quanomous.now = { Date(0) }
        Quanomous.decoder = { Base64.getDecoder().decode(it) }
        Quanomous.lastHash = null
        Quanomous.lastName = null
    }

    @After
    fun reset() {
        Quanomous.storage = QuanomousStorage()
        Quanomous.now = ::Date
        Quanomous.decoder = { android.util.Base64.decode(it, android.util.Base64.NO_WRAP) }
        Quanomous.lastHash = null
        Quanomous.lastName = null
    }

    @Test
    fun filenameUsesMillisecondsInsteadOfTheNumberOfStoredFiles() {
        Quanomous.now = { Date(42718) }
        val before = Quanomous.filename()
        Quanomous.storage.save(before, JsonArray())
        directory.resolve(before).delete()
        Quanomous.now = { Date(42719) }
        val after = Quanomous.filename()
        org.junit.Assert.assertTrue(before.endsWith("-42718.json"))
        org.junit.Assert.assertTrue(after.endsWith("-42719.json"))
        org.junit.Assert.assertNotEquals(before, after)
    }

    @Test
    fun decodesStoresAndDeduplicatesPrograms() {
        val json = "[{\"cmd\":\"score\"}]"
        val encoded = ByteArrayOutputStream().use { output ->
            GZIPOutputStream(output).use { it.write(json.toByteArray()) }
            Base64.getEncoder().encodeToString(output.toByteArray())
        }

        assertEquals(json, Quanomous.decode(encoded))
        assertEquals(JsonArray::class.java, Quanomous.parse(json).javaClass)
        val expected = java.text.SimpleDateFormat("MM-dd-HHmm-ssSSS'.json'", java.util.Locale.US).format(Date(0))
        assertEquals(expected, Quanomous.filename())

        val first = Quanomous.process(encoded)
        val duplicate = Quanomous.process(encoded)

        assertEquals(expected, first)
        assertEquals(first, duplicate)
        assertEquals(listOf(first), Quanomous.options())
        assertEquals(Quanomous.parse(json), Quanomous.load(first))
        assertEquals(8, Quanomous.hash(Quanomous.parse(json)).length)
    }
}
