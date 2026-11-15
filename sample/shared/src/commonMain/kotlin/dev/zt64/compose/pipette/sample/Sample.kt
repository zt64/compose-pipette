package dev.zt64.compose.pipette.sample

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.zt64.compose.pipette.*
import dev.zt64.compose.pipette.sample.ui.icon.GithubIcon
import dev.zt64.compose.pipette.sample.ui.icon.Palette
import dev.zt64.compose.pipette.sample.ui.icon.PaletteFilled
import dev.zt64.compose.pipette.sample.ui.icon.Refresh
import dev.zt64.compose.pipette.sample.ui.theme.Theme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun Sample() {
    val colorPickerState = rememberColorPickerState(HsvColor(180f, 1f, 1f))

    var theme by rememberSaveable { mutableStateOf(Theme.SYSTEM) }
    var useDynamicTheme by rememberSaveable { mutableStateOf(false) }

    Theme(
        color = { colorPickerState.color },
        theme = theme,
        useDynamicTheme = useDynamicTheme
    ) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = { Text("Compose Pipette Sample") },
                    actions = {
                        var expanded by remember { mutableStateOf(false) }
                        val uriHandler = LocalUriHandler.current

                        IconButton(
                            onClick = {
                                uriHandler.openUri("https://github.com/zt64/compose-pipette")
                            }
                        ) {
                            Icon(
                                imageVector = GithubIcon,
                                contentDescription = null
                            )
                        }

                        IconButton(
                            onClick = { useDynamicTheme = !useDynamicTheme }
                        ) {
                            Icon(
                                imageVector = if (useDynamicTheme) {
                                    PaletteFilled
                                } else {
                                    Palette
                                },
                                contentDescription = null
                            )
                        }

                        Box {
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                Theme.entries.forEach {
                                    DropdownMenuItem(
                                        text = { Text(it.label) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = it.icon,
                                                contentDescription = null
                                            )
                                        },
                                        onClick = {
                                            theme = it
                                            expanded = false
                                        }
                                    )
                                }
                            }

                            IconButton(
                                onClick = { expanded = true }
                            ) {
                                Icon(
                                    imageVector = theme.icon,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 760.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Pick a color", style = MaterialTheme.typography.headlineSmall)
                                Text(
                                    text = "Test the color picker components",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .drawBehind { drawCircle(colorPickerState.color.toColor()) }
                                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                    .semantics { contentDescription = "Current color" }
                            )
                        }

                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            val pickerWidth = with(LocalDensity.current) {
                                minOf(
                                    170.dp.roundToPx(),
                                    (constraints.maxWidth - 12.dp.roundToPx()).coerceAtLeast(0) / 2
                                ).toDp()
                            }

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                @Composable
                                fun Picker(
                                    label: String,
                                    description: String,
                                    content: @Composable () -> Unit
                                ) {
                                    OutlinedCard(
                                        modifier = Modifier.width(pickerWidth),
                                        colors = CardDefaults.outlinedCardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Box(
                                                modifier = Modifier.fillMaxWidth().aspectRatio(1f).padding(14.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                content()
                                            }

                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.titleMedium,
                                                textAlign = TextAlign.Center
                                            )

                                            Text(
                                                text = description,
                                                modifier = Modifier.padding(horizontal = 4.dp),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = TextAlign.Center,
                                                minLines = 2
                                            )
                                        }
                                    }
                                }

                                Picker("Circle", "Hue + saturation") {
                                    CircularColorPicker(colorPickerState)
                                }

                                Picker("Square", "Saturation + value") {
                                    SquareColorPicker(
                                        state = colorPickerState,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }

                                Picker("Ring", "Hue") {
                                    RingColorPicker(colorPickerState)
                                }

                                Picker("Triangle", "Hue + saturation + value") {
                                    Box(contentAlignment = Alignment.Center) {
                                        RingColorPicker(
                                            state = colorPickerState,
                                            thumb = {
                                                Canvas(
                                                    Modifier
                                                        .size(16.dp)
                                                        .offset((-8).dp, (-8).dp)
                                                ) {
                                                    val angle = colorPickerState.color.hue * (PI / 180f).toFloat()
                                                    val cx = size.width / 2f
                                                    val cy = size.height / 2f
                                                    drawLine(
                                                        color = Color.White,
                                                        start = Offset(cx, cy),
                                                        end = Offset(
                                                            cx + cos(angle) * cx,
                                                            cy + sin(angle) * cy
                                                        ),
                                                        strokeWidth = 6.dp.toPx()
                                                    )
                                                }
                                            }
                                        )

                                        TriangularColorPicker(
                                            modifier = Modifier.size(78.dp),
                                            state = colorPickerState
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Column {
                        Text("Adjust color", style = MaterialTheme.typography.headlineSmall)
                        Text(
                            "Sliders for demonstration",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    SampleSlider(
                        value = { colorPickerState.color.hue },
                        onValueChange = { colorPickerState.updateColor(colorPickerState.color.copy(hue = it)) },
                        valueRange = 0f..359f,
                        text = "Hue",
                        brush = {
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Red,
                                    Color.Yellow,
                                    Color.Green,
                                    Color.Cyan,
                                    Color.Blue,
                                    Color.Magenta,
                                    Color.Red
                                )
                            )
                        }
                    )

                    SampleSlider(
                        value = { colorPickerState.color.saturation },
                        onValueChange = { colorPickerState.updateColor(colorPickerState.color.copy(saturation = it)) },
                        valueRange = 0f..1f,
                        text = "Saturation",
                        brush = {
                            Brush.horizontalGradient(
                                listOf(
                                    Color.White,
                                    Color.hsv(colorPickerState.color.hue, 1f, colorPickerState.color.value)
                                )
                            )
                        }
                    )

                    SampleSlider(
                        value = { colorPickerState.color.value },
                        onValueChange = { colorPickerState.updateColor(colorPickerState.color.copy(value = it)) },
                        valueRange = 0f..1f,
                        text = "Value",
                        brush = {
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Black,
                                    Color.hsv(colorPickerState.color.hue, colorPickerState.color.saturation, 1f)
                                )
                            )
                        }
                    )

                    Button(
                        onClick = {
                            val h = (0..359).random().toFloat()
                            val s = (20..100).random().toFloat() / 100f

                            colorPickerState.updateColor(colorPickerState.color.copy(h, s))
                        }
                    ) {
                        Icon(
                            imageVector = Refresh,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                        Text("Randomize")
                    }

                    var valuesExpanded by rememberSaveable { mutableStateOf(false) }
                    Column(modifier = Modifier.fillMaxWidth()) {
                        HorizontalDivider()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .toggleable(
                                    value = valuesExpanded,
                                    role = Role.Button,
                                    onValueChange = { valuesExpanded = it }
                                )
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Color values",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Icon(
                                imageVector = if (valuesExpanded) Icons.Default.KeyboardArrowUp
                                else Icons.Default.KeyboardArrowDown,
                                contentDescription = null
                            )
                        }

                        AnimatedVisibility(valuesExpanded) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                HexField(colorPickerState)
                                RgbField(colorPickerState)
                                HsvField(colorPickerState)
                            }
                        }
                    }
                }
            }
        }
    }
}