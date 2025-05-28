package dev.zt64.compose.pipette

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * The standard square color picker that allows the user to select a color by dragging a thumb around the color space.
 *
 * The color is represented in HSV color space with a fixed hue. The saturation and value can be controlled by
 * dragging the thumb.
 *
 * @param color The current color
 * @param onColorChange Callback that is called when the color changes
 * @param modifier The modifier to be applied to the color picker
 * @param interactionSource The interaction source for the color picker
 * @param thumb Composable that is used to draw the thumb
 * @param shape The shape of the color picker, note that the corner radius should be kept small,
 * to prevent the thumb from visually appearing outside the color picker
 * @param onColorChangeFinished Callback that is called when the user finishes changing the color
 *
 * @see CircularColorPicker
 * @see RingColorPicker
 */
@Composable
public fun SquareColorPicker(
    color: HsvColor,
    onColorChange: (color: HsvColor) -> Unit,
    modifier: Modifier = Modifier.size(128.dp),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    thumb: @Composable () -> Unit = {
        ColorPickerDefaults.Thumb(color.toColor(), interactionSource)
    },
    shape: Shape = RectangleShape,
    onColorChangeFinished: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var size by remember { mutableStateOf(IntSize.Zero) }
    val updatedColor by rememberUpdatedState(color)
    val saturationBrush = remember {
        Brush.verticalGradient(listOf(Color.Transparent, Color.Black))
    }

    val hueBrush = remember(updatedColor.hue) {
        Brush.horizontalGradient(
            listOf(
                Color.Transparent,
                Color.hsv(updatedColor.hue, 1f, 1f)
            )
        )
    }

    Box {
        Canvas(
            modifier = modifier
                .onSizeChanged { size = it }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            hsvColorForPosition(it, size).let { (s, v) ->
                                onColorChange(updatedColor.copy(saturation = s, value = v))
                            }
                            onColorChangeFinished()
                        }
                    )
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
                        onDrag = { change, _ ->
                            hsvColorForPosition(change.position, size).let { (s, v) ->
                                onColorChange(updatedColor.copy(saturation = s, value = v))
                            }
                        },
                        onDragEnd = {
                            scope.launch {
                                interaction?.let {
                                    interactionSource.emit(DragInteraction.Stop(it))
                                }
                            }
                            onColorChangeFinished()
                        },
                        onDragCancel = {
                            scope.launch {
                                interaction?.let {
                                    interactionSource.emit(DragInteraction.Cancel(it))
                                }
                            }
                        }
                    )
                }
                .clip(shape)
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        ) {
            drawRect(Color.White)
            drawRect(hueBrush)
            drawRect(saturationBrush)
        }

        Box(
            modifier = Modifier.offset {
                IntOffset(
                    x = (updatedColor.saturation * size.width).roundToInt(),
                    y = (size.height - updatedColor.value * size.height).roundToInt()
                )
            }
        ) {
            thumb()
        }
    }
}

private fun hsvColorForPosition(position: Offset, size: IntSize): Pair<Float, Float> {
    val clampedX = position.x.coerceIn(0f, size.width.toFloat())
    val clampedY = position.y.coerceIn(0f, size.height.toFloat())

    val saturation = clampedX / size.width
    val value = 1f - (clampedY / size.height)

    return Pair(saturation, value)
}