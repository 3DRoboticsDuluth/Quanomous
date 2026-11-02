package com.threedrd.quanomous

import com.google.gson.JsonObject

/** A team-owned instruction handler. Construct an action here; execute it later. */
fun interface CommandFactory<T> {
    fun create(step: JsonObject): T
}
