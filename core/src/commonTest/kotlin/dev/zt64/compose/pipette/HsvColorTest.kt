package dev.zt64.compose.pipette

import kotlin.test.Test
import kotlin.test.assertEquals

class HsvColorTest {
    @Test
    fun testHsvPacking() {
        val h = 352f
        val s = 0.5f
        val v = 0.5f

        val color = HsvColor(h, s, v)

        assertEquals(h, color.hue)
        assertEquals(s, color.saturation)
        assertEquals(v, color.value)
    }
}