package dev.zt64.compose.pipette

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.graphics.Color
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName
import kotlin.math.max

/**
 * Represents a color in the HSV (Hue, Saturation, Value) color space.
 *
 * This class provides properties to access the hue, saturation, and value of the color,
 * as well as methods to convert to and from RGB color space.
 */
@Immutable
@JvmInline
public value class HsvColor(public val packedValue: Long) {
    /**
     * The hue of the color, in degrees (0-360).
     */
    @Stable
    public val hue: Float
        get() = (packedValue shr 40 and 0xFFFF) / 100f

    /**
     * The saturation of the color (0-1).
     */
    @Stable
    public val saturation: Float
        get() = (packedValue shr 20 and 0xFFFFF) / 1000000f

    /**
     * The value (brightness) of the color (0-1).
     */
    @Stable
    public val value: Float
        get() = (packedValue and 0xFFFFF) / 1000000f

    @Stable
    public val red: Float
        get() = hsvToRgbComponent(5, hue, saturation, value)

    @Stable
    public val green: Float
        get() = hsvToRgbComponent(3, hue, saturation, value)

    @Stable
    public val blue: Float
        get() = hsvToRgbComponent(1, hue, saturation, value)

    /**
     * Creates an HsvColor from the given hue, saturation, and value.
     */
    public constructor(hue: Float, saturation: Float, value: Float) : this(
        ((hue * 100).toLong() and 0xFFFF shl 40) or
            ((saturation * 1000000).toLong() and 0xFFFFF shl 20) or
            ((value * 1000000).toLong() and 0xFFFFF)
    )

    /**
     * Converts the HSV color to a Color object.
     *
     * **Note**: HSV to RGB conversion is a lossy process, so the resulting color may not be exactly the same as the original HSV color.
     * When red, green and blue are equal, hue will be 0 and saturation will be 0.
     */
    public fun toColor(): Color = Color.hsv(hue, saturation, value)

    @Suppress("NOTHING_TO_INLINE")
    @Stable
    public inline operator fun component1(): Float = hue

    @Suppress("NOTHING_TO_INLINE")
    @Stable
    public inline operator fun component2(): Float = saturation

    @Suppress("NOTHING_TO_INLINE")
    @Stable
    public inline operator fun component3(): Float = value

    /**
     * Copies the existing color, changing only the provided values.
     */
    @Stable
    public fun copy(hue: Float = this.hue, saturation: Float = this.saturation, value: Float = this.value): HsvColor {
        return HsvColor(hue, saturation, value)
    }

    /**
     * Returns a string representation of the color in HSV format.
     */
    @Stable
    override fun toString(): String {
        return "HsvColor(hue=$hue, saturation=$saturation, value=$value)"
    }

    public companion object {
        public val Saver: Saver<HsvColor, Long> = Saver(
            save = { it.packedValue.toLong() },
            restore = { HsvColor(it) }
        )

        @JvmName("colorToHsvColor")
        public operator fun invoke(color: Color): HsvColor {
            val (r, g, b) = color
            val max = maxOf(r, g, b)
            val min = minOf(r, g, b)
            val delta = max - min

            val hue = when (max) {
                r -> ((g - b) / delta) % 6
                g -> ((b - r) / delta) + 2
                b -> ((r - g) / delta) + 4
                else -> 0f
            }

            return HsvColor(
                hue = hue,
                saturation = if (max == 0f) 0f else 1 - min / max,
                value = max
            )
        }

        @JvmName("longToHsvColor")
        public operator fun invoke(color: Long): HsvColor = HsvColor(
            hue = ((color shr 16) and 0xFF).toFloat(),
            saturation = ((color shr 8) and 0xFF).toFloat(),
            value = (color and 0xFF).toFloat()
        )

        private fun hsvToRgbComponent(n: Int, h: Float, s: Float, v: Float): Float {
            val k = (n.toFloat() + h / 60f) % 6f
            return v - (v * s * max(0f, minOf(k, 4 - k, 1f)))
        }
    }
}