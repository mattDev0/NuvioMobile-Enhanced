package com.nuvio.app.core.ui

import nuvio.composeapp.generated.resources.Res
import nuvio.composeapp.generated.resources.theme_amber
import nuvio.composeapp.generated.resources.theme_crimson
import nuvio.composeapp.generated.resources.theme_emerald
import nuvio.composeapp.generated.resources.theme_ocean
import nuvio.composeapp.generated.resources.theme_rose
import nuvio.composeapp.generated.resources.theme_midnight
import nuvio.composeapp.generated.resources.theme_forest
import nuvio.composeapp.generated.resources.theme_northern_lights
import nuvio.composeapp.generated.resources.theme_ember
import nuvio.composeapp.generated.resources.theme_sakura
import nuvio.composeapp.generated.resources.theme_sunset
import nuvio.composeapp.generated.resources.theme_neon
import nuvio.composeapp.generated.resources.theme_glacier
import nuvio.composeapp.generated.resources.theme_mocha
import nuvio.composeapp.generated.resources.theme_matcha
import nuvio.composeapp.generated.resources.theme_violet
import nuvio.composeapp.generated.resources.theme_white
import nuvio.composeapp.generated.resources.theme_gold
import nuvio.composeapp.generated.resources.theme_jade
import nuvio.composeapp.generated.resources.theme_rose_gold
import nuvio.composeapp.generated.resources.theme_arctic_blue
import nuvio.composeapp.generated.resources.theme_graphite
import nuvio.composeapp.generated.resources.theme_custom
import org.jetbrains.compose.resources.StringResource

enum class AppTheme {
    GOLD,
    JADE,
    ROSE_GOLD,
    ARCTIC_BLUE,
    GRAPHITE,
    CUSTOM,
    CRIMSON,
    OCEAN,
    VIOLET,
    EMERALD,
    AMBER,
    ROSE,
    MIDNIGHT,
    FOREST,
    NORTHERN_LIGHTS,
    EMBER,
    SAKURA,
    SUNSET,
    NEON,
    GLACIER,
    MOCHA,
    MATCHA,
    WHITE,
}

val AppTheme.labelRes: StringResource
    get() = when (this) {
        AppTheme.GOLD -> Res.string.theme_gold
        AppTheme.JADE -> Res.string.theme_jade
        AppTheme.ROSE_GOLD -> Res.string.theme_rose_gold
        AppTheme.ARCTIC_BLUE -> Res.string.theme_arctic_blue
        AppTheme.GRAPHITE -> Res.string.theme_graphite
        AppTheme.CUSTOM -> Res.string.theme_custom
        AppTheme.CRIMSON -> Res.string.theme_crimson
        AppTheme.OCEAN -> Res.string.theme_ocean
        AppTheme.VIOLET -> Res.string.theme_violet
        AppTheme.EMERALD -> Res.string.theme_emerald
        AppTheme.AMBER -> Res.string.theme_amber
        AppTheme.ROSE -> Res.string.theme_rose
        AppTheme.MIDNIGHT -> Res.string.theme_midnight
        AppTheme.FOREST -> Res.string.theme_forest
        AppTheme.NORTHERN_LIGHTS -> Res.string.theme_northern_lights
        AppTheme.EMBER -> Res.string.theme_ember
        AppTheme.SAKURA -> Res.string.theme_sakura
        AppTheme.SUNSET -> Res.string.theme_sunset
        AppTheme.NEON -> Res.string.theme_neon
        AppTheme.GLACIER -> Res.string.theme_glacier
        AppTheme.MOCHA -> Res.string.theme_mocha
        AppTheme.MATCHA -> Res.string.theme_matcha
        AppTheme.WHITE -> Res.string.theme_white
    }
