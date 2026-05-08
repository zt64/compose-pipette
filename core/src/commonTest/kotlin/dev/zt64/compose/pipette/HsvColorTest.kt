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

    @Test
    fun testCopy() {
        val original = HsvColor(180f, 0.5f, 0.75f)

        val changedHue = original.copy(hue = 90f)
        assertEquals(90f, changedHue.hue)
        assertEquals(original.saturation, changedHue.saturation)
        assertEquals(original.value, changedHue.value)

        val changedSat = original.copy(saturation = 0f)
        assertEquals(original.hue, changedSat.hue)
        assertEquals(0f, changedSat.saturation)
        assertEquals(original.value, changedSat.value)

        val changedVal = original.copy(value = 1f)
        assertEquals(original.hue, changedVal.hue)
        assertEquals(original.saturation, changedVal.saturation)
        assertEquals(1f, changedVal.value)
    }

    @Test
    fun testDestructuring() {
        val color = HsvColor(120f, 1f, 0.5f)
        val (h, s, v) = color

        assertEquals(120f, h)
        assertEquals(1f, s)
        assertEquals(0.5f, v)
    }

    @Test
    fun testRgbComponents() {
        val red = HsvColor(0f, 1f, 1f)
        assertEquals(1f, red.red, 0.001f)
        assertEquals(0f, red.green, 0.001f)
        assertEquals(0f, red.blue, 0.001f)

        val white = HsvColor(0f, 0f, 1f)
        assertEquals(1f, white.red, 0.001f)
        assertEquals(1f, white.green, 0.001f)
        assertEquals(1f, white.blue, 0.001f)

        val black = HsvColor(0f, 0f, 0f)
        assertEquals(0f, black.red, 0.001f)
        assertEquals(0f, black.green, 0.001f)
        assertEquals(0f, black.blue, 0.001f)
    }
}