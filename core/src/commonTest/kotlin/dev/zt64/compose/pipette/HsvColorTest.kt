package dev.zt64.compose.pipette

import androidx.compose.ui.graphics.Color
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

    @Test
    fun testConstructors() {
        val color1 = HsvColor(0xFF0000)
        val color2 = HsvColor(0xFF0000L)
        val color3 = HsvColor(0f, 1f, 1f)
        val color4 = HsvColor(Color.Red)

        val colors = listOf(color1, color2, color3, color4)

        colors.forEach { a ->
            colors.forEach { b ->
                assertEquals(a, b)
            }
        }
    }
}