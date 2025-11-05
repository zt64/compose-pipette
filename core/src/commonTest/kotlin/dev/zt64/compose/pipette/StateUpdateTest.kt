package dev.zt64.compose.pipette

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import kotlin.test.Test
import kotlin.test.assertEquals

private const val TAG_COLOR_PICKER = "colorPicker"

/**
 * Tests that verify color picker state updates are properly captured in long-lived lambda callbacks.
 * These tests ensure that `rememberUpdatedState` is used correctly to capture the latest state
 * in gesture handlers that may be called after state changes.
 */
@OptIn(ExperimentalTestApi::class)
class StateUpdateTest {
    @Test
    fun circularColorPickerStateUpdate() = runComposeUiTest {
        var hsvColor by mutableStateOf(HsvColor(120f, 1f, 1f))

        setContent {
            CircularColorPickerWrapper(
                hsvColor = hsvColor,
                onColorChange = { hsvColor = it }
            )
        }

        onNodeWithTag(TAG_COLOR_PICKER).performTouchInput {
            click(Offset(width * 0.5f, height * 0.5f))
        }

        // Update value externally
        hsvColor = hsvColor.copy(value = 0.5f)

        // Perform another interaction
        onNodeWithTag(TAG_COLOR_PICKER).performTouchInput {
            click(Offset(width * 0.7f, height * 0.3f))
        }

        // Verify the updated value is preserved (not overwritten with stale value)
        assertEquals(0.5f, hsvColor.value, 0.01f, "Value should remain 0.5f after second click")
    }

    @Test
    fun squareColorPickerStateUpdate() = runComposeUiTest {
        var hsvColor by mutableStateOf(HsvColor(120f, 1f, 1f))

        setContent {
            SquareColorPickerWrapper(
                hsvColor = hsvColor,
                onColorChange = { hsvColor = it }
            )
        }

        onNodeWithTag(TAG_COLOR_PICKER).performTouchInput {
            click(Offset(width * 0.5f, height * 0.5f))
        }

        // Update hue externally
        hsvColor = hsvColor.copy(hue = 60f)

        // Perform another interaction
        onNodeWithTag(TAG_COLOR_PICKER).performTouchInput {
            click(Offset(width * 0.7f, height * 0.3f))
        }

        // Verify the updated hue is preserved (not overwritten with stale value)
        assertEquals(60f, hsvColor.hue, 0.01f, "Hue should remain 60f after second click")
    }

    @Test
    fun ringColorPickerStateUpdate() = runComposeUiTest {
        var hsvColor by mutableStateOf(HsvColor(120f, 1f, 1f))

        setContent {
            RingColorPickerWrapper(
                hsvColor = hsvColor,
                onColorChange = { hsvColor = it }
            )
        }

        onNodeWithTag(TAG_COLOR_PICKER).performTouchInput {
            click(Offset(4f, height * 0.5f))
        }

        // Update saturation externally
        hsvColor = hsvColor.copy(saturation = 0.5f)

        // Perform another interaction
        onNodeWithTag(TAG_COLOR_PICKER).performTouchInput {
            click(Offset(width.toFloat() - 4f, height * 0.5f))
        }

        // Verify the updated saturation is preserved (not overwritten with stale value)
        assertEquals(0.5f, hsvColor.saturation, 0.01f, "Saturation should remain 0.5f after second click")
    }

    @Composable
    private fun CircularColorPickerWrapper(hsvColor: HsvColor, onColorChange: (HsvColor) -> Unit) {
        CircularColorPicker(
            modifier = Modifier.testTag(TAG_COLOR_PICKER),
            color = { hsvColor },
            onColorChange = onColorChange
        )
    }

    @Composable
    private fun SquareColorPickerWrapper(hsvColor: HsvColor, onColorChange: (HsvColor) -> Unit) {
        SquareColorPicker(
            modifier = Modifier.testTag(TAG_COLOR_PICKER),
            color = { hsvColor },
            onColorChange = onColorChange
        )
    }

    @Composable
    private fun RingColorPickerWrapper(hsvColor: HsvColor, onColorChange: (HsvColor) -> Unit) {
        RingColorPicker(
            modifier = Modifier.testTag(TAG_COLOR_PICKER),
            color = { hsvColor },
            onColorChange = onColorChange
        )
    }
}