package com.threedrd.examples

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class KotlinExampleTest {
    @Test fun constructsTeamDefinedActionsBeforeExecutingThem() {
        val events = mutableListOf<String>()
        val actions = compileRoutine("""[{"cmd":"signal","label":"first"},{"cmd":"signal","label":"second"}]""", events)
        assertTrue(events.isEmpty())
        actions.forEach { it() }
        assertEquals(listOf("first", "second"), events)
    }
}
