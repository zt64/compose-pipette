# 🎨 compose-pipette

[![Maven Central Version](https://img.shields.io/maven-central/v/dev.zt64.compose.pipette/compose-pipette)](https://central.sonatype.com/artifact/dev.zt64.compose.pipette/compose-pipette)

A multiplatform color picker for Kotlin Compose, featuring components for selecting colors. Designed to be minimal and
dependency-free for easy integration into your project. A live demo can be
found [here](https://zt64.github.io/compose-pipette/).

Supported platforms:

- JVM
- Android (minimum API level 21)
- Kotlin/JS
- Kotlin/WASM
- macOS (untested)
- iOS (untested)

## Styles

Compose-pipette comes with three different components for selecting colors: `CircularColorPicker`, `SquareColorPicker`
and `RingColorPicker`. Each component has a `thumb` parameter for passing a custom composable to be used for the thumb
component.

#### Circular Color Picker

The `CircularColorPicker` component is a simple circle that allows the user to select a color by dragging a point around
the
circle. The circle allows for control over the hue and saturation of the color.

```kotlin
var color by remember { mutableStateOf(HsvColor(Color.Red)) }

CircularColorPicker(
    color = { color },
    onColorChange = { color = it }
)
```

#### Square Color Picker

The `SquareColorPicker` component is a square that allows the user to select a color by dragging a point around the
square.
The square allows for control over the saturation and value of the color.

```kotlin
var color by remember { mutableStateOf(HsvColor(Color.Red)) }

SquareColorPicker(
    color = { color },
    onColorChange = { color = it },
    shape = RoundedCornerShape(8.dp),
)
```

#### Ring Color Picker

The `RingColorPicker` component is a ring that allows the user to select a color by dragging a point around the ring.
Only the hue of the color can be changed with this component. For saturation control, use the `CircularColorPicker`
component.

```kotlin
var color by remember { mutableStateOf(HsvColor(Color.Red)) }

RingColorPicker(
    color = { color },
    onColorChange = { color = it }
)
```

## Setup

To use the library, add the following to your version catalog:

```toml
[versions]
composePipette = "x.y.z"

[libraries]
composePipette = { module = "dev.zt64.compose.pipette:compose-pipette", version.ref = "composePipette" }
```

## License

Compose-pipette is an open source project licensed under the [MIT license](LICENSE).