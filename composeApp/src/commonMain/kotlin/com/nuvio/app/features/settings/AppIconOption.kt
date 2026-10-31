package com.nuvio.app.features.settings

internal enum class AppIconOption(
    val key: String,
    val platformName: String?,
) {
    ORIGINAL(
        key = "original",
        platformName = null,
    ),
    ARCTIC_BLUE(
        key = "arctic_blue",
        platformName = "AppIconArcticBlue",
    ),
    EMERALD(
        key = "emerald",
        platformName = "AppIconEmerald",
    ),
    ROSE_GOLD(
        key = "rose_gold",
        platformName = "AppIconRoseGold",
    ),
    COPPER(
        key = "copper",
        platformName = "AppIconCopper",
    ),
    GRAPHITE(
        key = "graphite",
        platformName = "AppIconGraphite",
    ),
    ELECTRIC_VIOLET(
        key = "electric_violet",
        platformName = "AppIconElectricViolet",
    ),
    TURQUOISE(
        key = "turquoise",
        platformName = "AppIconTurquoise",
    ),
    SUNSET(
        key = "sunset",
        platformName = "AppIconSunset",
    ),
    MAGENTA_RUSH(
        key = "magenta_rush",
        platformName = "AppIconMagentaRush",
    ),
    CRIMSON(
        key = "crimson",
        platformName = "AppIconCrimson",
    ),
    SOLAR(
        key = "solar",
        platformName = "AppIconSolar",
    ),
    ;

    companion object {
        fun fromPlatformName(name: String?): AppIconOption =
            entries.firstOrNull { it.platformName == name } ?: ORIGINAL
    }
}
