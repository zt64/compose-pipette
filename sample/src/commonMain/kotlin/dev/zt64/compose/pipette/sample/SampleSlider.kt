package dev.zt64.compose.pipette.sample

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    text: String,
    brush: Brush
) {
    Column {
        val interactionSource = remember { MutableInteractionSource() }

        Text(
            text = "$text slider (demo only)",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        Slider(
            value = value,
            onValueChange = onValueChange,
            thumb = {
                val interactions = remember { mutableStateListOf<Interaction>() }

                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect { interaction ->
                        when (interaction) {
                            is PressInteraction.Press -> interactions.add(interaction)
                            is PressInteraction.Release -> interactions.remove(interaction.press)
                            is PressInteraction.Cancel -> interactions.remove(interaction.press)
                            is DragInteraction.Start -> interactions.add(interaction)
                            is DragInteraction.Stop -> interactions.remove(interaction.start)
                            is DragInteraction.Cancel -> interactions.remove(interaction.start)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .hoverable(interactionSource = interactionSource)
                ) {
                    val visualSize = if (interactions.isNotEmpty()) 28.dp else 24.dp

                    Spacer(
                        modifier = Modifier
                            .size(visualSize)
                            .align(Alignment.Center)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    )
                }
            },
            track = {
                Canvas(
                    modifier = Modifier
                        .widthIn(max = 700.dp)
                        .height(12.dp)
                        .fillMaxWidth()
                ) {
                    drawLine(
                        brush = brush,
                        start = Offset(0f, size.center.y),
                        end = Offset(size.width, size.center.y),
                        strokeWidth = size.height,
                        cap = StrokeCap.Round
                    )
                }
            },
            valueRange = valueRange,
            interactionSource = interactionSource
        )
    }
}