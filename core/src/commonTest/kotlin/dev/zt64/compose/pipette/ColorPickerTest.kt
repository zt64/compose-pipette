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

private const val TEST_TAG = "squareColorPicker"

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
}