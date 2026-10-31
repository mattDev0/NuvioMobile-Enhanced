package com.nuvio.app.features.speedtest

import com.nuvio.app.core.network.createApiHttpClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal enum class SpeedTestPhase { Idle, Latency, Download, Upload, Done, Failed }

internal data class SpeedTestState(
    val phase: SpeedTestPhase = SpeedTestPhase.Idle,
    val latencyMs: Double? = null,
    val jitterMs: Double? = null,
    val downloadMbps: Double? = null,
    val uploadMbps: Double? = null,
    val rateLimited: Boolean = false,
) {
    val isRunning: Boolean
        get() = phase == SpeedTestPhase.Latency ||
            phase == SpeedTestPhase.Download ||
            phase == SpeedTestPhase.Upload
}

internal object SpeedTestRunner {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val engine by lazy { SpeedTestEngine(createApiHttpClient()) }
    private val _state = MutableStateFlow(SpeedTestState())
    private var job: Job? = null

    val state: StateFlow<SpeedTestState> = _state.asStateFlow()

    fun start() {
        if (job?.isActive == true) return
        job = scope.launch {
            try {
                _state.value = SpeedTestState(phase = SpeedTestPhase.Latency)
                val latency = engine.measureLatency()
                _state.update {
                    it.copy(
                        phase = SpeedTestPhase.Download,
                        latencyMs = latency.latencyMs,
                        jitterMs = latency.jitterMs,
                    )
                }
                val download = engine.measureDownload { mbps ->
                    _state.update { it.copy(downloadMbps = mbps) }
                }
                _state.update { it.copy(phase = SpeedTestPhase.Upload, downloadMbps = download) }
                val upload = engine.measureUpload { mbps ->
                    _state.update { it.copy(uploadMbps = mbps) }
                }
                _state.update { it.copy(phase = SpeedTestPhase.Done, uploadMbps = upload) }
            } catch (error: CancellationException) {
                throw error
            } catch (_: SpeedTestRateLimitedException) {
                _state.update { it.copy(phase = SpeedTestPhase.Failed, rateLimited = true) }
            } catch (error: Exception) {
                _state.update { it.copy(phase = SpeedTestPhase.Failed) }
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
        _state.value = SpeedTestState()
    }
}
