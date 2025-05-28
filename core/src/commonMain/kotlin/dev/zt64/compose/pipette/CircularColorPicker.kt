package dev.zt64.compose.pipette

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import kotlin.math.*

/**
 * Circular color picker that allows the user to select a hue by dragging a thumb around the circle.
 * The color is represented in HSV color space with a fixed value.
 *
 * @param color The current color
 * @param onColorChange Callback that is called when the color changes
 * @param modifier The modifier to be applied to the color picker
 * @param interactionSource The interaction source for the color picker
 * @param onColorChangeFinished Callback that is called when the user finishes changing the color
 * @param thumb Composable that is used to draw the thumb
 *
 * @see RingColorPicker
 * @see SquareColorPicker
 */
@Composable
public fun CircularColorPicker(
    color: HsvColor,
    onColorChange: (HsvColor) -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onColorChangeFinished: () -> Unit = {},
    thumb: @Composable () -> Unit = {
        ColorPickerDefaults.Thumb(color.toColor(), interactionSource)
    }
) {
    val updatedColor by rememberUpdatedState(color)

    CircularColorPicker(
        hue = updatedColor.hue,
        saturation = updatedColor.saturation,
        value = updatedColor.value,
        onColorChange = { h, s -> onColorChange(updatedColor.copy(hue = h, saturation = s)) },
        modifier = modifier,
        interactionSource = interactionSource,
        onColorChangeFinished = onColorChangeFinished,
        thumb = thumb
    )
}

/**
 * Circular color picker that allows the user to select a hue by dragging a thumb around the circle.
 * The color is represented in HSV color space with a fixed value.
 *
 * @param hue The hue of the color
 * @param saturation The saturation of the color
 * @param value The value of the color
 * @param onColorChange Callback that is called when the color changes
 * @param modifier The modifier to be applied to the color picker
 * @param interactionSource The interaction source for the color picker
 * @param onColorChangeFinished Callback that is called when the user finishes changing the color
 * @param thumb Composable that is used to draw the thumb
 *
 * @see RingColorPicker
 * @see SquareColorPicker
 */
@Composable
public fun CircularColorPicker(
    hue: Float,
    saturation: Float,
    value: Float = 1f,
    onColorChange: (hue: Float, saturation: Float) -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onColorChangeFinished: () -> Unit = {},
    thumb: @Composable () -> Unit = {
        ColorPickerDefaults.Thumb(Color.hsv(hue, saturation, value), interactionSource)
    }
) {
    val scope = rememberCoroutineScope()
    var radius by remember { mutableStateOf(0f) }

    val hueBrush = remember(value) {
        Brush.sweepGradient(
            colors = List(7) { i ->
                Color.hsv(
                    hue = i * 60f,
                    saturation = 1f,
                    value = value
                )
            }
        )
    }

    val saturationBrush = remember(value) {
        Brush.radialGradient(listOf(Color.hsv(0f, 0f, value), Color.Transparent))
    }

    Box(
        modifier = modifier
            .size(ColorPickerDefaults.ComponentSize)
            .onSizeChanged {
                radius = it.width / 2f
            }
            .pointerInput(Unit) {
                detectTapGestures { tapPosition ->
                    colorForPosition(tapPosition, radius)?.let { (h, s) ->
                        onColorChange(h, s)
                    }
                }
            }
            .pointerInput(Unit) {
                var interaction: DragInteraction.Start? = null

                detectDragGestures(
                    onDragStart = {
                        scope.launch {
                            interaction = DragInteraction.Start()
                            interactionSource.emit(interaction)
                        }
                    },
                    onDragEnd = {
                        scope.launch {
                            interaction?.let { interactionSource.emit(DragInteraction.Stop(it)) }
                        }
                        onColorChangeFinished()
                    },
                    onDragCancel = {
                        scope.launch {
                            interaction?.let { interactionSource.emit(DragInteraction.Cancel(it)) }
                        }
                        onColorChangeFinished()
                    }
                ) { change, _ ->
                    change.consume()
                    val newPosition = clampPositionToRadius(change.position, radius)
                    colorForPosition(newPosition, radius)?.let { (h, s) ->
                        onColorChange(h, s)
                    }
                }
            }
            .drawWithCache {
                onDrawBehind {
                    drawCircle(hueBrush)
                    drawCircle(saturationBrush)
                }
            }
    ) {
        Box(
            modifier = Modifier.offset {
                val angle = hue * (PI / 180).toFloat()
                val distance = saturation * radius

                IntOffset(
                    x = (radius + distance * cos(angle)).roundToInt(),
                    y = (radius + distance * sin(angle)).roundToInt()
                )
            }
        ) {
            thumb()
        }
    }
}

private fun colorForPosition(position: Offset, radius: Float): Pair<Float, Float>? {
    val xOffset = position.x - radius
    val yOffset = position.y - radius

    val centerOffset = hypot(xOffset, yOffset)

    if (centerOffset > radius) return null

    val degrees = atan2(yOffset, xOffset) * (180 / PI).toFloat()
    val centerAngle = (degrees + 360) % 360

    return centerAngle to centerOffset / radius
}

private fun clampPositionToRadius(position: Offset, radius: Float): Offset {
    val xOffset = position.x - radius
    val yOffset = position.y - radius

    val centerOffset = hypot(xOffset, yOffset)

    // If the position is outside the circle, adjust it to be on the edge in the same direction
    return if (centerOffset > radius) {
        val scale = radius / centerOffset
        Offset((xOffset * scale) + radius, (yOffset * scale) + radius)
    } else {
        // Otherwise, the position is inside the circle so return it as is
        position
    }
}