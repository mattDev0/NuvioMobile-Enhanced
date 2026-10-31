"""Generate extra app icon colorways from the transparent master in the repo.

The original artwork under asset/ is not checked in, so this starts from the
lossless ic_splash_logo.webp and applies the recolor formula from
generate_logo_colorways.py. Output layout matches generate_app_icon_assets.py.

Needs Pillow and numpy. Android theme XML is written separately by this script,
which appends only the new styles and leaves existing ones untouched.
"""
from pathlib import Path
import json
import sys

import numpy as np
from PIL import Image

sys.path.insert(0, str(Path(__file__).resolve().parent))
from generate_app_icon_assets import (  # noqa: E402
    ANDROID_RES,
    ANDROID_SPLASH,
    ANDROID_THEMES,
    ANDROID_THEMES_V31,
    COMPOSE_DRAWABLE,
    DENSITIES,
    IOS_ASSETS,
    adaptive_icon_contents,
    expanded_logo,
    ios_contents,
    on_black,
    resized,
    save_webp,
    wordmark_for,
)

SOURCE = ANDROID_SPLASH / "ic_splash_logo.webp"

# key, iOS/Android class name, swatches, hues (from generate_logo_colorways.py)
NEW_VARIANTS = [
    ("electric_violet", "AppIconElectricViolet", ["#4985FF", "#704DFF", "#EA4DFF"], [218, 253, 293]),
    ("turquoise", "AppIconTurquoise", ["#38F2D0", "#12D6B3", "#19A7D8"], [169, 168, 195]),
    ("sunset", "AppIconSunset", ["#8D55FF", "#F044A7", "#FF7048"], [260, 324, 374]),
    ("magenta_rush", "AppIconMagentaRush", ["#6B5CFF", "#E640D5", "#FF4E87"], [248, 306, 339]),
    ("crimson", "AppIconCrimson", ["#FF5B91", "#F52E62", "#E42E3D"], [340, 346, 355]),
    ("solar", "AppIconSolar", ["#FF6B3D", "#FFAA2D", "#FFE35C"], [14, 36, 50]),
]

# Needed only by --verify, to compare against a colorway that already ships.
EMERALD = ("emerald", "AppIconEmerald", ["#7BF08D", "#22D37C", "#0BBF9A"], [127, 148, 168])


def rgb_to_hsv(rgb):
    maximum = rgb.max(axis=-1)
    minimum = rgb.min(axis=-1)
    delta = maximum - minimum
    safe_delta = np.where(delta == 0, 1, delta)
    red, green, blue = rgb[..., 0], rgb[..., 1], rgb[..., 2]
    hue = np.select(
        [maximum == red, maximum == green],
        [(green - blue) / safe_delta % 6, (blue - red) / safe_delta + 2],
        (red - green) / safe_delta + 4,
    ) * 60
    hue = np.where(delta == 0, 0, hue)
    saturation = np.where(maximum == 0, 0, delta / np.where(maximum == 0, 1, maximum))
    return hue, saturation, maximum


def hsv_to_rgb(hue, saturation, value):
    sector = (hue % 360) / 60
    chroma = value * saturation
    x = chroma * (1 - np.abs(sector % 2 - 1))
    zero = np.zeros_like(hue)
    index = np.floor(sector).astype(int) % 6
    red = np.choose(index, [chroma, x, zero, zero, x, chroma])
    green = np.choose(index, [x, chroma, chroma, x, zero, zero])
    blue = np.choose(index, [zero, zero, x, chroma, chroma, x])
    match = value - chroma
    return np.stack([red + match, green + match, blue + match], axis=-1)


def swatch_saturation_value(swatches):
    values = np.array(
        [[int(item[index:index + 2], 16) for index in (1, 3, 5)] for item in swatches],
        dtype=np.float64,
    ) / 255
    _, saturation, value = rgb_to_hsv(values)
    return saturation, value


def recolor(source, swatches, hues):
    pixels = np.array(source.convert("RGBA"), dtype=np.uint8)
    rgb = pixels[..., :3].astype(np.float64) / 255
    alpha = pixels[..., 3]
    hue, saturation, value = rgb_to_hsv(rgb)

    color_mask = (alpha > 0) & (saturation > 0.12) & (value > 0.22)
    position = np.clip((hue - 184) / 110, 0, 1)
    stops = np.linspace(0, 1, len(swatches))
    target_hue = np.interp(position, stops, hues) % 360
    swatch_saturation, swatch_value = swatch_saturation_value(swatches)
    target_saturation = np.interp(position, stops, swatch_saturation)
    target_value = np.interp(position, stops, swatch_value)
    saturation_factor = np.clip((saturation / 0.69) ** 0.35, 0.72, 1.16)
    value_factor = np.clip((value / 0.88) ** 0.65, 0.74, 1.1)

    mapped = hsv_to_rgb(
        np.where(color_mask, target_hue, hue),
        np.where(color_mask, np.clip(target_saturation * saturation_factor, 0, 1), saturation),
        np.where(color_mask, np.clip(target_value * value_factor, 0, 1), value),
    )
    mapped[(alpha > 0) & (value <= 0.22)] = 0
    pixels[..., :3] = np.rint(mapped * 255).astype(np.uint8)
    return Image.fromarray(pixels, mode="RGBA")


def write_variant(key, ios_name, transparent):
    expanded = expanded_logo(transparent, max_width_ratio=0.70, max_height_ratio=0.74)
    adaptive = expanded_logo(transparent, max_width_ratio=0.52, max_height_ratio=0.55)
    black = on_black(expanded)
    launcher_name = f"ic_launcher_{key}"
    foreground_name = f"{launcher_name}_foreground"

    save_webp(transparent, ANDROID_SPLASH / f"ic_splash_logo_{key}.webp")
    (ANDROID_RES / "mipmap-anydpi-v26" / f"{launcher_name}.xml").write_text(
        adaptive_icon_contents(foreground_name),
        encoding="utf-8",
    )
    resized(black, 256).save(COMPOSE_DRAWABLE / f"app_icon_{key}.png", "PNG", optimize=True)
    wordmark_for(transparent).save(
        COMPOSE_DRAWABLE / f"app_logo_wordmark_{key}.png",
        "PNG",
        optimize=True,
    )

    icon_set = IOS_ASSETS / f"{ios_name}.appiconset"
    icon_set.mkdir(parents=True, exist_ok=True)
    resized(black, 1024).save(icon_set / "app-icon-1024.png", "PNG", optimize=True)
    (icon_set / "Contents.json").write_text(
        json.dumps(ios_contents(), indent=2) + "\n",
        encoding="utf-8",
    )

    for density, scale in DENSITIES.items():
        directory = ANDROID_RES / f"mipmap-{density}"
        save_webp(resized(black, round(48 * scale)), directory / f"{launcher_name}.webp")
        save_webp(
            resized(adaptive, round(108 * scale)),
            directory / f"{foreground_name}.webp",
        )


def append_themes():
    for path, template in (
        (ANDROID_THEMES, '    <style name="Theme.Nuvio.Splash.{name}" parent="@style/Theme.Nuvio.Splash" />\n'),
        (
            ANDROID_THEMES_V31,
            '\n    <style name="Theme.Nuvio.Splash.{name}" parent="@style/Theme.Nuvio.Splash">\n'
            '        <item name="windowSplashScreenAnimatedIcon">@drawable/ic_splash_logo_{key}</item>\n'
            "    </style>\n",
        ),
    ):
        text = path.read_text(encoding="utf-8")
        additions = ""
        for key, ios_name, _, _ in NEW_VARIANTS:
            name = ios_name.removeprefix("AppIcon")
            if f"Theme.Nuvio.Splash.{name}\"" not in text:
                additions += template.format(name=name, key=key)
        path.write_text(text.replace("</resources>", additions + "</resources>"), encoding="utf-8")


def verify():
    key, _, swatches, hues = EMERALD
    source = Image.open(SOURCE)
    shipped = np.array(Image.open(ANDROID_SPLASH / f"ic_splash_logo_{key}.webp").convert("RGBA"), dtype=int)
    produced = np.array(recolor(source, swatches, hues), dtype=int)
    visible = shipped[..., 3] > 128
    difference = np.abs(shipped[..., :3] - produced[..., :3])[visible]
    print(f"emerald vs shipped: mean channel diff {difference.mean():.2f}/255, p99 {np.percentile(difference, 99):.0f}")


def main():
    if "--verify" in sys.argv:
        verify()
        return
    source = Image.open(SOURCE).convert("RGBA")
    for key, ios_name, swatches, hues in NEW_VARIANTS:
        write_variant(key, ios_name, recolor(source, swatches, hues))
    append_themes()


if __name__ == "__main__":
    main()
