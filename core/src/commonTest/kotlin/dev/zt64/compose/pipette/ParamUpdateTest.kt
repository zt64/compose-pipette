package dev.zt64.compose.pipette

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import kotlin.test.Test
import kotlin.test.assertEquals

private const val TAG_COLOR_PICKER = "colorPicker"

@OptIn(ExperimentalTestApi::class)
class ParamUpdateTest {
    @Test
    fun testUpdatedParams() = runComposeUiTest {
        var hsvColor by mutableStateOf(HsvColor(120f, 1f, 1f))

        setContent {
            ColorPickerWrapper(
                hsvColor = hsvColor,
                onColorChange = { hsvColor = it }
            )
        }

        // First click to trigger the color picker
        onNodeWithTag(TAG_COLOR_PICKER).performTouchInput {
            click(Offset(width * 0.5f, height * 0.5f))
        }
        waitForIdle()

        // Manually update the value
        hsvColor = hsvColor.copy(value = 0.5f)
        waitForIdle()

        // Second click - without rememberUpdatedState, this will use the old value (1f)
        onNodeWithTag(TAG_COLOR_PICKER).performTouchInput {
            click(Offset(width * 0.7f, height * 0.3f))
        }
        waitForIdle()

        // Value should be 0.5f, not 1f
        assertEquals(0.5f, hsvColor.value, 0.01f, "Value should remain 0.5f after second click")
    }

    @Composable
    private fun ColorPickerWrapper(hsvColor: HsvColor, onColorChange: (HsvColor) -> Unit) {
        CircularColorPicker(
            modifier = Modifier.testTag(TAG_COLOR_PICKER),
            hue = { hsvColor.hue },
            saturation = { hsvColor.saturation },
            value = { hsvColor.value },
            onColorChange = { hue, saturation ->
                onColorChange(hsvColor.copy(hue, saturation))
            }
        )
    }
}