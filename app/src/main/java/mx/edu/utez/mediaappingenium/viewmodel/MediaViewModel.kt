// --- 7. MediaViewModel —
package mx.edu.utez.mediaappingenium.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mx.edu.utez.mediaappingenium.data.model.MediaItem
import mx.edu.utez.mediaappingenium.data.model.MediaType
import mx.edu.utez.mediaappingenium.data.repository.MediaRepository
import java.io.File

class MediaViewModel(application: Application, private val repository: MediaRepository) : AndroidViewModel(application) {
    val allAudio: StateFlow<List<MediaItem>> = repository.getAllAudio()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    val allImages: StateFlow<List<MediaItem>> = repository.getAllImages()
        .stateIn(

            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    val allVideos: StateFlow<List<MediaItem>> = repository.getAllVideos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    // Inserta un nuevo medio en la BD. Esta función extrae los metadatos de la Uri.
    fun insertMediaFromUri(uri: Uri, type: MediaType) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                val metadata = getMetadataFromUri(context.contentResolver, uri)
                val item = MediaItem(
                    uri = uri.toString(),
                    name = metadata.first,
                    date = System.currentTimeMillis(),
                    duration = metadata.second,
                    type = type
                )
                repository.insertMedia(item)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun insertMediaFromFile(file: File, type: MediaType) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                val authority = "${context.packageName}.fileprovider"
                // Obtener la content:// Uri segura para este archivo
                val uri = FileProvider.getUriForFile(context, authority, file)
                // Ahora que tenemos una Uri, podemos usar la lógica existente
                insertMediaFromUri(uri, type)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    // Helper para obtener nombre y duración de una Uri. @return Pair<Nombre, Duración en ms>
    private fun getMetadataFromUri(contentResolver: ContentResolver, uri: Uri): Pair<String, Long> {
        var aName = "Unknown"
        var aDuration = 0L
        // Obtener nombre
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    aName = cursor.getString(nameIndex)
                }
            }
        }
        // Obtener duración (solo para audio/video)
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(getApplication<Application>(), uri)
            val durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            aDuration = durationString?.toLongOrNull() ?: 0L
            retriever.release()
        } catch (e: Exception) {
            aDuration = 0L // Fallback
        }
        return Pair(aName, aDuration)
    }
}