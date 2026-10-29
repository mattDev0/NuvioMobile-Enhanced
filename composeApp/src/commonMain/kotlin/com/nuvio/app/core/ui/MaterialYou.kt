package com.nuvio.app.core.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

/** True when the device can supply wallpaper colours (Android 12+). Always false on iOS. */
expect fun isMaterialYouSupported(): Boolean

@Composable
expect fun platformDynamicDarkColorScheme(): ColorScheme?

val LocalMaterialYou = staticCompositionLocalOf { false }
