package dev.zt64.compose.pipette.sample

import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import dev.zt64.compose.pipette.HsvColor
import kotlin.math.roundToInt

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun HexField(color: HsvColor, onColorChange: (HsvColor) -> Unit) {
    val hex = remember(color) {
        "#" + listOf(color.red, color.green, color.blue).joinToString("") {
            (it * 255).roundToInt().toString(16).padStart(2, '0')
        }.uppercase()
    }

    FormatField(
        label = "Hex",
        value = hex,
        onValueChange = {
            if (it.isNotEmpty()) {
                val long = try {
                    it.removePrefix("#").toLong(16)
                } catch (_: Exception) {
                    return@FormatField false
                }
                onColorChange(HsvColor(long))
            }

            true
        }
    )
}

@Composable
fun RgbField(color: HsvColor, onColorChange: (HsvColor) -> Unit) {
    val rgbString = remember(color) {
        listOf(color.red, color.green, color.blue).joinToString(", ") {
            (it * 255).roundToInt().toString()
        }
    }

    FormatField(
        label = "RGB",
        value = rgbString,
        onValueChange = {
            val values = it.split(",").map { it.trim().toFloatOrNull() }

            if (values.size == 3 && values.all { it != null }) {
                val (red, green, blue) = values.map { it!! }
                onColorChange(HsvColor(red, green, blue))

                true
            } else {
                false
            }
        }
    )
}

@Composable
fun HsvField(hsvColor: HsvColor, onColorChange: (HsvColor) -> Unit) {
    val hsvString = remember(hsvColor) {
        "${hsvColor.hue.roundToInt()}°, ${(hsvColor.saturation * 100).roundToInt()}%, ${(hsvColor.value * 100).roundToInt()}%"
    }

    FormatField(
        label = "HSV",
        value = hsvString,
        onValueChange = {
            val values = it.split(",").map { it.trim().toFloatOrNull() }

            if (values.size == 3 && values.all { it != null }) {
                val (hue, saturation, value) = values.map { it!! }
                onColorChange(HsvColor(hue, saturation, value))

                true
            } else {
                false
            }
        }
    )
}

@Composable
private fun FormatField(
    label: String,
    value: String,
    onValueChange: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    var isError by rememberSaveable { mutableStateOf(false) }

    TextField(
        modifier = modifier
            .widthIn(min = 180.dp, max = 240.dp)
            .onFocusChanged {
                if (!it.isFocused) isError = false
            },
        value = value,
        onValueChange = {
            isError = !onValueChange(it)
        },
        label = { Text(label) },
        supportingText = if (isError) {
            { Text("Invalid $label") }
        } else {
            null
        },
        isError = isError,
        singleLine = true
    )
}