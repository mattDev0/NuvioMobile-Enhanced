package com.nuvio.app.core.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

actual fun isMaterialYouSupported(): Boolean = false

@Composable
actual fun platformDynamicDarkColorScheme(): ColorScheme? = null
