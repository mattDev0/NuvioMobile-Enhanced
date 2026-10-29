package com.nuvio.app.core.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Typography
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import nuvio.composeapp.generated.resources.Res
import nuvio.composeapp.generated.resources.jetbrains_sans_bold
import nuvio.composeapp.generated.resources.jetbrains_sans_regular
import nuvio.composeapp.generated.resources.jetbrains_sans_semibold
import org.jetbrains.compose.resources.Font

val LocalAppTheme = staticCompositionLocalOf { AppTheme.WHITE }
val LocalThemePalette = staticCompositionLocalOf { ThemeColors.White }

val MaterialTheme.themePalette: ThemeColorPalette
    @Composable
    @ReadOnlyComposable
    get() = LocalThemePalette.current

val MaterialTheme.appTheme: AppTheme
    @Composable
    @ReadOnlyComposable
    get() = LocalAppTheme.current

private fun contentColorFor(background: Color): Color =
    if (background.luminance() > 0.5f) Color(0xFF111111) else Color(0xFFF5F7F8)

private fun buildColorScheme(palette: ThemeColorPalette, amoled: Boolean = false) = darkColorScheme(
    primary = palette.secondary,
    onPrimary = palette.onSecondary,
    primaryContainer = palette.focusBackground,
    onPrimaryContainer = contentColorFor(palette.focusBackground),
    secondary = palette.secondaryVariant,
    onSecondary = palette.onSecondaryVariant,
    background = if (amoled) Color.Black else palette.background,
    onBackground = Color(0xFFF5F7F8),
    surface = palette.backgroundElevated,
    onSurface = Color(0xFFF5F7F8),
    surfaceVariant = palette.backgroundCard,
    onSurfaceVariant = Color(0xFF969CA3),
    outline = Color(0xFF252A2A),
    error = Color(0xFFE36A8A),
    onError = Color(0xFFFCE5EC),
)

private val JetBrainsSans: FontFamily
    @Composable
    get() = FontFamily(
        Font(Res.font.jetbrains_sans_bold, FontWeight.Bold, FontStyle.Normal),
        Font(Res.font.jetbrains_sans_semibold, FontWeight.SemiBold, FontStyle.Normal),
        Font(Res.font.jetbrains_sans_regular, FontWeight.Normal, FontStyle.Normal),
    )

private val NuvioTypography: Typography
    @Composable
    get() = Typography(
        displayLarge = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = NuvioTokens.Type.pageDisplay,
            lineHeight = NuvioTokens.LineHeight.pageDisplay,
            fontWeight = FontWeight.Bold,
            letterSpacing = NuvioTokens.LetterSpacing.pageDisplay,
        ),
        headlineLarge = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = NuvioTokens.Type.headline,
            lineHeight = NuvioTokens.LineHeight.headline,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = NuvioTokens.LetterSpacing.headline,
        ),
        titleLarge = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = NuvioTokens.Type.titleSm,
            lineHeight = NuvioTokens.LineHeight.materialTitleLarge,
            fontWeight = FontWeight.SemiBold,
        ),
        titleMedium = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = NuvioTokens.Type.bodyLg,
            lineHeight = NuvioTokens.LineHeight.bodyMd,
            fontWeight = FontWeight.SemiBold,
        ),
        bodyLarge = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = NuvioTokens.Type.bodyApp,
            lineHeight = NuvioTokens.LineHeight.bodyApp,
            fontWeight = FontWeight.Normal,
        ),
        bodyMedium = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = NuvioTokens.Type.bodyMd,
            lineHeight = NuvioTokens.LineHeight.bodyMd,
            fontWeight = FontWeight.Normal,
        ),
        labelLarge = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = NuvioTokens.Type.bodyMd,
            lineHeight = NuvioTokens.LineHeight.bodySm,
            fontWeight = FontWeight.SemiBold,
        ),
        labelMedium = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = NuvioTokens.Type.labelSm,
            lineHeight = NuvioTokens.LineHeight.labelXs,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = NuvioTokens.LetterSpacing.label,
        ),
    )

private val MaterialYouTypography: Typography
    @Composable
    get() = Typography(
        displayLarge = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = 57.sp,
            lineHeight = 64.sp,
            fontWeight = FontWeight.Bold,
        ),
        displayMedium = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = 45.sp,
            lineHeight = 52.sp,
            fontWeight = FontWeight.Bold,
        ),
        displaySmall = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            fontWeight = FontWeight.Bold,
        ),
        headlineLarge = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight.Bold,
        ),
        headlineMedium = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.Bold,
        ),
        headlineSmall = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        titleLarge = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        titleMedium = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        titleSmall = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        bodyLarge = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal,
        ),
        bodyMedium = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal,
        ),
        bodySmall = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Normal,
        ),
        labelLarge = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        labelMedium = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        labelSmall = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Medium,
        ),
    )

private val NuvioTypeTokens: NuvioTypeScale
    @Composable
    get() = NuvioTypeScale(
        labelXs = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = NuvioTokens.Type.labelXs,
            lineHeight = NuvioTokens.LineHeight.labelXs,
            fontWeight = FontWeight.SemiBold,
        ),
        labelSm = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = NuvioTokens.Type.labelSm,
            lineHeight = NuvioTokens.LineHeight.labelSm,
            fontWeight = FontWeight.SemiBold,
        ),
        bodySm = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = NuvioTokens.Type.bodySm,
            lineHeight = NuvioTokens.LineHeight.bodySm,
            fontWeight = FontWeight.Normal,
        ),
        bodyMd = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = NuvioTokens.Type.bodyMd,
            lineHeight = NuvioTokens.LineHeight.bodyMd,
            fontWeight = FontWeight.Normal,
        ),
        bodyLg = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = NuvioTokens.Type.bodyLg,
            lineHeight = NuvioTokens.LineHeight.bodyLg,
            fontWeight = FontWeight.Normal,
        ),
        titleSm = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = NuvioTokens.Type.titleSm,
            lineHeight = NuvioTokens.LineHeight.titleSm,
            fontWeight = FontWeight.SemiBold,
        ),
        titleMd = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = NuvioTokens.Type.titleMd,
            lineHeight = NuvioTokens.LineHeight.titleMd,
            fontWeight = FontWeight.SemiBold,
        ),
        titleLg = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = NuvioTokens.Type.titleLg,
            lineHeight = NuvioTokens.LineHeight.titleLg,
            fontWeight = FontWeight.SemiBold,
        ),
        displaySm = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = NuvioTokens.Type.displaySm,
            lineHeight = NuvioTokens.LineHeight.displaySm,
            fontWeight = FontWeight.Bold,
        ),
        displayMd = TextStyle(
            fontFamily = JetBrainsSans,
            fontSize = NuvioTokens.Type.displayMd,
            lineHeight = NuvioTokens.LineHeight.displayMd,
            fontWeight = FontWeight.Bold,
        ),
    )

private val NuvioRippleConfiguration = RippleConfiguration(
    color = Color.Black,
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NuvioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    appTheme: AppTheme = AppTheme.WHITE,
    amoled: Boolean = false,
    customThemeColors: CustomThemeColors = CustomThemeColors.Default,
    materialYou: Boolean = false,
    content: @Composable () -> Unit,
) {
    val dynamicScheme = if (materialYou && isMaterialYouSupported()) {
        platformDynamicDarkColorScheme()
    } else {
        null
    }
    val isMaterialYouActive = dynamicScheme != null

    val (palette, colorScheme) = if (isMaterialYouActive) {
        val effectiveDynamicScheme = if (amoled) {
            dynamicScheme!!.copy(
                background = Color.Black,
                surface = Color.Black,
            )
        } else {
            dynamicScheme!!
        }
        val pal = ThemeColorPalette.fromMaterialYou(effectiveDynamicScheme, amoled = amoled)
        pal to effectiveDynamicScheme
    } else {
        val pal = remember(appTheme, customThemeColors) {
            ThemeColors.getColorPalette(appTheme, customThemeColors)
        }
        val scheme = buildColorScheme(pal, amoled = amoled)
        pal to scheme
    }

    val tokens = defaultNuvioThemeTokens(palette, amoled = amoled, colorScheme = colorScheme, materialYou = isMaterialYouActive)

    val density = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides Density(
            density = density.density,
            fontScale = 1f,
        ),
        LocalNuvioThemeTokens provides tokens,
        LocalNuvioTypeScale provides NuvioTypeTokens,
        LocalRippleConfiguration provides NuvioRippleConfiguration,
        LocalAppTheme provides appTheme,
        LocalThemePalette provides palette,
        LocalMaterialYou provides isMaterialYouActive,
    ) {
        if (isMaterialYouActive) {
            MaterialExpressiveTheme(
                colorScheme = colorScheme,
                motionScheme = MotionScheme.expressive(),
                shapes = MaterialYouExpressiveShapes,
                typography = MaterialYouTypography,
            ) {
                SkeletonAnimationProvider(content = content)
            }
        } else {
            MaterialTheme(
                colorScheme = colorScheme,
                typography = NuvioTypography,
            ) {
                SkeletonAnimationProvider(content = content)
            }
        }
    }
}
