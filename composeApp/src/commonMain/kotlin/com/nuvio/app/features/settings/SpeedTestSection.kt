package com.nuvio.app.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nuvio.app.core.ui.NuvioPrimaryButton
import com.nuvio.app.core.ui.nuvio
import com.nuvio.app.features.speedtest.SpeedTestPhase
import com.nuvio.app.features.speedtest.SpeedTestRunner
import com.nuvio.app.features.speedtest.formatMbps
import kotlin.math.roundToInt
import nuvio.composeapp.generated.resources.Res
import nuvio.composeapp.generated.resources.settings_speed_test_again
import nuvio.composeapp.generated.resources.settings_speed_test_data_note
import nuvio.composeapp.generated.resources.settings_speed_test_download
import nuvio.composeapp.generated.resources.settings_speed_test_failed
import nuvio.composeapp.generated.resources.settings_speed_test_latency_summary
import nuvio.composeapp.generated.resources.settings_speed_test_mbps
import nuvio.composeapp.generated.resources.settings_speed_test_phase_download
import nuvio.composeapp.generated.resources.settings_speed_test_phase_latency
import nuvio.composeapp.generated.resources.settings_speed_test_phase_upload
import nuvio.composeapp.generated.resources.settings_speed_test_rate_limited
import nuvio.composeapp.generated.resources.settings_speed_test_start
import nuvio.composeapp.generated.resources.settings_speed_test_stop
import nuvio.composeapp.generated.resources.settings_speed_test_title
import nuvio.composeapp.generated.resources.settings_speed_test_upload
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SpeedTestSection(isTablet: Boolean) {
    val state by SpeedTestRunner.state.collectAsStateWithLifecycle()
    val tokens = MaterialTheme.nuvio

    SettingsSection(
        title = stringResource(Res.string.settings_speed_test_title),
        isTablet = isTablet,
    ) {
        SettingsGroup(isTablet = isTablet) {
            val padding = if (isTablet) 20.dp else 16.dp
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    val downloadText = state.downloadMbps?.let {
                        stringResource(Res.string.settings_speed_test_mbps, formatMbps(it))
                    } ?: "—"
                    SpeedTestStatTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Rounded.ArrowDownward,
                        label = stringResource(Res.string.settings_speed_test_download),
                        value = downloadText,
                    )
                    val uploadText = state.uploadMbps?.let {
                        stringResource(Res.string.settings_speed_test_mbps, formatMbps(it))
                    } ?: "—"
                    SpeedTestStatTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Rounded.ArrowUpward,
                        label = stringResource(Res.string.settings_speed_test_upload),
                        value = uploadText,
                    )
                }

                val latencyStr = state.latencyMs?.roundToInt()?.toString() ?: "—"
                val jitterStr = state.jitterMs?.roundToInt()?.toString() ?: "—"
                Text(
                    text = stringResource(Res.string.settings_speed_test_latency_summary, latencyStr, jitterStr),
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.colors.textMuted,
                )

                val phaseText = when (state.phase) {
                    SpeedTestPhase.Latency -> stringResource(Res.string.settings_speed_test_phase_latency)
                    SpeedTestPhase.Download -> stringResource(Res.string.settings_speed_test_phase_download)
                    SpeedTestPhase.Upload -> stringResource(Res.string.settings_speed_test_phase_upload)
                    SpeedTestPhase.Failed -> if (state.rateLimited) {
                        stringResource(Res.string.settings_speed_test_rate_limited)
                    } else {
                        stringResource(Res.string.settings_speed_test_failed)
                    }
                    SpeedTestPhase.Idle, SpeedTestPhase.Done -> null
                }

                if (phaseText != null) {
                    Text(
                        text = phaseText,
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.colors.textMuted,
                    )
                }

                val buttonText = when {
                    state.isRunning -> stringResource(Res.string.settings_speed_test_stop)
                    state.phase == SpeedTestPhase.Done || state.phase == SpeedTestPhase.Failed ->
                        stringResource(Res.string.settings_speed_test_again)
                    else -> stringResource(Res.string.settings_speed_test_start)
                }

                NuvioPrimaryButton(
                    text = buttonText,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (state.isRunning) {
                            SpeedTestRunner.stop()
                        } else {
                            SpeedTestRunner.start()
                        }
                    },
                )

                Text(
                    text = stringResource(Res.string.settings_speed_test_data_note),
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.colors.textMuted,
                )
            }
        }
    }
}

@Composable
private fun SpeedTestStatTile(
    modifier: Modifier,
    icon: ImageVector,
    label: String,
    value: String,
) {
    val tokens = MaterialTheme.nuvio
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(tokens.colors.overlaySelected)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = tokens.colors.textMuted,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = tokens.colors.textMuted,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = tokens.colors.textPrimary,
        )
    }
}
