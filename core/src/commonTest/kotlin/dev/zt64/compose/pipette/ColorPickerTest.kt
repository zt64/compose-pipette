package dev.zt64.compose.pipette

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import kotlin.math.round
import kotlin.test.Test
import kotlin.test.assertEquals

private const val TEST_TAG = "colorPicker"

@OptIn(ExperimentalTestApi::class)
class ColorPickerTest {
    @Test
    fun testSquarePicker() = runComposeUiTest {
        var color by mutableStateOf(HsvColor(Color.Red))

        setContent {
            SquareColorPicker(
                modifier = Modifier.testTag(TEST_TAG),
                color = color,
                onColorChange = { color = it }
            )
        }

        onNodeWithTag(TEST_TAG).performTouchInput {
            click(Offset(width / 2f, 0f))
        }

        // saturation should be half. multiply by ten to round two decimal places
        assertEquals(5f, round(color.saturation * 10))
    }

    @Test
    fun testCirclePicker() = runComposeUiTest {
        var color by mutableStateOf(HsvColor(Color.Red))

        setContent {
            CircularColorPicker(
                modifier = Modifier.testTag(TEST_TAG),
                color = color,
                onColorChange = { color = it }
            )
        }

        val node = onNodeWithTag(TEST_TAG)

        node.performTouchInput {
            // click at the top of the circle
            click(Offset(width / 2f, 0f))
        }

        assertEquals(270f, round(color.hue))
        assertEquals(1f, color.saturation)
        assertEquals(1f, color.value)

        node.performTouchInput {
            // click at the center of the circle
            click(Offset(width / 2f, height / 2f))
        }

        assertEquals(0f, color.hue)
        assertEquals(0f, color.saturation)
        assertEquals(1f, color.value)
    }

    @Test
    fun testRingPicker() = runComposeUiTest {
        var color by mutableStateOf(HsvColor(Color.Red))

        setContent {
            RingColorPicker(
                modifier = Modifier.testTag(TEST_TAG),
                color = color,
                onColorChange = { color = it }
            )
        }

        onNodeWithTag(TEST_TAG).performTouchInput {
            // click at the top of the ring
            click(Offset(width / 2f, 0f))
        }

        assertEquals(270f, round(color.hue))
        assertEquals(1f, color.saturation)
        assertEquals(1f, color.value)
    }
}