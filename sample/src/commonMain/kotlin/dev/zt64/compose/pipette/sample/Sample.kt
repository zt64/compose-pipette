package dev.zt64.compose.pipette.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwitchLeft
import androidx.compose.material.icons.filled.SwitchRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import dev.zt64.compose.pipette.*

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
                                    Icons.Default.SwitchLeft
                                } else {
                                    Icons.Default.SwitchRight
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
                                    color = hsvColor,
                                    onColorChange = { hsvColor = it }
                                )

                                RgbField(
                                    color = hsvColor,
                                    onColorChange = { hsvColor = it }
                                )

                                HsvField(
                                    hsvColor = hsvColor,
                                    onColorChange = { hsvColor = it }
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .fillMaxHeight()
                                    .background(hsvColor.toColor(), MaterialTheme.shapes.medium)
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("CircularColorPicker")

                            Spacer(Modifier.height(6.dp))

                            CircularColorPicker(
                                color = hsvColor,
                                onColorChange = { hsvColor = it }
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("SquareColorPicker")

                            Spacer(Modifier.height(6.dp))

                            SquareColorPicker(
                                color = hsvColor,
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
                                color = hsvColor,
                                onColorChange = { hsvColor = it }
                            )
                        }
                    }

                    SampleSlider(
                        value = hsvColor.hue,
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
                        value = hsvColor.saturation,
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
                        value = hsvColor.value,
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
                            imageVector = Icons.Default.Refresh,
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