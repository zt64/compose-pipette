package dev.zt64.compose.pipette

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal val ThumbRadiusPressed = 14.dp
internal val ThumbRadius = 10.dp

public object ColorPickerDefaults {
    /**
     * The default size applied to color pickers. Note that you can override it by applying
     * Modifier.size directly on a picker component.
     */
    public val ComponentSize: Dp = 128.dp

    /**
     * Default implementation of the thumb component for the color pickers.
     *
     * @param color The color of the thumb
     * @param interactionSource The interaction source for the thumb
     * @param modifier The modifier for the thumb
     */
    @Composable
    public fun Thumb(color: Color, interactionSource: MutableInteractionSource, modifier: Modifier = Modifier) {
        val isPressed by interactionSource.collectIsPressedAsState()
        val isDragged by interactionSource.collectIsDraggedAsState()
        val radius by animateDpAsState(
            targetValue = if (isPressed || isDragged) ThumbRadiusPressed else ThumbRadius
        )

        Canvas(modifier = modifier) {
            drawCircle(color, radius = radius.toPx())

            // Draw the border
            drawCircle(
                color = color.contrastingColor,
                radius = radius.toPx(),
                alpha = 0.5f,
                style = Stroke(1.dp.toPx())
            )
        }
    }
}

internal val Color.contrastingColor: Color
    get() = if (this.isDark()) Color.White else Color.Black

internal fun Color.isDark(): Boolean {
    return 0.2126 * this.red + 0.7152 * this.green + 0.0722 * this.blue < .5f
}