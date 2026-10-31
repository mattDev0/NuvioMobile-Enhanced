package com.nuvio.app.features.speedtest

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class SpeedTestMathTest {

    @Test
    fun megabitsPerSecondComputesBandwidthAndHandlesZeroElapsed() {
        assertEquals(100.0, megabitsPerSecond(12_500_000L, 1.seconds))
        assertEquals(0.0, megabitsPerSecond(12_500_000L, Duration.ZERO))
    }

    @Test
    fun medianOfHandlesEmptyOddAndEvenLists() {
        assertNull(medianOf(emptyList()))
        assertEquals(20.0, medianOf(listOf(10.0, 30.0, 20.0)))
        assertEquals(25.0, medianOf(listOf(10.0, 20.0, 30.0, 40.0)))
    }

    @Test
    fun jitterOfComputesMeanConsecutiveDifference() {
        assertEquals(10.0, jitterOf(listOf(10.0, 20.0, 10.0)))
        assertNull(jitterOf(listOf(10.0)))
        assertNull(jitterOf(emptyList()))
    }

    @Test
    fun formatMbpsFormatsCorrectly() {
        assertEquals("87.3", formatMbps(87.26))
        assertEquals("124", formatMbps(123.6))
        assertEquals("0.0", formatMbps(0.04))
    }
}
