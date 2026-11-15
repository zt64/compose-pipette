package dev.zt64.compose.pipette.sample

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import dev.zt64.compose.pipette.ColorPickerState
import dev.zt64.compose.pipette.HsvColor
import kotlin.math.roundToInt

@Composable
internal fun HexField(colorPickerState: ColorPickerState) {
    FormatField(
        state = colorPickerState,
        label = "Hex",
        value = { c ->
            "#" + listOf(c.red, c.green, c.blue).joinToString("") {
                (it * 255).roundToInt().toString(16).padStart(2, '0')
            }.uppercase()
        }
    )
}

@Composable
fun RgbField(colorPickerState: ColorPickerState) {
    FormatField(
        state = colorPickerState,
        label = "RGB",
        value = { c ->
            listOf(c.red, c.green, c.blue).joinToString(", ") {
                (it * 255).roundToInt().toString()
            }
        }
    )
}

@Composable
fun HsvField(colorPickerState: ColorPickerState) {
    FormatField(
        state = colorPickerState,
        label = "HSV",
        value = { c ->
            "${c.hue.roundToInt()}°, ${(c.saturation * 100).roundToInt()}%, ${(c.value * 100).roundToInt()}%"
        }
    )
}

@Composable
private fun FormatField(
    state: ColorPickerState,
    label: String,
    value: (color: HsvColor) -> String,
    modifier: Modifier = Modifier
) {
    var isError by rememberSaveable { mutableStateOf(false) }

    val textFieldState = rememberTextFieldState(initialText = "")
    val currentValue by rememberUpdatedState(value)

    LaunchedEffect(state, textFieldState) {
        snapshotFlow { currentValue(state.color) }.collect { formatted ->
            if (textFieldState.text.toString() != formatted) {
                textFieldState.setTextAndPlaceCursorAtEnd(formatted)
            }
        }
    }

    TextField(
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged {
                if (!it.isFocused) isError = false
            },
        state = textFieldState,
        label = { Text(label) },
        supportingText = if (isError) {
            { Text("Invalid $label") }
        } else {
            null
        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        isError = isError,
        lineLimits = TextFieldLineLimits.SingleLine
    )
}