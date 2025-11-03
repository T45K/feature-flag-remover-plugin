package io.github.t45k.feature_flag_remover.core

import kotlin.test.Test
import kotlin.test.assertEquals

class IntegrationTest {

    @Test
    fun test() = removeFeatureFlagContext {
        val classLoader = this::class.java.classLoader
        val before = classLoader.getResource("Before.kt")!!.readText()
        val after = classLoader.getResource("After.kt")!!.readText().removePrefix("// @formatter:off\n")

        // expect
        assertEquals(
            after,
            removeFeatureFlag(before, "sample").formatted(),
        )
    }

    private fun String.formatted(): String = lines().joinToString("\n") { it.trimEnd() }
}
