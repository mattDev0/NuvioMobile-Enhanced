package com.nuvio.app.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.DataUsage
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nuvio.app.core.format.formatReleaseDateForDisplay
import com.nuvio.app.core.ui.NuvioLoadingIndicator
import com.nuvio.app.core.ui.nuvio
import com.nuvio.app.features.debrid.PREMIUMIZE_FAIR_USE_POINTS
import com.nuvio.app.features.debrid.PremiumizeAccountUsage
import com.nuvio.app.features.debrid.PremiumizeAccountUsageLoader
import com.nuvio.app.features.debrid.PremiumizeAccountUsageResult
import io.ktor.util.date.GMTDate
import kotlin.math.roundToInt
import nuvio.composeapp.generated.resources.Res
import nuvio.composeapp.generated.resources.action_retry
import nuvio.composeapp.generated.resources.settings_premiumize_usage_booster_points
import nuvio.composeapp.generated.resources.settings_premiumize_usage_total_bandwidth
import nuvio.composeapp.generated.resources.settings_premiumize_usage_bandwidth_used
import nuvio.composeapp.generated.resources.settings_premiumize_usage_bandwidth_gb
import nuvio.composeapp.generated.resources.settings_premiumize_usage_bandwidth_note
import nuvio.composeapp.generated.resources.settings_premiumize_usage_days_left
import nuvio.composeapp.generated.resources.settings_premiumize_usage_expired
import nuvio.composeapp.generated.resources.settings_premiumize_usage_failed
import nuvio.composeapp.generated.resources.settings_premiumize_usage_fair_use_remaining
import nuvio.composeapp.generated.resources.settings_premiumize_usage_low_warning
import nuvio.composeapp.generated.resources.settings_premiumize_usage_percent_summary
import nuvio.composeapp.generated.resources.settings_premiumize_usage_points
import nuvio.composeapp.generated.resources.settings_premiumize_usage_premium_until
import nuvio.composeapp.generated.resources.settings_premiumize_usage_refresh
import nuvio.composeapp.generated.resources.settings_premiumize_usage_title
import nuvio.composeapp.generated.resources.settings_premiumize_usage_used
import org.jetbrains.compose.resources.stringResource

// Below this share of fair use left, the bar turns to the warning colour and a hint is shown.
private const val LOW_FAIR_USE_REMAINING_FRACTION = 0.15f

private sealed interface PremiumizeUsageState {
    data object Loading : PremiumizeUsageState
    data class Loaded(val usage: PremiumizeAccountUsage) : PremiumizeUsageState
    data class Failed(val message: String?) : PremiumizeUsageState
}

@Composable
internal fun PremiumizeUsageSection(
    isTablet: Boolean,
    apiKey: String,
) {
    var refreshToken by remember { mutableIntStateOf(0) }
    var state by remember(apiKey) { mutableStateOf<PremiumizeUsageState>(PremiumizeUsageState.Loading) }

    LaunchedEffect(apiKey, refreshToken) {
        state = PremiumizeUsageState.Loading
        state = when (val result = PremiumizeAccountUsageLoader.load(apiKey)) {
            is PremiumizeAccountUsageResult.Success -> PremiumizeUsageState.Loaded(result.usage)
            is PremiumizeAccountUsageResult.Failure -> PremiumizeUsageState.Failed(result.message)
        }
    }

    SettingsSection(
        title = stringResource(Res.string.settings_premiumize_usage_title),
        isTablet = isTablet,
        actions = {
            IconButton(
                onClick = { refreshToken++ },
                enabled = state !is PremiumizeUsageState.Loading,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = stringResource(Res.string.settings_premiumize_usage_refresh),
                    tint = MaterialTheme.nuvio.colors.textMuted,
                )
            }
        },
    ) {
        SettingsGroup(isTablet = isTablet) {
            val padding = if (isTablet) 20.dp else 16.dp
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                when (val current = state) {
                    PremiumizeUsageState.Loading -> Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(96.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        NuvioLoadingIndicator(size = 28.dp)
                    }
                    is PremiumizeUsageState.Failed -> PremiumizeUsageError(
                        message = current.message,
                        onRetry = { refreshToken++ },
                    )
                    is PremiumizeUsageState.Loaded -> PremiumizeUsageContent(usage = current.usage)
                }
            }
        }
    }
}

@Composable
private fun PremiumizeUsageContent(usage: PremiumizeAccountUsage) {
    val tokens = MaterialTheme.nuvio
    val remainingFraction = usage.fairUseRemainingFraction
    val isLow = remainingFraction < LOW_FAIR_USE_REMAINING_FRACTION
    val barColor = if (isLow) tokens.colors.warning else tokens.colors.accent

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(Res.string.settings_premiumize_usage_fair_use_remaining),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = tokens.colors.textMuted,
        )
        Text(
            text = "${usage.fairUsePointsRemaining} / $PREMIUMIZE_FAIR_USE_POINTS",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = barColor,
        )
    }
    LinearProgressIndicator(
        progress = { remainingFraction },
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(999.dp)),
        color = barColor,
        trackColor = tokens.colors.borderSubtle,
    )
    val remainingPercent = (remainingFraction * 100f).roundToInt()
    Text(
        text = stringResource(
            Res.string.settings_premiumize_usage_percent_summary,
            remainingPercent,
            100 - remainingPercent,
        ),
        style = MaterialTheme.typography.bodySmall,
        color = tokens.colors.textMuted,
    )
    if (isLow) {
        Text(
            text = stringResource(Res.string.settings_premiumize_usage_low_warning),
            style = MaterialTheme.typography.bodySmall,
            color = tokens.colors.warning,
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PremiumizeStatTile(
            modifier = Modifier.weight(1f),
            icon = Icons.Rounded.Speed,
            label = stringResource(Res.string.settings_premiumize_usage_used),
            value = stringResource(Res.string.settings_premiumize_usage_points, usage.fairUsePointsUsed),
        )
        PremiumizeStatTile(
            modifier = Modifier.weight(1f),
            icon = Icons.Rounded.AutoAwesome,
            label = stringResource(Res.string.settings_premiumize_usage_booster_points),
            value = usage.boosterPoints.toString(),
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PremiumizeStatTile(
            modifier = Modifier.weight(1f),
            icon = Icons.Rounded.CloudDownload,
            label = stringResource(Res.string.settings_premiumize_usage_total_bandwidth),
            value = stringResource(
                Res.string.settings_premiumize_usage_bandwidth_gb,
                usage.estimatedTotalBandwidthGb,
            ),
        )
        PremiumizeStatTile(
            modifier = Modifier.weight(1f),
            icon = Icons.Rounded.DataUsage,
            label = stringResource(Res.string.settings_premiumize_usage_bandwidth_used),
            value = stringResource(
                Res.string.settings_premiumize_usage_bandwidth_gb,
                usage.estimatedBandwidthUsedGb,
            ),
        )
    }
    Text(
        text = stringResource(Res.string.settings_premiumize_usage_bandwidth_note),
        style = MaterialTheme.typography.bodySmall,
        color = tokens.colors.textMuted,
    )

    val premiumDate = usage.premiumUntilIsoDate()
    val daysLeft = usage.premiumDaysLeft(nowEpochMs = GMTDate().timestamp)
    Text(
        text = if (premiumDate != null && daysLeft != null && daysLeft > 0) {
            stringResource(
                Res.string.settings_premiumize_usage_premium_until,
                formatReleaseDateForDisplay(premiumDate),
            ) + " · " + stringResource(Res.string.settings_premiumize_usage_days_left, daysLeft)
        } else {
            stringResource(Res.string.settings_premiumize_usage_expired)
        },
        style = MaterialTheme.typography.bodyMedium,
        color = if (daysLeft != null && daysLeft > 0) tokens.colors.textSecondary else tokens.colors.danger,
    )
}

@Composable
private fun PremiumizeStatTile(
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

@Composable
private fun PremiumizeUsageError(
    message: String?,
    onRetry: () -> Unit,
) {
    val tokens = MaterialTheme.nuvio
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = message ?: stringResource(Res.string.settings_premiumize_usage_failed),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = tokens.colors.textMuted,
        )
        TextButton(onClick = onRetry) {
            Text(stringResource(Res.string.action_retry))
        }
    }
}
