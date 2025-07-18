package dev.zt64.compose.pipette

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.*
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
import androidx.compose.ui.unit.*
import kotlinx.coroutines.launch
import kotlin.math.*

/**
 * A ring color picker that allows the user to select a hue by rotating a handle around the ring. The ring is
 * a continuous gradient of colors from red to red.
 *
 * To be able to also control the saturation, use the [CircularColorPicker] composable.
 *
 * @param color The current color
 * @param onColorChange Callback that is called when the color changes
 * @param modifier The modifier to be applied to the color picker
 * @param interactionSource The interaction source for the color picker
 * @param ringStrokeWidth The width of the ring
 * @param thumb The composable that is used to draw the thumb
 * @param onColorChangeFinished Callback that is called when the user finishes changing the color
 *
 * @see CircularColorPicker
 * @see SquareColorPicker
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
public fun RingColorPicker(
    color: () -> HsvColor,
    onColorChange: (HsvColor) -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    ringStrokeWidth: Dp = 16.dp,
    thumb: @Composable () -> Unit = {
        ColorPickerDefaults.Thumb(Color.hsv(color().hue, 1f, 1f), interactionSource)
    },
    onColorChangeFinished: () -> Unit = {}
) {
    RingColorPicker(
        hue = { color().hue },
        onHueChange = { hue -> onColorChange(color().copy(hue = hue)) },
        modifier = modifier,
        interactionSource = interactionSource,
        ringStrokeWidth = ringStrokeWidth,
        thumb = thumb,
        onColorChangeFinished = onColorChangeFinished
    )
}

/**
 * A ring color picker that allows the user to select a hue by rotating a handle around the ring. The ring is
 * a continuous gradient of colors from red to red.
 *
 * To be able to also control the saturation, use the [CircularColorPicker] composable.
 *
 * @param hue The hue of the color
 * @param saturation The saturation of the color
 * @param value The value of the color
 * @param onHueChange Callback that is called when the hue changes
 * @param modifier The modifier to be applied to the color picker
 * @param interactionSource The interaction source for the color picker
 * @param ringStrokeWidth The width of the ring
 * @param thumb The composable that is used to draw the thumb
 * @param onColorChangeFinished Callback that is called when the user finishes changing the color
 *
 * @see CircularColorPicker
 * @see SquareColorPicker
 */
@Composable
public fun RingColorPicker(
    hue: () -> Float,
    saturation: () -> Float = { 1f },
    value: () -> Float = { 1f },
    onHueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    ringStrokeWidth: Dp = 16.dp,
    thumb: @Composable () -> Unit = {
        ColorPickerDefaults.Thumb(Color.hsv(hue(), saturation(), value()), interactionSource)
    },
    onColorChangeFinished: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var radius by remember { mutableStateOf(0f) }
    var center by remember { mutableStateOf(Offset.Zero) }
    val strokeWidth = with(LocalDensity.current) { ringStrokeWidth.toPx() }

    fun updateHandlePosition(position: Offset) {
        val (dx, dy) = position - center
        val theta = atan2(dy, dx)
        var angle = theta * (180.0 / PI).toFloat()

        if (angle < 0) angle += 360f

        onHueChange(angle)
    }

    Box(
        modifier = modifier
            .size(ColorPickerDefaults.ComponentSize)
            .onSizeChanged {
                radius = (it.width - strokeWidth) / 2f
                center = Offset(it.width / 2f, it.height / 2f)
            }
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    val downPosition = down.position
                    val center = size.center.toOffset()

                    val distanceSquared = (downPosition - center).getDistanceSquared()
                    val halfStroke = strokeWidth / 2f
                    val innerRadiusSquared = (radius - halfStroke) * (radius - halfStroke)
                    val outerRadiusSquared = (radius + halfStroke) * (radius + halfStroke)

                    if (distanceSquared !in innerRadiusSquared..outerRadiusSquared) return@awaitEachGesture

                    // Handle initial tap
                    updateHandlePosition(downPosition)

                    val interaction = DragInteraction.Start()
                    scope.launch {
                        interactionSource.emit(interaction)
                    }

                    var change = awaitTouchSlopOrCancellation(down.id) { change, _ ->
                        change.consume()
                        updateHandlePosition(change.position)
                    }

                    while (change != null && change.pressed) {
                        change = awaitDragOrCancellation(change.id)
                        if (change != null && change.pressed) {
                            updateHandlePosition(change.position)
                        }
                    }

                    scope.launch {
                        interactionSource.emit(DragInteraction.Stop(interaction))
                    }

                    onColorChangeFinished()
                }
            }
            .drawWithCache {
                val brush = Brush.sweepGradient(
                    List(7) {
                        Color.hsv(
                            hue = (it * 60).toFloat(),
                            saturation = saturation(),
                            value = value()
                        )
                    }
                )

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
                val rad = hue() * (PI / 180).toFloat()
                val x = center.x + radius * cos(rad)
                val y = center.y + radius * sin(rad)

                IntOffset(x.roundToInt(), y.roundToInt())
            }
        ) {
            thumb()
        }
    }
}