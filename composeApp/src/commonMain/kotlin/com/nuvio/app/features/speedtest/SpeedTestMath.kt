package com.nuvio.app.features.speedtest

import kotlin.math.abs
import kotlin.math.roundToLong
import kotlin.time.Duration

internal fun megabitsPerSecond(bytes: Long, elapsed: Duration): Double {
    val seconds = elapsed.inWholeMilliseconds / 1000.0
    return if (seconds <= 0.0) 0.0 else bytes * 8 / 1_000_000.0 / seconds
}

internal fun medianOf(values: List<Double>): Double? {
    if (values.isEmpty()) return null
    val sorted = values.sorted()
    val middle = sorted.size / 2
    return if (sorted.size % 2 == 1) sorted[middle] else (sorted[middle - 1] + sorted[middle]) / 2
}

/** Mean difference between consecutive samples. */
internal fun jitterOf(values: List<Double>): Double? =
    if (values.size < 2) null else values.zipWithNext { a, b -> abs(b - a) }.average()

/** One decimal below 100 Mbps, none above. String.format does not exist in common code. */
internal fun formatMbps(value: Double): String {
    if (value >= 100.0) return value.roundToLong().toString()
    val tenths = (value * 10).roundToLong()
    return "${tenths / 10}.${tenths % 10}"
}
