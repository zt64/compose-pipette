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

Compose-pipette comes with three different components for selecting colors: `ColorCircle`, `ColorSquare` and`ColorRing`.
Each component has a `thumb` parameter for passing a custom composable to be used for the thumb component.

#### Color Circle

The `ColorCircle` component is a simple circle that allows the user to select a color by dragging a point around the
circle. The circle allows for control over the hue and saturation of the color.

```kotlin
var color by remember { mutableStateOf(Color.Red) }

ColorCircle(
    color = color,
    onColorChange = { color = it }
)
```

#### Color Square

The `ColorSquare` component is a square that allows the user to select a color by dragging a point around the square.
The square allows for control over the saturation and value of the color.

> [!NOTE]
> Due to how Compose stores the color, it is not possible to use the `ColorSquare` component with the `Color`
> class. At low saturation and value values, `Color` fails to represent the color correctly.
> Instead, the hue, saturation and value components of the color need to be extracted and stored separately.

```kotlin
var hue by rememberSaveable { mutableStateOf(0f) }
var saturation by rememberSaveable { mutableStateOf(0f) }
var value by rememberSaveable { mutableStateOf(0f) }
var color = remember(hue, saturation, value) {
    Color.hsv(hue, saturation, value)
}

ColorSquare(
    hue = hue,
    saturation = saturation,
    value = value,
    onColorChange = { h, s, v ->
        hue = h
        saturation = s
        value = v
    }
)
```

#### Color Ring

The `ColorRing` component is a ring that allows the user to select a color by dragging a point around the ring.
Only the hue of the color can be changed with this component. For saturation control, use the `ColorCircle` component.

```kotlin
var color by remember { mutableStateOf(Color.Red) }

ColorRing(
    color = color,
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