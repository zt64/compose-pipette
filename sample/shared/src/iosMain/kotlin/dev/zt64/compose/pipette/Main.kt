package dev.zt64.compose.pipette

import androidx.compose.ui.window.ComposeUIViewController
import dev.zt64.compose.pipette.sample.Sample
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    Sample()
}