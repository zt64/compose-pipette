# 🎨 compose-pipette

[![Maven Central Version](https://img.shields.io/maven-central/v/dev.zt64.compose.pipette/compose-pipette)](https://central.sonatype.com/artifact/dev.zt64.compose.pipette/compose-pipette)

A multiplatform color picker for Kotlin Compose, featuring components for selecting colors. Designed to be minimal and
dependency-free for easy integration into your project.

Supported platforms:

- JVM
- Android
- Kotlin/JS
- Kotlin/WASM
- macOS
- iOS

## Styles

<details>
<summary>Color Circle</summary>

```kotlin
var color = remember { mutableStateOf(Color.Red) }

ColorCircle(
    color = color,
    onColorChange = { color = it }
)
```

</details>

<details>
<summary>Color Square</summary>

```kotlin
var color = remember { mutableStateOf(Color.Red) }

ColorSquare(
    color = color,
    onColorChange = { color = it }
)
```

</details>

<details>
<summary>Color Ring</summary>

```kotlin
var color = remember { mutableStateOf(Color.Red) }

ColorRing(
    color = color,
    onColorChange = { color = it }
)
```

</details>

## Setup

To use the library, add the following to your `build.gradle.kts`:

```toml
[versions]
composePipette = "x.y.z"

[libraries]
composePipette = { module = "dev.zt64.compose.pipette:compose-pipette", version.ref = "composePipette" }
```

## License

Compose-pipette is an open source project licensed under the [MIT license](LICENSE).