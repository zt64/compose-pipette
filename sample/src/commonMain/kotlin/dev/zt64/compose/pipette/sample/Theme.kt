package dev.zt64.compose.pipette.sample

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.vector.ImageVector
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle
import dev.zt64.compose.pipette.util.hsvValue
import dev.zt64.compose.pipette.util.hue
import dev.zt64.compose.pipette.util.saturation

@Stable
enum class Theme(val icon: ImageVector, val label: String) {
    LIGHT(Icons.Default.LightMode, "Light"),
    DARK(Icons.Default.DarkMode, "Dark"),
    SYSTEM(Icons.Default.Settings, "System")
}

@Composable
fun Theme(color: ColorProducer, theme: Theme, useDynamicTheme: Boolean, content: @Composable () -> Unit) {
    if (useDynamicTheme) {
        DynamicMaterialTheme(
            seedColor = color().let {
                if (it.saturation < 0.5f || it.hsvValue < 0.5f) {
                    Color.hsv(
                        hue = it.hue,
                        saturation = it.saturation.coerceAtLeast(0.5f),
                        value = it.hsvValue.coerceAtLeast(0.5f)
                    )
                } else {
                    it
                }
            },
            useDarkTheme = theme == Theme.DARK || theme == Theme.SYSTEM && isSystemInDarkTheme(),
            style = PaletteStyle.Fidelity,
            content = content
        )
    } else {
        MaterialTheme(
            colorScheme = if (theme == Theme.DARK || theme == Theme.SYSTEM && isSystemInDarkTheme()) {
                darkColorScheme()
            } else {
                lightColorScheme()
            }
        ) {
            content()
        }
    }
}