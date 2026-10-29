package com.nuvio.app.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NuvioProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = NuvioTokens.Space.s4,
    trackColor: Color = MaterialTheme.nuvio.colors.playerTimelineTrack,
    fillColor: Color = MaterialTheme.nuvio.colors.playerTimelineFill,
) {
    val clampedProgress = progress.coerceIn(0f, 1f)
    if (LocalMaterialYou.current) {
        LinearWavyProgressIndicator(
            progress = { clampedProgress },
            modifier = modifier
                .fillMaxWidth()
                .height(height.coerceAtLeast(10.dp)),
            color = if (fillColor == MaterialTheme.nuvio.colors.playerTimelineFill) MaterialTheme.colorScheme.primary else fillColor,
            trackColor = if (trackColor == MaterialTheme.nuvio.colors.playerTimelineTrack) MaterialTheme.colorScheme.surfaceContainerHighest else trackColor,
        )
    } else {
        val tokens = MaterialTheme.nuvio
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(height)
                .clip(tokens.shapes.chip)
                .background(trackColor),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(clampedProgress)
                    .width(NuvioTokens.Space.none)
                    .height(height)
                    .clip(tokens.shapes.chip)
                    .background(fillColor),
            )
        }
    }
}
