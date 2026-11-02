package org.firstinspires.ftc.threedrd.quanomous

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import java.io.File

open class QuanomousStorage(
    val directory: File = File("/sdcard/FIRST/quanomous")
) {
    open fun names() = directory
        .listFiles { file -> file.extension.equals("json", true) }
        .orEmpty()
        .sortedBy { it.name }
        .map { it.name }

    open fun load(name: String) =
        JsonParser().parse(File(directory, name).readText()).asJsonArray

    open fun save(name: String, program: JsonArray) {
        directory.mkdirs()
        File(directory, name).writeText(program.toString())
    }
}
