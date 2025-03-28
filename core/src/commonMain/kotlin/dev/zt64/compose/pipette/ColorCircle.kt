package dev.zt64.compose.pipette

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.zt64.compose.pipette.util.hsvValue
import dev.zt64.compose.pipette.util.hue
import dev.zt64.compose.pipette.util.saturation
import kotlinx.coroutines.launch
import kotlin.math.*

/**
 * Color circle that allows the user to select a hue by dragging a thumb around the circle.
 * The color is represented in HSV color space with a fixed saturation and value.
 *
 * @param color The current color
 * @param onColorChange Callback that is called when the color changes
 * @param modifier The modifier to be applied to the color circle
 * @param interactionSource The interaction source for the color circle
 * @param onColorChangeFinished Callback that is called when the user finishes changing the color
 * @param thumb Composable that is used to draw the thumb
 *
 * @see ColorRing
 * @see ColorSquare
 */
@Composable
public fun ColorCircle(
    color: Color,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onColorChangeFinished: () -> Unit = {},
    thumb: @Composable (Color) -> Unit = {
        ColorPickerDefaults.Thumb(color, interactionSource)
    }
) {
    val scope = rememberCoroutineScope()
    val updatedColor by rememberUpdatedState(color)
    var radius by rememberSaveable { mutableStateOf(0f) }
    val value by remember { derivedStateOf { updatedColor.hsvValue } }

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
            .size(128.dp)
            .onGloballyPositioned {
                radius = it.size.width.toFloat() / 2f
            }
            .pointerInput(Unit) {
                detectTapGestures { tapPosition ->
                    val newColor = colorForPosition(tapPosition, radius, updatedColor.hsvValue)
                    if (newColor.isSpecified) onColorChange(newColor)
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
                    val newColor = colorForPosition(newPosition, radius, updatedColor.hsvValue)
                    if (newColor.isSpecified) onColorChange(newColor)
                }
            }
            .drawWithCache {
                onDrawBehind {
                    drawCircle(hueBrush)
                    drawCircle(saturationBrush)
                }
            }
    ) {
        val position = remember(updatedColor, radius) {
            val angle = updatedColor.hue * (PI / 180).toFloat()
            val distance = updatedColor.saturation * radius

            Offset(
                x = radius + distance * cos(angle),
                y = radius + distance * sin(angle)
            )
        }

        Box(
            modifier = Modifier.offset {
                IntOffset(position.x.roundToInt(), position.y.roundToInt())
            }
        ) {
            thumb(updatedColor)
        }
    }
}

private fun colorForPosition(position: Offset, radius: Float, value: Float): Color {
    val xOffset = position.x - radius
    val yOffset = position.y - radius

    val centerOffset = hypot(xOffset, yOffset)

    if (centerOffset > radius) return Color.Unspecified

    val degrees = atan2(yOffset, xOffset) * (180 / PI).toFloat()
    val centerAngle = (degrees + 360) % 360

    return Color.hsv(
        hue = centerAngle,
        saturation = centerOffset / radius,
        value = value
    )
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