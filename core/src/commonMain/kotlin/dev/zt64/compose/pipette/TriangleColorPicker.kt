package dev.zt64.compose.pipette

import androidx.compose.foundation.gestures.awaitDragOrCancellation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
public fun TriangularColorPicker(
    color: () -> HsvColor,
    onColorChange: (color: HsvColor) -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    thumb: @Composable () -> Unit = {
        ColorPickerDefaults.Thumb(color().toColor(), interactionSource)
    },
    onColorChangeFinished: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var size by remember { mutableStateOf(IntSize.Zero) }

    val currentColor by rememberUpdatedState(color)
    val currentOnColorChange by rememberUpdatedState(onColorChange)
    val currentOnColorChangeFinished by rememberUpdatedState(onColorChangeFinished)

    Box(
        modifier = modifier
            .size(ColorPickerDefaults.ComponentSize)
            .onSizeChanged { size = it }
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()

                    // hsvColorForPosition(down.position, size).let { (s, v) ->
                    //     currentOnColorChange(currentColor().copy(saturation = s, value = v))
                    // }

                    // Start drag interaction
                    val interaction = DragInteraction.Start()
                    scope.launch {
                        interactionSource.emit(interaction)
                    }

                    var change = awaitTouchSlopOrCancellation(down.id) { change, _ ->
                        change.consume()
                        // hsvColorForPosition(change.position, size).let { (s, v) ->
                        //     currentOnColorChange(currentColor().copy(saturation = s, value = v))
                        // }
                    }

                    // Continue dragging
                    while (change != null && change.pressed) {
                        change.consume()
                        // hsvColorForPosition(change.position, size).let { (s, v) ->
                        //     currentOnColorChange(currentColor().copy(saturation = s, value = v))
                        // }
                        change = awaitDragOrCancellation(change.id)
                    }

                    scope.launch {
                        interactionSource.emit(DragInteraction.Stop(interaction))
                    }

                    currentOnColorChangeFinished()
                }
            }
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithCache {
                val path = Path().apply {
                    val altitude = this@drawWithCache.size.width * 0.866f
                    val offsetX = this@drawWithCache.size.width / 2f - altitude / 3f
                    moveTo(offsetX, 0f)
                    lineTo(offsetX + altitude, this@drawWithCache.size.height / 2f)
                    lineTo(offsetX, this@drawWithCache.size.height)
                    close()
                }

                onDrawBehind {
                    val saturationBrush = Brush.verticalGradient(listOf(Color.Transparent, Color.Black))
                    val hueBrush = Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            Color.hsv(currentColor().hue, 1f, 1f)
                        )
                    )

                    drawPath(
                        path = path,
                        Color.White
                    )

                    drawPath(
                        path = path,
                        hueBrush
                    )
                    drawPath(
                        path = path,
                        saturationBrush
                    )
                }
            }
    ) {
        Box(
            modifier = Modifier.offset {
                IntOffset(
                    x = (currentColor().saturation * size.width).roundToInt(),
                    y = (size.height - currentColor().value * size.height).roundToInt()
                )
            }
        ) {
            thumb()
        }
    }
}