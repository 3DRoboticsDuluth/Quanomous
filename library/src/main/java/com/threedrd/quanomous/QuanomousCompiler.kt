package com.threedrd.quanomous

import com.google.gson.JsonObject

class QuanomousCompiler<T>(
    val commands: Map<String, (JsonObject) -> T>
) {
    companion object {
        /** Java-friendly registration without exposing Kotlin function types. */
        @JvmStatic
        fun <T> fromHandlers(commands: Map<String, CommandFactory<T>>): QuanomousCompiler<T> =
            QuanomousCompiler(commands.mapValues { (_, factory) -> { step -> factory.create(step) } })
    }

    fun load(name: String) = compile(Quanomous.load(name))

    fun compile(steps: Iterable<com.google.gson.JsonElement>) =
        steps.map { element ->
            val step = element.asJsonObject
            val name = step["cmd"].asString
            require(commands.containsKey(name)) { "Unknown Quanomous command: $name" }
            commands.getValue(name)(step)
        }
}
