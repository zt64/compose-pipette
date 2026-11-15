package dev.zt64.compose.pipette

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable

/**
 * Color picker state
 *
 * @constructor Creates a [ColorPickerState] with the given initial color.
 * @param initialColor the initial color
 */
@Stable
public class ColorPickerState(initialColor: HsvColor) {
    public var color: HsvColor by mutableStateOf(initialColor)
        private set

    /** Updates the color, including when it is changed outside a picker. */
    public fun updateColor(color: HsvColor) {
        this.color = color
    }

    public companion object {
        /**
         * The default saver for [ColorPickerState].
         */
        public val Saver: Saver<ColorPickerState, *> = Saver(
            save = { state -> state.color.packedValue.toLong() },
            restore = { color -> ColorPickerState(HsvColor(color.toULong())) }
        )
    }
}

/**
 * Remember a color picker state
 *
 * @param initialColor The initial color to start at
 */
@Stable
@Composable
public fun rememberColorPickerState(initialColor: HsvColor): ColorPickerState {
    return rememberSaveable(
        initialColor,
        saver = ColorPickerState.Saver
    ) { ColorPickerState(initialColor) }
}