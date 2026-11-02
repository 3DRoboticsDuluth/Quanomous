package org.firstinspires.ftc.threedrd.quanomous

import com.google.gson.JsonParser
import java.nio.file.Files
import org.junit.Assert.assertEquals
import org.junit.Test

class QuanomousStorageTests {
    @Test
    fun savesLoadsAndSortsJsonPrograms() {
        val directory = Files.createTempDirectory("quanomous-storage").toFile()
        val storage = QuanomousStorage(directory)
        val program = JsonParser().parse("[{\"cmd\":\"score\"}]").asJsonArray

        assertEquals(emptyList<String>(), storage.names())
        storage.save("b.JSON", program)
        storage.save("a.json", program)
        directory.resolve("ignored.txt").writeText("ignored")

        assertEquals(listOf("a.json", "b.JSON"), storage.names())
        assertEquals(program, storage.load("a.json"))
        assertEquals(directory, storage.directory)
    }
}
