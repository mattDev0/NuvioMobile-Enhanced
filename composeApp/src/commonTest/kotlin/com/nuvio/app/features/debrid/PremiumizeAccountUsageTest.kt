package com.nuvio.app.features.debrid

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class PremiumizeAccountUsageTest {
    @Test
    fun `maps fair use fraction to points`() {
        val usage = premiumizeAccountUsageFromDto(
            PremiumizeAccountInfoDto(status = "success", limitUsed = 0.08, boosterPoints = 3),
        )

        assertEquals(80, usage.fairUsePointsUsed)
        assertEquals(920, usage.fairUsePointsRemaining)
        assertEquals(3, usage.boosterPoints)
    }

    @Test
    fun `clamps out of range values`() {
        val over = premiumizeAccountUsageFromDto(PremiumizeAccountInfoDto(limitUsed = 1.4, boosterPoints = -2))
        val under = premiumizeAccountUsageFromDto(PremiumizeAccountInfoDto(limitUsed = -0.1))

        assertEquals(0, over.fairUsePointsRemaining)
        assertEquals(0, over.boosterPoints)
        assertEquals(PREMIUMIZE_FAIR_USE_POINTS, under.fairUsePointsRemaining)
    }

    @Test
    fun `computes premium expiry date and days left`() {
        // 2026-11-27T00:00:00Z
        val usage = premiumizeAccountUsageFromDto(PremiumizeAccountInfoDto(premiumUntil = 1_795_737_600L))
        // 2026-10-28T00:00:00Z
        val now = 1_793_145_600_000L

        assertEquals("2026-11-27", usage.premiumUntilIsoDate())
        assertEquals(30, usage.premiumDaysLeft(now))
        assertEquals(0, usage.premiumDaysLeft(now + 60L * 86_400_000L))
    }

    @Test
    fun `treats missing or zero expiry as no premium`() {
        val usage = premiumizeAccountUsageFromDto(PremiumizeAccountInfoDto(premiumUntil = 0L))

        assertNull(usage.premiumUntilIsoDate())
        assertNull(usage.premiumDaysLeft(0L))
    }

    @Test
    fun `maps api error envelope to failure with message`() {
        val result = premiumizeAccountUsageResult(
            DebridApiResponse(
                status = 200,
                body = PremiumizeAccountInfoDto(status = "error", message = "Not logged in."),
                rawBody = "",
            ),
        )

        assertIs<PremiumizeAccountUsageResult.Failure>(result)
        assertEquals("Not logged in.", result.message)
    }

    @Test
    fun `maps successful response`() {
        val result = premiumizeAccountUsageResult(
            DebridApiResponse(
                status = 200,
                body = PremiumizeAccountInfoDto(status = "success", customerId = "123", limitUsed = 0.5),
                rawBody = "",
            ),
        )

        assertIs<PremiumizeAccountUsageResult.Success>(result)
        assertEquals(500, result.usage.fairUsePointsRemaining)
    }
}
