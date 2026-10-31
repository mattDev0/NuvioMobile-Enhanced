package com.nuvio.app.features.debrid

import io.ktor.util.date.GMTDate
import kotlin.math.roundToInt

// Premiumize reports fair use as a 0..1 fraction of a rolling 30-day allowance of 1,000 points.
internal const val PREMIUMIZE_FAIR_USE_POINTS = 1_000

// One fair-use point covers about 1 GB at the base fair-use factor. Hosts with a higher
// factor spend points faster, so these figures are estimates, not metered traffic.
internal const val PREMIUMIZE_ESTIMATED_GB_PER_POINT = 1

private const val SECONDS_PER_DAY = 86_400L

data class PremiumizeAccountUsage(
    val customerId: String?,
    /** Fraction of the fair-use allowance consumed, clamped to 0..1. */
    val fairUseUsedFraction: Float,
    val boosterPoints: Int,
    /** Premium expiry in epoch seconds, or null for free/expired accounts. */
    val premiumUntilEpochSeconds: Long?,
) {
    val fairUseRemainingFraction: Float
        get() = 1f - fairUseUsedFraction

    val fairUsePointsUsed: Int
        get() = (fairUseUsedFraction * PREMIUMIZE_FAIR_USE_POINTS).roundToInt()

    val fairUsePointsRemaining: Int
        get() = PREMIUMIZE_FAIR_USE_POINTS - fairUsePointsUsed

    val estimatedTotalBandwidthGb: Int
        get() = PREMIUMIZE_FAIR_USE_POINTS * PREMIUMIZE_ESTIMATED_GB_PER_POINT

    val estimatedBandwidthUsedGb: Int
        get() = fairUsePointsUsed * PREMIUMIZE_ESTIMATED_GB_PER_POINT

    val estimatedBandwidthRemainingGb: Int
        get() = fairUsePointsRemaining * PREMIUMIZE_ESTIMATED_GB_PER_POINT

    fun premiumDaysLeft(nowEpochMs: Long): Int? =
        premiumUntilEpochSeconds?.let { until ->
            ((until - nowEpochMs / 1000).coerceAtLeast(0L) / SECONDS_PER_DAY).toInt()
        }

    /** Expiry as an ISO yyyy-MM-dd date (UTC), suitable for the shared release-date formatter. */
    fun premiumUntilIsoDate(): String? =
        premiumUntilEpochSeconds?.let { until ->
            val date = GMTDate(until * 1000)
            "${date.year}-${(date.month.ordinal + 1).pad2()}-${date.dayOfMonth.pad2()}"
        }
}

sealed interface PremiumizeAccountUsageResult {
    data class Success(val usage: PremiumizeAccountUsage) : PremiumizeAccountUsageResult
    data class Failure(val message: String?) : PremiumizeAccountUsageResult
}

internal fun premiumizeAccountUsageFromDto(dto: PremiumizeAccountInfoDto): PremiumizeAccountUsage =
    PremiumizeAccountUsage(
        customerId = dto.customerId?.takeIf { it.isNotBlank() },
        fairUseUsedFraction = (dto.limitUsed ?: 0.0).toFloat().coerceIn(0f, 1f),
        boosterPoints = (dto.boosterPoints ?: 0).coerceAtLeast(0),
        premiumUntilEpochSeconds = dto.premiumUntil?.takeIf { it > 0L },
    )

internal fun premiumizeAccountUsageResult(
    response: DebridApiResponse<PremiumizeAccountInfoDto>,
): PremiumizeAccountUsageResult {
    val body = response.body
    return if (response.isSuccessful && body != null && body.status.equals("success", ignoreCase = true)) {
        PremiumizeAccountUsageResult.Success(premiumizeAccountUsageFromDto(body))
    } else {
        PremiumizeAccountUsageResult.Failure(body?.message?.takeIf { it.isNotBlank() })
    }
}

object PremiumizeAccountUsageLoader {
    suspend fun load(apiKey: String): PremiumizeAccountUsageResult {
        val normalized = apiKey.trim()
        if (normalized.isBlank()) return PremiumizeAccountUsageResult.Failure(null)
        return try {
            premiumizeAccountUsageResult(PremiumizeApiClient.accountInfo(normalized))
        } catch (error: kotlin.coroutines.cancellation.CancellationException) {
            throw error
        } catch (error: Exception) {
            PremiumizeAccountUsageResult.Failure(error.message)
        }
    }
}

private fun Int.pad2(): String = toString().padStart(2, '0')
