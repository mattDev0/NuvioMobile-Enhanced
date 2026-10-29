package com.nuvio.app.core.ui

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

actual fun isMaterialYouSupported(): Boolean = Build.VERSION.SDK_INT >= 31

@Composable
actual fun platformDynamicDarkColorScheme(): ColorScheme? {
    return if (Build.VERSION.SDK_INT >= 31) {
        dynamicDarkColorScheme(LocalContext.current)
    } else {
        null
    }
}
