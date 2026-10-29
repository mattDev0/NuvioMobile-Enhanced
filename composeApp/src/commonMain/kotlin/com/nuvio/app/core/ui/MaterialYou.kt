package com.nuvio.app.core.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/** True when the device can supply wallpaper colours (Android 12+). Always false on iOS. */
expect fun isMaterialYouSupported(): Boolean

@Composable
expect fun platformDynamicDarkColorScheme(): ColorScheme?

val LocalMaterialYou = staticCompositionLocalOf { false }

val MaterialYouExpressiveShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun nuvioShape(default: Shape, materialYou: Shape): Shape =
    if (LocalMaterialYou.current) materialYou else default

