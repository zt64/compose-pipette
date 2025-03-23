package dev.zt64.compose.pipette

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.zt64.compose.pipette.util.hsvValue
import dev.zt64.compose.pipette.util.hue
import dev.zt64.compose.pipette.util.saturation
import kotlinx.coroutines.launch
import kotlin.math.*

/**
 * A color ring that allows the user to select a hue by rotating a handle around the ring. The color
 * ring is a continuous gradient of colors from red to red.
 *
 * To be able to also control the saturation, use the [ColorCircle] composable.
 *
 * @param color The current color
 * @param onColorChange Callback that is called when the color changes
 * @param modifier The modifier to be applied to the color ring
 * @param interactionSource The interaction source for the color ring
 * @param ringStrokeWidth The width of the ring
 * @param thumb The composable that is used to draw the thumb
 * @param onColorChangeFinished Callback that is called when the user finishes changing the color
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
public fun ColorRing(
    color: Color,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    ringStrokeWidth: Dp = 16.dp,
    thumb: @Composable () -> Unit = {
        ColorPickerDefaults.Thumb(color, interactionSource)
    },
    onColorChangeFinished: () -> Unit = {}
) {
    val color by rememberUpdatedState(color)
    var radius by remember { mutableStateOf(0f) }
    var center by remember { mutableStateOf(Offset.Zero) }
    val scope = rememberCoroutineScope()
    val strokeWidth = with(LocalDensity.current) { ringStrokeWidth.toPx() }

    val brush = remember(color) {
        Brush.sweepGradient(
            List(7) {
                Color.hsv(
                    hue = (it * 60).toFloat(),
                    saturation = color.saturation,
                    value = color.hsvValue
                )
            }
        )
    }

    fun updateHandlePosition(position: Offset) {
        onColorChange(
            Color.hsv(
                hue = getRotationAngle(position, center),
                saturation = color.saturation,
                value = color.hsvValue
            )
        )
    }

    Box(
        modifier = modifier
            .size(128.dp)
            .onSizeChanged {
                radius = (it.width - strokeWidth) / 2f
                center = Offset(it.width / 2f, it.height / 2f)
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    updateHandlePosition(offset)
                    onColorChangeFinished()
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
                    updateHandlePosition(change.position)
                }
            }
            .drawWithCache {
                onDrawBehind {
                    drawCircle(
                        brush = brush,
                        radius = size.minDimension / 2 - strokeWidth / 2f,
                        style = Stroke(strokeWidth)
                    )
                }
            }
    ) {
        Box(
            modifier = Modifier.offset {
                val rad = color.hue * (PI / 180f).toFloat()
                val handleOffset = center + Offset(radius * cos(rad), radius * sin(rad))
                IntOffset(handleOffset.x.roundToInt(), handleOffset.y.roundToInt())
            }
        ) {
            thumb()
        }
    }
}

private fun getRotationAngle(currentPosition: Offset, center: Offset): Float {
    val (dx, dy) = currentPosition - center
    val theta = atan2(dy, dx)
    var angle = theta * (180.0 / PI).toFloat()

    if (angle < 0) angle += 360f

    return angle
}