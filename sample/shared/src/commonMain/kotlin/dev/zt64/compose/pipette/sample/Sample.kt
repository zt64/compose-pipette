package dev.zt64.compose.pipette.sample

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import dev.zt64.compose.pipette.CircularColorPicker
import dev.zt64.compose.pipette.HsvColor
import dev.zt64.compose.pipette.RingColorPicker
import dev.zt64.compose.pipette.SquareColorPicker
import dev.zt64.compose.pipette.TriangularColorPicker
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
    var hsvColor by rememberSaveable(stateSaver = HsvColor.Saver) {
        mutableStateOf(HsvColor(180f, 1f, 1f))
    }

    var theme by rememberSaveable { mutableStateOf(Theme.SYSTEM) }
    var useDynamicTheme by rememberSaveable { mutableStateOf(false) }

    Theme(
        color = { hsvColor },
        theme = theme,
        useDynamicTheme = useDynamicTheme
    ) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
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
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.height(IntrinsicSize.Min)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                HexField(
                                    color = { hsvColor },
                                    onColorChange = { hsvColor = it }
                                )

                                RgbField(
                                    color = { hsvColor },
                                    onColorChange = { hsvColor = it }
                                )

                                HsvField(
                                    hsvColor = { hsvColor },
                                    onColorChange = { hsvColor = it }
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .fillMaxHeight()
                                    .clip(MaterialTheme.shapes.medium)
                                    .drawBehind {
                                        drawRect(hsvColor.toColor())
                                    }
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("CircularColorPicker")

                            Spacer(Modifier.height(6.dp))

                            CircularColorPicker(
                                color = { hsvColor },
                                onColorChange = { hsvColor = it }
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("SquareColorPicker")

                            Spacer(Modifier.height(6.dp))

                            SquareColorPicker(
                                color = { hsvColor },
                                onColorChange = { hsvColor = it },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("RingColorPicker")

                            Spacer(Modifier.height(6.dp))

                            RingColorPicker(
                                color = { hsvColor },
                                onColorChange = { hsvColor = it }
                            )
                        }

                        Column {
                            Text("TriangularColorPicker")

                            Spacer(Modifier.height(6.dp))

                            Box(contentAlignment = Alignment.Center) {
                                RingColorPicker(
                                    color = { hsvColor },
                                    onColorChange = { hsvColor = it },
                                    thumb = {
                                        Canvas(
                                            Modifier
                                                .size(16.dp)
                                                .offset((-8).dp, (-8).dp)
                                        ) {
                                            val angle = hsvColor.hue * (PI / 180f).toFloat()
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
                                    modifier = Modifier
                                        .size(78.dp)
                                        .rotate(hsvColor.hue),
                                    color = { hsvColor },
                                    onColorChange = { hsvColor = it },
                                )
                            }
                        }
                    }

                    SampleSlider(
                        value = { hsvColor.hue },
                        onValueChange = { hsvColor = hsvColor.copy(hue = it) },
                        valueRange = 0f..359f,
                        text = "Hue",
                        brush = Brush.horizontalGradient(
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
                    )

                    SampleSlider(
                        value = { hsvColor.saturation },
                        onValueChange = { hsvColor = hsvColor.copy(saturation = it) },
                        valueRange = 0f..1f,
                        text = "Saturation",
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color.White,
                                Color.hsv(hsvColor.hue, 1f, hsvColor.value)
                            )
                        )
                    )

                    SampleSlider(
                        value = { hsvColor.value },
                        onValueChange = { hsvColor = hsvColor.copy(value = it) },
                        valueRange = 0f..1f,
                        text = "Value",
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color.Black,
                                Color.hsv(hsvColor.hue, hsvColor.saturation, 1f)
                            )
                        )
                    )

                    Button(
                        onClick = {
                            val h = (0..359).random().toFloat()
                            val s = (20..100).random().toFloat() / 100f

                            hsvColor = hsvColor.copy(h, s)
                        }
                    ) {
                        Icon(
                            imageVector = Refresh,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                        Text("Randomize")
                    }
                }
            }
        }
    }
}