package com.threedrd.examples

import com.threedrd.quanomous.Quanomous
import com.threedrd.quanomous.QuanomousCompiler

/** Replace () -> Unit with your command or state-machine action type. */
fun compileRoutine(json: String, events: MutableList<String>): List<() -> Unit> {
    val compiler = QuanomousCompiler<() -> Unit>(mapOf(
        "signal" to { step ->
            val label = step["label"].asString
            val action: () -> Unit = { events.add(label) }
            action
        }
    ))
    return compiler.compile(Quanomous.parse(json))
}
