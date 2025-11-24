// --- 9. PlaybackViewModel---
package mx.edu.utez.mediaappingenium.viewmodel

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mx.edu.utez.mediaappingenium.data.repository.SettingsRepository

class PlaybackViewModel(
    application: Application,
    private val settingsRepository: SettingsRepository
) : AndroidViewModel(application), SensorEventListener {
    // --- ExoPlayer ---
    val exoPlayer: ExoPlayer = ExoPlayer.Builder(application).build()
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    // --- Sensores (Acelerómetro) ---
    private val sensorManager: SensorManager =
        getSystemService(application, SensorManager::class.java) as SensorManager
    private var accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val _isAccelerometerEnabled = MutableStateFlow(false)
    val isAccelerometerEnabled: StateFlow<Boolean> = _isAccelerometerEnabled.asStateFlow()
    // --- DataStore (Volumen) ---
    val currentVolume: StateFlow<Float> = settingsRepository.userVolume
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsRepository.DEFAULT_VOLUME
        )
    init {
        // Observar el volumen de DataStore y aplicarlo a ExoPlayer
        viewModelScope.launch {
            currentVolume.collect { volume ->
                exoPlayer.volume = volume
            }
        }
        // Observar el estado de reproducción de ExoPlayer
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }
        })
    }
    // --- Funciones de Reproducción ---
    fun playMedia(uri: String) {
        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }
    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()

        }
    }
    fun releasePlayer() {
        exoPlayer.release()
        unregisterSensorListener() // Asegurarse de desactivar el sensor
    }
    // --- Funciones de Acelerómetro ---
    fun toggleAccelerometer() {
        _isAccelerometerEnabled.update { isEnabled ->
            if (!isEnabled) {
                registerSensorListener()
            } else {
                unregisterSensorListener()
            }
            !isEnabled
        }
    }
    private fun registerSensorListener() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }
    private fun unregisterSensorListener() {
        sensorManager.unregisterListener(this)
    }
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            // Usamos el eje Y (inclinación lateral)
            val yValue = event.values[1]
            // Mapeamos el valor del sensor (aprox -9.8 a 9.8) a un volumen (0.0 a 1.0)
            // Usaremos un rango más pequeño para que no sea tan sensible
            // ej. de -5 (inclinado a la izquierda) a +5 (inclinado a la derecha)
            val newVolume = (yValue + 5f) / 10f
            val clampedVolume = newVolume.coerceIn(0.0f, 1.0f)
            // Aplicar el volumen
            setVolume(clampedVolume)
        }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No es necesario
    }
    // --- Funciones de Volumen ---
    fun setVolume(volume: Float) {
        val clampedVolume = volume.coerceIn(0.0f, 1.0f)
        exoPlayer.volume = clampedVolume
        // Guardar en DataStore
        viewModelScope.launch {
            settingsRepository.saveVolume(clampedVolume)
        }
    }
    override fun onCleared() {
        super.onCleared()
        releasePlayer() // Liberar recursos
    }
}