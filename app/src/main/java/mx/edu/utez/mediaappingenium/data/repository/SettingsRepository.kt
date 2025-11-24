// --- 5. SettingsRepository (control del volumen guardando con DataStore) —
package mx.edu.utez.mediaappingenium.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensión para crear la instancia de DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(context: Context) {
    private val dataStore = context.dataStore
    companion object {
        val VOLUME_KEY = floatPreferencesKey("volume_level")
        const val DEFAULT_VOLUME = 0.5f
    }
    // Obtener el volumen guardado
    val userVolume: Flow<Float> = dataStore.data.map { preferences ->
        preferences[VOLUME_KEY] ?: DEFAULT_VOLUME
    }

    // Guardar el volumen
    suspend fun saveVolume(volume: Float) {
        dataStore.edit { settings ->
            val clampedVolume = volume.coerceIn(0.0f, 1.0f)
            settings[VOLUME_KEY] = clampedVolume
        }
    }
}