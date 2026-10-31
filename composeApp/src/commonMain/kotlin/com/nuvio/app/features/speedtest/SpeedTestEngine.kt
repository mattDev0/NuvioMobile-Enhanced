package com.nuvio.app.features.speedtest

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.prepareGet
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.utils.io.readAvailable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.io.IOException

private const val BASE_URL = "https://speed.cloudflare.com"
private const val CONNECTIONS = 4
private const val LATENCY_SAMPLES = 6
private const val DOWNLOAD_CHUNK_BYTES = 10_000_000L
private const val UPLOAD_CHUNK_BYTES = 1_000_000
// The server blocks an IP for about an hour (429) after heavy use, so each run stays small.
private const val MAX_DOWNLOAD_BYTES = 150_000_000L
private const val MAX_UPLOAD_BYTES = 50_000_000L
private const val HTTP_TOO_MANY_REQUESTS = 429
private val DOWNLOAD_WINDOW = 8.seconds
private val UPLOAD_WINDOW = 6.seconds
// TCP slow start would drag the average down, so the first moment is not counted.
private val GRACE_PERIOD = 1.seconds
private val SAMPLE_INTERVAL = 200.milliseconds

internal data class LatencyResult(val latencyMs: Double?, val jitterMs: Double?)

internal class SpeedTestRateLimitedException : IOException("Speed test rate limited")

private fun checkStatus(status: HttpStatusCode) {
    if (status.value == HTTP_TOO_MANY_REQUESTS) throw SpeedTestRateLimitedException()
    if (!status.isSuccess()) throw IOException("HTTP ${status.value}")
}

internal class SpeedTestEngine(
    private val client: HttpClient,
    private val baseUrl: String = BASE_URL,
) {

    suspend fun measureLatency(): LatencyResult {
        val samples = List(LATENCY_SAMPLES) {
            val mark = TimeSource.Monotonic.markNow()
            checkStatus(client.get("$baseUrl/__down?bytes=0").status)
            mark.elapsedNow().inWholeMicroseconds / 1000.0
        }.drop(1) // The first request includes the TLS handshake.
        return LatencyResult(medianOf(samples), jitterOf(samples))
    }

    suspend fun measureDownload(onProgress: (Double) -> Unit): Double? =
        measure(DOWNLOAD_WINDOW, MAX_DOWNLOAD_BYTES, onProgress) { transferred ->
            client.prepareGet("$baseUrl/__down?bytes=$DOWNLOAD_CHUNK_BYTES").execute { response ->
                checkStatus(response.status)
                val channel = response.bodyAsChannel()
                val buffer = ByteArray(64 * 1024)
                while (true) {
                    val count = channel.readAvailable(buffer, 0, buffer.size)
                    if (count == -1) break
                    if (count == 0) {
                        channel.awaitContent()
                        continue
                    }
                    transferred.update { it + count }
                }
            }
        }

    suspend fun measureUpload(onProgress: (Double) -> Unit): Double? {
        val payload = ByteArray(UPLOAD_CHUNK_BYTES)
        return measure(UPLOAD_WINDOW, MAX_UPLOAD_BYTES, onProgress) { transferred ->
            val response = client.post("$baseUrl/__up") {
                contentType(ContentType.Application.OctetStream)
                setBody(payload)
            }
            checkStatus(response.status)
            transferred.update { it + payload.size }
        }
    }

    /**
     * Runs [CONNECTIONS] looping workers and returns the average speed after the grace period.
     * Hitting the rate limit part way through keeps what was measured; it throws only if
     * nothing was.
     */
    private suspend fun measure(
        window: Duration,
        maxBytes: Long,
        onProgress: (Double) -> Unit,
        worker: suspend (MutableStateFlow<Long>) -> Unit,
    ): Double? = coroutineScope {
        val transferred = MutableStateFlow(0L)
        val rateLimited = MutableStateFlow(false)
        val mark = TimeSource.Monotonic.markNow()
        val workers = List(CONNECTIONS) {
            launch {
                try {
                    while (isActive && transferred.value < maxBytes) worker(transferred)
                } catch (_: SpeedTestRateLimitedException) {
                    rateLimited.value = true
                }
            }
        }
        var measureStart: Duration? = null
        var bytesAtStart = 0L
        var speed: Double? = null
        while (mark.elapsedNow() < window && transferred.value < maxBytes && !rateLimited.value) {
            delay(SAMPLE_INTERVAL)
            val elapsed = mark.elapsedNow()
            val start = measureStart
            if (start == null) {
                if (elapsed >= GRACE_PERIOD) {
                    measureStart = elapsed
                    bytesAtStart = transferred.value
                }
            } else if (transferred.value > bytesAtStart) {
                val current = megabitsPerSecond(transferred.value - bytesAtStart, elapsed - start)
                speed = current
                onProgress(current)
            }
        }
        val total = mark.elapsedNow()
        workers.forEach { it.cancel() }
        // A link fast enough to reach the byte cap within the grace period never produced a sample.
        val result = speed ?: transferred.value.takeIf { it > 0 }?.let { megabitsPerSecond(it, total) }
        if (result == null && rateLimited.value) throw SpeedTestRateLimitedException()
        result
    }
}
