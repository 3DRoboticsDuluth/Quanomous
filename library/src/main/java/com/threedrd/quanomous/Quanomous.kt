package com.threedrd.quanomous

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat
import android.util.Base64
import java.util.Date
import java.util.Locale
import java.util.zip.GZIPInputStream

object Quanomous {
    var storage = QuanomousStorage()
    var now: () -> Date = ::Date
    var decoder: (String) -> ByteArray = { Base64.decode(it, Base64.NO_WRAP) }
    var lastHash: String? = null
    var lastName: String? = null

    fun options() = storage.names()

    fun process(data: String): String {
        val program = parse(decode(data))
        val hash = hash(program)
        if (hash == lastHash) return lastName!!

        val name = filename()
        storage.save(name, program)
        lastHash = hash
        lastName = name
        return name
    }

    fun decode(data: String): String {
        val input = ByteArrayInputStream(decoder(data))
        return GZIPInputStream(input).use { gzip ->
            ByteArrayOutputStream().use { output ->
                gzip.copyTo(output)
                output.toString(Charsets.UTF_8.name())
            }
        }
    }

    fun parse(json: String) = JsonParser().parse(json).asJsonArray

    fun load(name: String) = storage.load(name)

    fun filename(): String {
        return SimpleDateFormat("MM-dd-HHmm-ssSSS'.json'", Locale.US).format(now())
    }

    fun hash(program: JsonArray): String {
        val canonical = GsonBuilder().create().toJson(program)
        return MessageDigest.getInstance("SHA-256")
            .digest(canonical.toByteArray())
            .joinToString("") { "%02x".format(it) }
            .take(8)
    }
}
