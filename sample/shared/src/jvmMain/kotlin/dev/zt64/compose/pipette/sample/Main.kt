package dev.zt64.compose.pipette.sample

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.window.singleWindowApplication
import java.awt.Dimension
import kotlin.system.exitProcess

fun main() {
    singleWindowApplication(
        title = "Compose Pipette Sample",
        onKeyEvent = {
            if (it.key == Key.Escape) {
                exitProcess(0)
            } else {
                false
            }
        }
    ) {
        window.minimumSize = Dimension(900, 800)

        Sample()
    }
}